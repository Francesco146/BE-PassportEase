package it.univr.passportease.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import it.univr.passportease.exception.illegalstate.OfficeOverloadedException;
import it.univr.passportease.exception.illegalstate.UserAlreadyExistsException;
import it.univr.passportease.exception.invalid.*;
import it.univr.passportease.exception.notfound.*;
import it.univr.passportease.exception.security.RateLimitException;
import it.univr.passportease.exception.security.WrongPasswordException;
import org.jetbrains.annotations.NotNull;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

/**
 * This class handles the exceptions thrown by the GraphQL queries and mutations.
 * It handles the following exceptions classes:
 * <ul>
 *     <li>Forbidden Exceptions</li>
 *     <li>Bad Request Exceptions</li>
 *     <li>Not Found Exceptions</li>
 *     <li>Unauthorized Exceptions</li>
 * </ul>
 */
@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {
    /**
     * Handles the exceptions thrown by the GraphQL queries and mutations of type {@code Forbidden}.
     *
     * @param exception   The exception thrown by the query or mutation.
     * @param environment The environment of the query or mutation.
     * @return a {@link GraphQLError} object containing the error type of {@code Not Found}, the message, the path and the location of the error.
     */
    private static GraphQLError notFoundError(@NotNull Throwable exception, @NotNull DataFetchingEnvironment environment) {
        return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.NOT_FOUND)
                .message(exception.getMessage())
                .path(environment.getExecutionStepInfo().getPath())
                .location(environment.getField().getSourceLocation())
                .build();
    }

    /**
     * Handles the exceptions thrown by the GraphQL queries and mutations of type {@code Unauthorized}.
     *
     * @param exception   The exception thrown by the query or mutation.
     * @param environment The environment of the query or mutation.
     * @return a {@link GraphQLError} object containing the error type of {@code Unauthorized},
     * the message, the path and the location of the error.
     */
    private static GraphQLError unauthorizedError(@NotNull Throwable exception, @NotNull DataFetchingEnvironment environment) {
        return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.UNAUTHORIZED)
                .message(exception.getMessage())
                .path(environment.getExecutionStepInfo().getPath())
                .location(environment.getField().getSourceLocation())
                .build();
    }

    /**
     * Handles the exceptions thrown by the GraphQL queries and mutations of type {@code Bad Request}.
     *
     * @param exception   The exception thrown by the query or mutation.
     * @param environment The environment of the query or mutation.
     * @return a {@link GraphQLError} object containing the error type of {@code Bad Request},
     */
    private static GraphQLError badRequestError(@NotNull Throwable exception, @NotNull DataFetchingEnvironment environment) {
        return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.BAD_REQUEST)
                .message(exception.getMessage())
                .path(environment.getExecutionStepInfo().getPath())
                .location(environment.getField().getSourceLocation())
                .build();
    }

    /**
     * Handles the exceptions thrown by the GraphQL queries and mutations of type {@code Forbidden}.
     *
     * @param exception   The exception thrown by the query or mutation.
     * @param environment The environment of the query or mutation.
     * @return a {@link GraphQLError} object containing the error type of {@code Forbidden},
     */
    private static GraphQLError forbiddenError(@NotNull Throwable exception, @NotNull DataFetchingEnvironment environment) {
        return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.FORBIDDEN)
                .message(exception.getMessage())
                .path(environment.getExecutionStepInfo().getPath())
                .location(environment.getField().getSourceLocation())
                .build();
    }

    /**
     * @param exception The exception thrown by the query or mutation.
     * @return {@code true} if the exception is part of the {@code Forbidden} exceptions, {@code false} otherwise.
     */
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

    /**
     * @param exception The exception thrown by the query or mutation.
     * @return {@code true} if the exception is part of the {@code Unauthorized} exceptions, {@code false} otherwise.
     */
    private boolean isBadRequest(@NotNull Throwable exception) {
        return exception instanceof InvalidAvailabilityIDException ||
                exception instanceof InvalidEmailException ||
                exception instanceof InvalidRequestTypeException ||
                exception instanceof InvalidWorkerActionException ||
                exception instanceof OfficeOverloadedException ||
                exception instanceof UserAlreadyExistsException ||
                exception instanceof InvalidDataFromRequestException;
    }

    /**
     * @param exception The exception thrown by the query or mutation.
     * @return {@code true} if the exception is part of the {@code Unauthorized} exceptions, {@code false} otherwise.
     */
    private boolean isUnauthorized(@NotNull Throwable exception) {
        return exception instanceof InvalidRefreshTokenException ||
                exception instanceof SecurityException ||
                exception instanceof WrongPasswordException;
    }

    /**
     * @param exception The exception thrown by the query or mutation.
     * @return {@code true} if the exception is part of the {@code Forbidden} exceptions, {@code false} otherwise.
     */
    private boolean isForbidden(@NotNull Throwable exception) {
        return exception instanceof RateLimitException;
    }

    /**
     * @param exception   the exception to resolve to a single {@link GraphQLError}
     * @param environment the environment for the invoked {@code DataFetcher}
     * @return a {@link GraphQLError} object containing the right error type, the message, the path and the location of the error.
     * If the exception is not part of the handled exceptions, it returns the default error. (Internal Server Error)
     */
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

        return super.resolveToSingleError(exception, environment);
    }
}
