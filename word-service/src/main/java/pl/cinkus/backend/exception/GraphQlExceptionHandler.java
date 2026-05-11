package pl.cinkus.backend.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Map;

@ControllerAdvice
public class GraphQlExceptionHandler {
    @org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
    public GraphQLError handle(WordServiceException e) {
        return GraphqlErrorBuilder.newError()
                .message(e.getMessage())
                .errorType(ErrorType.BAD_REQUEST)
                .extensions(Map.of("errorCode", e.getErrorCode().name()))
                .build();
    }

    @org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
    public GraphQLError handle(Exception e) {
        return GraphqlErrorBuilder.newError()
                .message(e.getMessage())
                .errorType(ErrorType.INTERNAL_ERROR)
                .extensions(Map.of("errorCode", ErrorType.INTERNAL_ERROR.name()))
                .build();
    }
}