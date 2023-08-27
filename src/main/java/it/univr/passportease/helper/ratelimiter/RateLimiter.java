package it.univr.passportease.helper.ratelimiter;

import io.github.bucket4j.Bandwidth;

public enum RateLimiter {
    getRequestTypesByUser {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    getReportDetailsByAvailabilityID {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    getUserNotifications {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    getUserDetails {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    getUserReservations {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    loginUser {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    logout {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    registerUser {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    refreshAccessToken {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    changePassword {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    createReservation {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    deleteReservation {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    createNotification {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    modifyNotification {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    deleteNotification {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    changeEmail {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    getRequestByAvailabilityID {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    getAllRequestTypes {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    getAvailabilities {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    getOffices {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    loginWorker {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    createRequest {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    modifyRequest {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    deleteRequest {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    },
    registerWorker {
        public Bandwidth getLimit() {
            return Bandwidth.simple(1, java.time.Duration.ofSeconds(1));
        }
    };

    public abstract Bandwidth getLimit();
}
