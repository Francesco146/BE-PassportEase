package it.univr.passportease.helper.ratelimiter;

import io.github.bucket4j.Bandwidth;
import it.univr.passportease.controller.UserWorkerQueryController;
import it.univr.passportease.controller.user.UserAuthController;
import it.univr.passportease.controller.user.UserMutationController;
import it.univr.passportease.controller.user.UserQueryController;
import it.univr.passportease.controller.worker.WorkerAuthController;
import it.univr.passportease.controller.worker.WorkerMutationController;
import it.univr.passportease.controller.worker.WorkerQueryController;
import it.univr.passportease.dto.input.*;

import java.util.UUID;

/**
 * This enum contains all the rate limiters used in the application. Each enum value is associated with a
 * {@link Bandwidth} object that represents the rate limiter. Every mutation or query that needs to be rate limited
 * should be associated with a rate limiter.
 */
public enum RateLimiter {
    /**
     * Rate limiter for {@link UserQueryController#getRequestTypesByUser()}
     */
    GET_REQUEST_TYPES_BY_USER {
        /**
         * @return the rate limiter for {@link UserQueryController#getRequestTypesByUser()} with a capacity of 5 requests
         */
        public Bandwidth getLimit() {
            return getBandwidth(5);
        }
    },
    /**
     * Rate limiter for {@link UserQueryController#getReportDetailsByAvailabilityID(String)}
     */
    GET_REPORT_DETAILS_BY_AVAILABILITY_ID {
        /**
         * @return the rate limiter for {@link UserQueryController#getReportDetailsByAvailabilityID(String)} with a capacity of 5 requests
         */
        public Bandwidth getLimit() {
            return getBandwidth(5);
        }
    },
    /**
     * Rate limiter for {@link UserQueryController#getUserNotifications()}
     */
    GET_USER_NOTIFICATIONS {
        /**
         * @return the rate limiter for {@link UserQueryController#getUserNotifications()} with a capacity of 5 requests
         */
        public Bandwidth getLimit() {
            return getBandwidth(5);
        }
    },
    /**
     * Rate limiter for {@link UserQueryController#getUserDetails()}
     */
    GET_USER_DETAILS {
        /**
         * @return the rate limiter for {@link UserQueryController#getUserDetails()} with a capacity of 5 requests
         */
        public Bandwidth getLimit() {
            return getBandwidth(5);
        }
    },
    /**
     * Rate limiter for {@link UserQueryController#getUserReservations()}
     */
    GET_USER_RESERVATIONS {
        /**
         * @return the rate limiter for {@link UserQueryController#getUserReservations()} with a capacity of 5 requests
         */
        public Bandwidth getLimit() {
            return getBandwidth(5);
        }
    },
    /**
     * Rate limiter for {@link UserAuthController#loginUser(String, String)}
     */
    LOGIN_USER {
        /**
         * @return the rate limiter for {@link UserAuthController#loginUser(String, String)} with a capacity of 1 request
         */
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    /**
     * Rate limiter for {@link UserAuthController#logout()}
     */
    LOGOUT {
        /**
         * @return the rate limiter for {@link UserAuthController#logout()} with a capacity of 1 request
         */
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    /**
     * Rate limiter for {@link UserAuthController#registerUser(RegisterInput)}
     */
    REGISTER_USER {
        /**
         * @return the rate limiter for {@link UserAuthController#registerUser(RegisterInput)} with a capacity of 1 request
         */
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    /**
     * Rate limiter for {@link UserAuthController#refreshAccessToken(String)}
     */
    REFRESH_ACCESS_TOKEN {
        /**
         * @return the rate limiter for {@link UserAuthController#refreshAccessToken(String)} with a capacity of 1 request
         */
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    /**
     * Rate limiter for {@link UserAuthController#changePassword(String, String)}
     */
    CHANGE_PASSWORD {
        /**
         * @return the rate limiter for {@link UserAuthController#changePassword(String, String)} with a capacity of 1 request
         */
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    /**
     * Rate limiter for {@link UserMutationController#createReservation(String)}
     */
    CREATE_RESERVATION {
        /**
         * @return the rate limiter for {@link UserMutationController#createReservation(String)} with a capacity of 1 request
         */
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    /**
     * Rate limiter for {@link UserMutationController#deleteReservation(String)}
     */
    DELETE_RESERVATION {
        /**
         * @return the rate limiter for {@link UserMutationController#deleteReservation(String)} with a capacity of 1 request
         */
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    /**
     * Rate limiter for {@link UserMutationController#createReservation(String)}
     */
    CREATE_NOTIFICATION {
        /**
         * @return the rate limiter for {@link UserMutationController#createNotification(NotificationInput)} with a capacity of 1 request
         */
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    /**
     * Rate limiter for {@link UserMutationController#modifyNotification(NotificationInput, UUID)}
     */
    MODIFY_NOTIFICATION {
        /**
         * @return the rate limiter for {@link UserMutationController#modifyNotification(NotificationInput, UUID)} with a capacity of 1 request
         */
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    /**
     * Rate limiter for {@link UserMutationController#deleteNotification(UUID)}
     */
    DELETE_NOTIFICATION {
        /**
         * @return the rate limiter for {@link UserMutationController#deleteNotification(UUID)} with a capacity of 1 request
         */
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    /**
     * Rate limiter for {@link UserAuthController#changeEmail(String, String)}
     */
    CHANGE_EMAIL {
        /**
         * @return the rate limiter for {@link UserAuthController#changeEmail(String, String)} with a capacity of 1 request
         */
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    /**
     * Rate limiter for {@link WorkerQueryController#getRequestByAvailabilityID(String)}
     */
    GET_REQUEST_BY_AVAILABILITY_ID {
        /**
         * @return the rate limiter for {@link WorkerQueryController#getRequestByAvailabilityID(String)} with a capacity of 5 requests
         */
        public Bandwidth getLimit() {
            return getBandwidth(5);
        }
    },
    /**
     * Rate limiter for {@link WorkerQueryController#getAllRequestTypes()}
     */
    GET_ALL_REQUEST_TYPES {
        /**
         * @return the rate limiter for {@link WorkerQueryController#getAllRequestTypes()} with a capacity of 5 requests
         */
        public Bandwidth getLimit() {
            return getBandwidth(5);
        }
    },
    /**
     * Rate limiter for {@link UserWorkerQueryController#getAvailabilities(AvailabilityFilters, Integer, Integer)}
     */
    GET_AVAILABILITIES {
        /**
         * @return the rate limiter for {@link UserWorkerQueryController#getAvailabilities(AvailabilityFilters, Integer, Integer)} with a capacity of 5 requests
         */
        public Bandwidth getLimit() {
            return getBandwidth(5);
        }
    },
    /**
     * Rate limiter for {@link UserWorkerQueryController#getOffices()}
     */
    GET_OFFICES {
        /**
         * @return the rate limiter for {@link UserWorkerQueryController#getOffices()} with a capacity of 5 requests
         */
        public Bandwidth getLimit() {
            return getBandwidth(5);
        }
    },
    /**
     * Rate limiter for {@link WorkerAuthController#loginWorker(String, String)}
     */
    LOGIN_WORKER {
        /**
         * @return the rate limiter for {@link WorkerAuthController#loginWorker(String, String)} with a capacity of 1 request
         */
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    /**
     * Rate limiter for {@link WorkerMutationController#createRequest(RequestInput)}
     */
    CREATE_REQUEST {
        /**
         * @return the rate limiter for {@link WorkerMutationController#createRequest(RequestInput)} with a capacity of 1 request
         */
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    /**
     * Rate limiter for {@link WorkerMutationController#modifyRequest(String, RequestInput)}
     */
    MODIFY_REQUEST {
        /**
         * @return the rate limiter for {@link WorkerMutationController#modifyRequest(String, RequestInput)} with a capacity of 1 request
         */
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    /**
     * Rate limiter for {@link WorkerMutationController#deleteRequest(String)}
     */
    DELETE_REQUEST {
        /**
         * @return the rate limiter for {@link WorkerMutationController#deleteRequest(String)} with a capacity of 1 request
         */
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    /**
     * @deprecated Rate limiter for {@link WorkerAuthController#registerWorker(WorkerInput)}
     */
    REGISTER_WORKER {
        /**
         * @return the rate limiter for {@link WorkerAuthController#registerWorker(WorkerInput)} with a capacity of 1 request
         */
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    };

    /**
     * @return the rate limiter for the enum value
     */
    public abstract Bandwidth getLimit();

    /**
     * @return the default rate limiter for the enum value
     */
    Bandwidth getBandwidth() {
        // Default value
        return getBandwidth(1);
    }

    /**
     * @param seconds the number of seconds to wait before refilling the bucket by 1 token
     * @return the {@link Bandwidth} object for the enum value
     */
    Bandwidth getBandwidth(int seconds) {
        return Bandwidth.builder()
                .capacity(1)
                .refillGreedy(1, java.time.Duration.ofSeconds(seconds))
                .build();
    }
}
