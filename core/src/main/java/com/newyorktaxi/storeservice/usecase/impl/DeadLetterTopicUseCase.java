package com.newyorktaxi.storeservice.usecase.impl;

import com.newyorktaxi.storeservice.entity.FailureMessage;
import com.newyorktaxi.storeservice.mapper.FailureMessageMapper;
import com.newyorktaxi.storeservice.repository.FailureMessageRepository;
import com.newyorktaxi.storeservice.usecase.FunctionalUseCase;
import com.newyorktaxi.storeservice.usecase.params.FailureMessageParams;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeadLetterTopicUseCase implements FunctionalUseCase<FailureMessageParams, Void> {

    FailureMessageRepository repository;
    FailureMessageMapper mapper;

    @Override
    public Void execute(FailureMessageParams params) {
        log.info("Saving message to dead letter topic");

        final FailureMessage failureMessage = mapper.toFailureMessage(params);
        repository.save(failureMessage);

        log.info("Message saved to dead letter topic: {}", failureMessage);
        return null;
    }
}
