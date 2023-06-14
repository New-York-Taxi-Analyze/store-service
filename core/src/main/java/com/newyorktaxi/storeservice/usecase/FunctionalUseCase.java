package com.newyorktaxi.storeservice.usecase;

import jakarta.validation.Valid;

public interface FunctionalUseCase<T, R> {

    R execute(@Valid T params);
}
