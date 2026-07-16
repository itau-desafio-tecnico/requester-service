package com.itau.desafio.requesterservice.infra.config;

import com.itau.desafio.requesterservice.app.usecase.CreateRequesterUseCase;
import com.itau.desafio.requesterservice.domain.repository.RequesterRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateRequesterUseCase createRequesterUseCase(RequesterRepository requesterRepository){
        return new CreateRequesterUseCase(requesterRepository);
    }
}
