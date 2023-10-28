package com.bs.krxstockservice.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QueryDslConfig(
    @PersistenceContext
    private val entityManager: EntityManager? = null,
) {
    @Bean
    fun jpaQueryFactory(): JPAQueryFactory{
        return JPAQueryFactory(this.entityManager)
    }
}