package it.univr.passportease.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import it.univr.passportease.exception.illegalstate.OfficeOverloadedException;
import it.univr.passportease.exception.illegalstate.UserAlreadyExistsException;
import it.univr.passportease.exception.invalid.*;
import it.univr.passportease.exception.notfound.*;
import it.univr.passportease.exception.security.RateLimitException;
import org.jetbrains.annotations.NotNull;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {
    private static GraphQLError notFoundError(@NotNull Throwable exception, @NotNull DataFetchingEnvironment environment) {
        return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.NOT_FOUND)
                .message(exception.getMessage())
                .path(environment.getExecutionStepInfo().getPath())
                .location(environment.getField().getSourceLocation())
                .build();
    }

    private static GraphQLError unauthorizedError(@NotNull Throwable exception, @NotNull DataFetchingEnvironment environment) {
        return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.UNAUTHORIZED)
                .message(exception.getMessage())
                .path(environment.getExecutionStepInfo().getPath())
                .location(environment.getField().getSourceLocation())
                .build();
    }

    private static GraphQLError badRequestError(@NotNull Throwable exception, @NotNull DataFetchingEnvironment environment) {
        return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.BAD_REQUEST)
                .message(exception.getMessage())
                .path(environment.getExecutionStepInfo().getPath())
                .location(environment.getField().getSourceLocation())
                .build();
    }

    private static GraphQLError forbiddenError(@NotNull Throwable exception, @NotNull DataFetchingEnvironment environment) {
        return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.FORBIDDEN)
                .message(exception.getMessage())
                .path(environment.getExecutionStepInfo().getPath())
                .location(environment.getField().getSourceLocation())
                .build();
    }

    private boolean isNotFound(@NotNull Throwable exception) {
        return exception instanceof OfficeNotFoundException ||
                exception instanceof UserNotFoundException ||
                exception instanceof WorkerNotFoundException ||
                exception instanceof UserOrWorkerIDNotFoundException ||
                exception instanceof NotificationNotFoundException ||
                exception instanceof RequestTypeNotFoundException ||
                exception instanceof AvailabilityNotFoundException ||
                exception instanceof RequestNotFoundException;
    }

    private boolean isBadRequest(@NotNull Throwable exception) {
        return exception instanceof InvalidAvailabilityIDException ||
                exception instanceof InvalidEmailException ||
                exception instanceof InvalidRequestTypeException ||
                exception instanceof InvalidWorkerActionException ||
                exception instanceof OfficeOverloadedException ||
                exception instanceof UserAlreadyExistsException ||
                exception instanceof InvalidDataFromRequestException;
    }

    private boolean isUnauthorized(@NotNull Throwable exception) {
        return exception instanceof InvalidRefreshTokenException ||
                exception instanceof SecurityException;
    }

    private boolean isForbidden(@NotNull Throwable exception) {
        return exception instanceof RateLimitException;
    }

    @Override
    protected GraphQLError resolveToSingleError(@NotNull Throwable exception, @NotNull DataFetchingEnvironment environment) {
        if (isForbidden(exception))
            return forbiddenError(exception, environment);
        else if (isUnauthorized(exception))
            return unauthorizedError(exception, environment);
        else if (isBadRequest(exception))
            return badRequestError(exception, environment);
        else if (isNotFound(exception))
            return notFoundError(exception, environment);
        else
            return super.resolveToSingleError(exception, environment);
    }
}
