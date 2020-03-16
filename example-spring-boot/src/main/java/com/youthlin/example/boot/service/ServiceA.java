package com.youthlin.example.boot.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(0)
public class ServiceA implements IMyService {
    @Override
    public void doSomeThing() {

    }
}
