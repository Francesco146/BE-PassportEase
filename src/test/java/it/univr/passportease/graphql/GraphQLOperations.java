package it.univr.passportease.graphql;

import it.univr.passportease.entity.Availability;

/**
 * This enum contains all the GraphQL operations that can be performed. Each operation has a method that returns the
 * GraphQL document to be sent to the server.
 */
public enum GraphQLOperations {
    /**
     * This operation is used to log in a user.
     */
    loginUser {
        /**
         * @param args The first argument is the fiscal code of the user, the second argument is the password of the
         *             user.
         * @return The GraphQL document to be sent to the server.
         */
        public String getGraphQl(String... args) {
            String fiscalCode = args[0];
            String password = args[1];

            String loginUser = """
                    mutation {
                        loginUser(fiscalCode: "%s", password: "%s"){
                            id
                            jwtSet {
                                accessToken
                                refreshToken
                            }
                        }
                    }
                    """;
            return String.format(loginUser, fiscalCode, password);
        }
    },
    /**
     * This operation is used to log out a user.
     */
    logout {
        /**
         * @param args This operation does not require any argument.
         * @return The GraphQL document to be sent to the server.
         */
        public String getGraphQl(String... args) {
            return """
                    mutation {
                        logout
                    }
                    """;
        }
    },

    /**
     * This operation is used to register a user.
     */
    registerUser {
        /**
         * @param args The first argument is the fiscal code of the user, the second argument is the email of the user,
         *             the third argument is the name of the user, the fourth argument is the surname of the user, the
         *             fifth argument is the city of birth of the user, the sixth argument is the date of birth of the
         *             user, the seventh argument is the password of the user.
         * @return The GraphQL document to be sent to the server.
         */
        public String getGraphQl(String... args) {
            String fiscalCode = args[0];
            String email = args[1];
            String name = args[2];
            String surname = args[3];
            String cityOfBirth = args[4];
            String dateOfBirth = args[5];
            String password = args[6];

            String registerUser = """
                    mutation {
                        registerUser(
                                registerInput: {
                                    fiscalCode: "%s"
                                    email: "%s"
                                    name: "%s"
                                    surname: "%s"
                                    cityOfBirth: "%s"
                                    dateOfBirth: "%s"
                                    password: "%s"
                                }
                            ){
                            id
                            jwtSet {
                                accessToken
                                refreshToken
                            }
                        }
                    }
                    """;
            return String.format(registerUser, fiscalCode, email, name, surname, cityOfBirth, dateOfBirth, password);
        }
    },

    /**
     * This operation is used to change the password of a user.
     */
    changePassword {
        /**
         * @param args The first argument is the new password of the user, the second argument is the old password of
         *             the user.
         * @return The GraphQL document to be sent to the server.
         */
        public String getGraphQl(String... args) {
            String newPassword = args[0];
            String oldPassword = args[1];

            String changePassword = """
                    mutation {
                        changePassword (
                            newPassword: "%s"
                            oldPassword: "%s"
                        )
                    }
                    """;
            return String.format(changePassword, newPassword, oldPassword);
        }
    },

    /**
     * This operation is used to change the email of a user.
     */
    changeEmail {
        /**
         * @param args The first argument is the new email of the user, the second argument is the old email of the
         *             user.
         * @return The GraphQL document to be sent to the server.
         */
        public String getGraphQl(String... args) {
            String newEmail = args[0];
            String oldEmail = args[1];

            String changeEmail = """
                    mutation {
                        changeEmail(
                            newEmail: "%s"
                            oldEmail: "%s"
                        )
                    }
                    """;
            return String.format(changeEmail, newEmail, oldEmail);
        }
    },

    /**
     * This operation is used to refresh the access token of a user.
     */
    refreshAccessToken {
        /**
         * @param args The first argument is the refresh token of the user.
         * @return The GraphQL document to be sent to the server.
         */
        public String getGraphQl(String... args) {
            String refreshToken = args[0];

            String refreshAccessToken = """
                    mutation {
                        refreshAccessToken(
                            refreshToken: "%s"
                        )
                        {
                            accessToken
                            refreshToken
                        }
                    }
                    """;
            return String.format(refreshAccessToken, refreshToken);
        }
    },

    /**
     * This operation is used to get request types that can be made by a user.
     */
    getRequestTypesByUser {
        /**
         * @param args This operation does not require any argument.
         * @return The GraphQL document to be sent to the server.
         */
        public String getGraphQl(String... args) {

            return """
                    query {
                        getRequestTypesByUser {
                            id
                            name
                        }
                    }
                    """;
        }
    },

    /**
     * This operation is used to get all the information about a user from an {@link Availability} ID.
     */
    getReportDetailsByAvailabilityID {
        /**
         * @param args The first argument is the {@link Availability} ID.
         * @return The GraphQL document to be sent to the server.
         */
        public String getGraphQl(String... args) {
            String availabilityID = args[0];

            String getReportDetailsByAvailabilityID = """
                    query {
                        getReportDetailsByAvailabilityID(availabilityID: "%s") {
                            fiscalCode
                            address
                            cityOfBirth
                            dateOfAvailability
                            dateOfBirth
                            name
                            nameRequestType
                            officeName
                            startTime
                            surname
                        }
                    }
                    """;
            return String.format(getReportDetailsByAvailabilityID, availabilityID);
        }
    },

    /**
     * This operation is used to get all the user's notifications.
     */
    getUserNotifications {
        /**
         * @param args This operation does not require any argument.
         * @return The GraphQL document to be sent to the server.
         */
        public String getGraphQl(String... args) {

            return """
                    query {
                        getUserNotifications {
                            id
                            isReady
                            startDate
                            endDate
                            office {
                                id
                                name
                                address
                            }
                            requestType {
                                id
                                name
                            }
                        }
                    }
                    """;
        }
    },

    /**
     * This operation is used to get all the user data
     */
    getUserDetails {
        /**
         * @param args This operation does not require any argument.
         * @return The GraphQL document to be sent to the server.
         */
        public String getGraphQl(String... args) {

            return """
                        query {
                            getUserDetails {
                                id
                                name
                                surname
                                cityOfBirth
                                fiscalCode
                                dateOfBirth
                            }
                        }
                    """;
        }
    },

    /**
     * This operation is used to get all the user's reservations.
     */
    getUserReservations {
        /**
         * @param args This operation does not require any argument.
         * @return The GraphQL document to be sent to the server.
         */
        public String getGraphQl(String... args) {
            return """
                    query {
                        getUserReservations {
                            id
                            date
                            time
                            status
                            request {
                                id
                                duration
                                startDate
                                endDate
                                requestType {
                                    id
                                    name
                                }
                            }
                            office {
                                id
                                name
                                address
                            }
                        }
                    }
                    """;
        }
    };

    /**
     * @param args The arguments to be passed to the method. The number of arguments depends on the operation.
     * @return The GraphQL document to be sent to the server.
     */
    public abstract String getGraphQl(String... args);
}
