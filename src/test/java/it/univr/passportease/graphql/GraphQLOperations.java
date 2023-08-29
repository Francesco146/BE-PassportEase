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
    };

    public abstract String getGraphQl(String... args);
}

