package com.newyorktaxi.storeservice.scheduler;

import com.newyorktaxi.storeservice.usecase.FunctionalUseCase;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RetryFailedMessage {

    FunctionalUseCase<Void, Void> retryFailedMessageUseCase;

    @Scheduled(cron = "${scheduler.retry-failed-message.cron-expression}")
    @SchedulerLock(
        name = "RetryFailedMessage_retry",
        lockAtMostFor = "${scheduler.retry-failed-message.lock-at-most-for}",
        lockAtLeastFor = "${scheduler.retry-failed-message.lock-at-least-for}")
    public void retry() {
        retryFailedMessageUseCase.execute(null);
    }
}
