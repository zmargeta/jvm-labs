package dev.margeta.lab.graphql.spring.config

import dev.margeta.lab.graphql.spring.config.component.DateCoercing
import graphql.schema.GraphQLScalarType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer

@Configuration(proxyBeanMethods = false)
class ApiConfiguration {
    @Bean
    fun runtimeWiringConfigurer(scalars: List<GraphQLScalarType>) = RuntimeWiringConfigurer {
        scalars
            .fold(it) { builder, type -> builder.scalar(type) }
            .build()
    }

    @Bean
    fun dateGraphQLScalarType(): GraphQLScalarType =
        GraphQLScalarType
            .newScalar()
            .name("Date")
            .description("ISO-8061 date compliant Scalar")
            .coercing(DateCoercing)
            .build()
}
