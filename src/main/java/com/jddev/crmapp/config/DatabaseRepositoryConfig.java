package com.jddev.crmapp.config;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.aop.support.AopUtils;
import org.springframework.data.repository.Repository;
import org.springframework.util.Assert;



@Configuration
public class DatabaseRepositoryConfig {
    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public Map<Class<?>, JpaRepository<?, ?>> repositoryMap() {
        Map<Class<?>, JpaRepository<?, ?>> repositoryMap = new HashMap<>();

        // Get all beans of type JpaRepository
        Map<String, JpaRepository> repositories = applicationContext.getBeansOfType(JpaRepository.class);

        for (JpaRepository<?, ?> repository : repositories.values()) {
            Class<?> entityClass = getRepositoryClass(repository);
            repositoryMap.put(entityClass, repository);
        }

        repositoryMap.forEach((key, value) -> {
            System.out.println("Class: " + key.getSimpleName() + ", Repository: " + value.getClass().getInterfaces()[0].getSimpleName());
        });

        return repositoryMap;
    }

    public Class<?> getRepositoryClass(JpaRepository<?, ?> repository) {
        return repository.getClass().getInterfaces()[0];
    }
}
