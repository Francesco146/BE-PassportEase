package it.univr.passportease.graphql;

public enum GraphQLOperations {
    loginUser {
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
    logout {
        public String getGraphQl(String... args) {
            return """
                    mutation {
                        logout
                    }
                    """;
        }
    },

    registerUser {
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

    changePassword {
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

    changeEmail {
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

    refreshAccessToken {
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

    getRequestTypesByUser {
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

    getUserNotifications {
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

    getUserDetails{
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

    getUserReservations {
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

    public abstract String getGraphQl(String... args);
}

