package pl.cinkus.backend.exception;

import graphql.GraphQLError;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GraphQlExceptionHandler {
    @org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
    public GraphQLError handle(Exception e) {
        return GraphQLError.newError()
                .message(e.getMessage())
                .errorType(ErrorType.INTERNAL_ERROR)
                .build();
    }
}