package pl.cinkus.IdentityService.exception;

import graphql.GraphQLError;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Map;

@ControllerAdvice
public class GraphQlExceptionHandler {
    @org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
    public GraphQLError handleIdentityServiceException(IdentityServiceException exception) {
        return GraphQLError.newError()
                .message(exception.getMessage())
                .errorType(ErrorType.BAD_REQUEST)
                .extensions(Map.of("errorCode", exception.getErrorCode().name()))
                .build();
    }

    public GraphQLError handleOtherException(Exception e) {
        e.printStackTrace();

        return GraphQLError.newError()
                .message("Unknown Server Error")
                .errorType(ErrorType.INTERNAL_ERROR)
                .extensions(Map.of("errorCode", ErrorType.INTERNAL_ERROR.name()))
                .build();
    }

}
