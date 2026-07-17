# requester-service

Microsserviço de **gerenciamento de solicitantes** ("requesters"), escrito em **Java 21 + Spring Boot 4**. Permite cadastrar solicitantes e valida se um solicitante existe e está ativo — usado pelo [`order-service`](https://github.com/itau-desafio-tecnico/order-service/blob/main/README.md) como pré-condição para a criação de ordens de serviço.

## Sumário

- [Escopo](#escopo)
- [Stack de tecnologia](#stack-de-tecnologia)
- [Arquitetura](#arquitetura)
- [Fluxos](#fluxos)
- [Endpoints](#endpoints)
- [Configuração](#configuração)
- [Como executar](#como-executar)
- [Testes](#testes)
- [CI/CD](#cicd)

## Escopo

O `requester-service` mantém o cadastro de **solicitantes** (`Requester`): a entidade/pessoa autorizada a solicitar ordens de serviço no sistema. Cada solicitante possui um documento (CPF de 11 dígitos ou CNPJ de 14 dígitos), nome, e-mail e um estado `active`.

O serviço expõe:

- Criação de solicitantes, com garantia de unicidade por documento.
- Consulta de um solicitante por ID.
- Um endpoint de **validação** (`.../{id}/validation`), consumido pelo `order-service`, que informa se o solicitante existe e está ativo — sem nunca retornar erro 404 (retorna `validation: false` quando não encontrado).

Este serviço não possui filas/tópicos de mensageria — é um provedor REST puramente síncrono.

## Stack de tecnologia

| Categoria | Tecnologia |
|---|---|
| Linguagem/runtime | Java 21 |
| Framework | Spring Boot 4.1.0 (`spring-boot-starter-webmvc`) |
| Build | Maven (via Maven Wrapper `mvnw`) |
| Banco de dados | PostgreSQL |
| ORM | Spring Data JPA |
| Migrações | Liquibase (changelogs YAML em `src/main/resources/db/changelog`) |
| Validação | Jakarta Bean Validation (`spring-boot-starter-validation`) |
| Documentação de API | springdoc-openapi (Swagger UI) 2.8.6 |
| Observabilidade | Spring Boot Actuator, Micrometer Tracing (bridge OpenTelemetry), Micrometer Prometheus |
| Testes | JUnit 5, Mockito, AssertJ, `@WebMvcTest`/`MockMvc` |
| Cobertura | JaCoCo, gate de 80% de linhas |
| Licença | ver `LICENSE` |

Não há mensageria (Kafka/RabbitMQ/SQS/SNS) nem cache neste serviço.

## Arquitetura

O projeto segue **arquitetura hexagonal (ports & adapters)**, com o domínio isolado de frameworks. Pacotes sob `src/main/java/com/itau/desafio/requesterservice/`:

```
domain/
├── model/Requester.java              # Record com as invariantes de negócio
├── repository/RequesterRepository.java  # Port (interface) de persistência
└── exception/                         # RequesterAlreadyExistsException, RequesterNotFoundException

app/usecase/                          # Casos de uso — dependem apenas dos ports do domínio
├── CreateRequesterUseCase.java
├── GetRequesterUseCase.java
└── ValidateRequesterUseCase.java

infra/
├── persistence/                       # Adapter de persistência
│   ├── RequesterEntity.java              # Entidade JPA
│   ├── RequesterJpaRepository.java       # Spring Data JpaRepository
│   └── RequesterRepositoryImpl.java      # Implementa o port RequesterRepository
└── config/
    ├── UseCaseConfig.java                # Wiring manual dos beans de caso de uso
    ├── ObservabilityConfig.java          # Exclui /actuator/health do tracing
    └── OpenApiConfig.java                # Metadados do Swagger

interfaces/rest/                      # Adapter de entrada (HTTP)
├── RequesterController.java
├── GlobalExceptionHandler.java          # @RestControllerAdvice
└── dto/                                  # RequesterRequest, RequesterResponse, ValidationResponse, ErrorResponse
```

O `domain` não tem dependências de framework; `app/usecase` depende só dos ports do domínio; `infra`/`interfaces` são adapters plugados via configuração Spring. Os casos de uso são POJOs simples, instanciados manualmente em `UseCaseConfig` (sem `@Service` direto nas classes de caso de uso), mantendo a camada de aplicação agnóstica de framework.

## Fluxos

### Criação de solicitante

`POST /jv-requester-service/requesters` → `RequesterController` → `CreateRequesterUseCase`:
1. Verifica duplicidade de documento via `RequesterRepository.existsByDocument`.
2. Se já existir, lança `RequesterAlreadyExistsException` → `409 Conflict`.
3. Caso contrário, cria o `Requester` (UUID gerado, `active=true`) e persiste.
4. Responde `201 Created` com header `Location` apontando para `GET .../requesters/{id}`.

O documento é mascarado nos logs (exibe apenas os dois últimos dígitos).

### Consulta por ID

`GET /jv-requester-service/requesters/{id}` → `GetRequesterUseCase.byId` → `404 Not Found` (`RequesterNotFoundException`) se não existir, ou o solicitante encontrado.

### Validação de solicitante (fluxo consumido pelo order-service)

`GET /jv-requester-service/requesters/{id}/validation` → `ValidateRequesterUseCase.execute(id)` → retorna `{requesterId, validation}`, onde `validation` é `true` apenas se o solicitante existir **e** estiver ativo (`false` em qualquer outro caso, sem erro 404).

Este é o endpoint chamado pelo `order-service` (`HttpRequesterClient.exists`) antes de criar uma ordem, com retry (3 tentativas, backoff exponencial) em caso de falha de transporte.

## Endpoints

Prefixo de contexto: **`/jv-requester-service`**.

| Método | Path | Request | Resposta | Status |
|---|---|---|---|---|
| `POST` | `/jv-requester-service/requesters` | `{document, name, email}` | `{id, document, name, email, active, createdAt}` | `201`, `400` (validação), `409` (documento já existe) |
| `GET` | `/jv-requester-service/requesters/{id}` | — | `{id, document, name, email, active, createdAt}` | `200`, `404` |
| `GET` | `/jv-requester-service/requesters/{id}/validation` | — | `{requesterId, validation}` | `200` (`validation=false` se inexistente/inativo) |

Erros seguem o formato `{code, message, timestamp}`, com códigos `REQUESTER_NOT_FOUND` (404), `REQUESTER_ALREADY_EXISTS` (409), `INVALID_REQUEST`/`VALIDATION` (400) e `INTERNAL_ERROR` (500), centralizados em `GlobalExceptionHandler`.

Endpoints adicionais (Actuator/OpenAPI): `/actuator/health`, `/actuator/info`, `/actuator/prometheus`, `/actuator/metrics`, `/v3/api-docs`, `/swagger-ui.html`.

Não há tópicos/filas de mensageria neste serviço.

## Configuração

Variáveis de ambiente (ver `src/main/resources/application.yml`):

| Variável | Padrão | Descrição |
|---|---|---|
| `DB_HOST` | `localhost` | Host do PostgreSQL |
| `DB_PORT` | `5434` | Porta do PostgreSQL (distinta da porta usada pelo `order-service`, para evitar conflito em execução local simultânea) |
| `DB_NAME` | `requesters` | Nome do banco |
| `DB_USER` | `requester` | Usuário do banco |
| `DB_PASSWORD` | `requester` | Senha do banco |
| `OTLP_COLLECTOR_ENDPOINT` | `http://localhost:4318/v1/traces` | Endpoint OTLP para traces |

Outras configurações fixas relevantes: `server.port=8081`, `server.servlet.context-path=/jv-requester-service`, `spring.jpa.hibernate.ddl-auto=validate` (schema controlado exclusivamente pelo Liquibase), `management.tracing.sampling.probability=1.0`.

## Como executar

### Via Docker

```bash
./mvnw clean package -DskipTests
docker build -t requester-service .
docker run -p 8081:8081 --env DB_HOST=... --env DB_PORT=... requester-service
```

Este repositório não possui `docker-compose.yml` próprio — para rodar junto com o [`order-service`](https://github.com/itau-desafio-tecnico/order-service/blob/main/README.md), disponibilize um PostgreSQL acessível pelas variáveis `DB_*` (ex.: em uma rede Docker compartilhada) e aponte o `order-service` para `http://requester-service:8081/jv-requester-service` via `REQUESTER_SERVICE_URL`.

### Local (sem Docker)

```bash
./mvnw spring-boot:run
```

Requer um PostgreSQL acessível em `localhost:5434/requesters` (ou conforme variáveis `DB_*`). O schema é criado automaticamente pelo Liquibase na subida da aplicação.

## Testes

```bash
./mvnw verify
```

- Frameworks: JUnit 5, Mockito, AssertJ; testes de controller via `@WebMvcTest` + `MockMvc`.
- Gate de cobertura: 80% de linhas via JaCoCo (`mvn verify`), excluindo classe principal, `infra/config/**` e `interfaces/rest/dto/**`.
- Estrutura em `src/test/java/...`, espelhando o layout hexagonal do `src/main` (`domain`, `app/usecase`, `infra/persistence`, `interfaces/rest`).

## CI/CD

Workflow `.github/workflows/ci.yml`: roda `mvn verify` (testes + gate de cobertura) em push/PR; em push para `main`, constrói e publica a imagem Docker no Amazon ECR (`desafio-dev-requester-service`, tags `latest` e SHA do commit) e força um novo deployment do serviço `requester-service` no cluster ECS `desafio-dev-cluster`.
