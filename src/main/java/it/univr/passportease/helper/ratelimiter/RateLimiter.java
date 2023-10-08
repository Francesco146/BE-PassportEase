package it.univr.passportease.helper.ratelimiter;

import io.github.bucket4j.Bandwidth;

public enum RateLimiter {
    GET_REQUEST_TYPES_BY_USER {
        public Bandwidth getLimit() {
            return getBandwidth(5);
        }
    },
    GET_REPORT_DETAILS_BY_AVAILABILITY_ID {
        public Bandwidth getLimit() {
            return getBandwidth(5);
        }
    },
    GET_USER_NOTIFICATIONS {
        public Bandwidth getLimit() {
            return getBandwidth(5);
        }
    },
    GET_USER_DETAILS {
        public Bandwidth getLimit() {
            return getBandwidth(5);
        }
    },
    GET_USER_RESERVATIONS {
        public Bandwidth getLimit() {
            return getBandwidth(5);
        }
    },
    LOGIN_USER {
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    LOGOUT {
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    REGISTER_USER {
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    REFRESH_ACCESS_TOKEN {
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    CHANGE_PASSWORD {
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    CREATE_RESERVATION {
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    DELETE_RESERVATION {
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    CREATE_NOTIFICATION {
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    MODIFY_NOTIFICATION {
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    DELETE_NOTIFICATION {
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    CHANGE_EMAIL {
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    GET_REQUEST_BY_AVAILABILITY_ID {
        public Bandwidth getLimit() {
            return getBandwidth(5);
        }
    },
    GET_ALL_REQUEST_TYPES {
        public Bandwidth getLimit() {
            return getBandwidth(5);
        }
    },
    GET_AVAILABILITIES {
        public Bandwidth getLimit() {
            return getBandwidth(5);
        }
    },
    GET_OFFICES {
        public Bandwidth getLimit() {
            return getBandwidth(5);
        }
    },
    LOGIN_WORKER {
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    CREATE_REQUEST {
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    MODIFY_REQUEST {
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    DELETE_REQUEST {
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    },
    REGISTER_WORKER {
        public Bandwidth getLimit() {
            return getBandwidth(1);
        }
    };

    public abstract Bandwidth getLimit();

    Bandwidth getBandwidth(int seconds) {
        return Bandwidth.builder()
                .capacity(1)
                .refillGreedy(1, java.time.Duration.ofSeconds(seconds))
                .build();
    }
}
