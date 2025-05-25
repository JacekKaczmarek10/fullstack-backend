package com.kaczmarek.fullstackbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    value = "com.kaczmarek.fullstackbackend.repository",
    considerNestedRepositories = true)
public class RepositoryConfiguration {}
