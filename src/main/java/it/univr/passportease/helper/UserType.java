package it.univr.passportease.helper;

/**
 * Interface that represents a user or a worker. Used to generalize the methods that can be used on both.
 */
public interface UserType {
    /**
     * Returns the email of the user or worker.
     *
     * @return the email of the user or worker.
     */
    String getEmail();

    /**
     * Changes the email of the user or worker.
     *
     * @param email new email
     */
    void setEmail(String email);

    /**
     * Returns the hash password of the user or worker.
     *
     * @return the hash password of the user or worker.
     */
    String getHashPassword();

    /**
     * Changes the hash password of the user or worker.
     *
     * @param hashPassword new hash password
     */
    void setHashPassword(String hashPassword);

    /**
     * Returns the refresh token of the user or worker.
     *
     * @return the refresh token of the user or worker.
     */
    String getRefreshToken();

    /**
     * Changes the refresh token of the user or worker.
     *
     * @param refreshToken new refresh token
     */
    void setRefreshToken(String refreshToken);
}
