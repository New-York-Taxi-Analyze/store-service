package com.newyorktaxi.storeservice.repository;

import com.newyorktaxi.storeservice.entity.FailureMessage;
import com.newyorktaxi.storeservice.entity.StatusEnum;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailureMessageRepository extends CrudRepository<FailureMessage, Long> {

    List<FailureMessage> findAllByStatus(StatusEnum status);
}
