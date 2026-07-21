package com.itau.desafio.requesterservice.infra.config;

import com.itau.desafio.requesterservice.app.usecase.CreateRequesterUseCase;
import com.itau.desafio.requesterservice.app.usecase.GetRequesterUseCase;
import com.itau.desafio.requesterservice.app.usecase.ListRequestersUseCase;
import com.itau.desafio.requesterservice.app.usecase.ValidateRequesterUseCase;
import com.itau.desafio.requesterservice.domain.repository.RequesterRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateRequesterUseCase createRequesterUseCase(RequesterRepository requesterRepository){
        return new CreateRequesterUseCase(requesterRepository);
    }

    @Bean
    public GetRequesterUseCase getRequesterUseCase(RequesterRepository requesterRepository){
        return new GetRequesterUseCase(requesterRepository);
    }

    @Bean
    public ValidateRequesterUseCase validateRequesterUseCase(RequesterRepository requesterRepository){
        return new ValidateRequesterUseCase(requesterRepository);
    }

    @Bean
    public ListRequestersUseCase listRequestersUseCase(RequesterRepository requesterRepository){
        return new ListRequestersUseCase(requesterRepository);
    }
}
