package it.univr.passportease.entity.enums;

import it.univr.passportease.entity.Availability;

/**
 * Status of a {@link Availability}.
 */
public enum Status {
    /**
     * The {@link Availability} is free and can be booked.
     */
    FREE,
    /**
     * The {@link Availability} is taken and cannot be booked.
     */
    TAKEN,
    /**
     * The {@link Availability} is not available anymore because it has expired has passed.
     */
    TIMEDOUT,
    /**
     * The {@link Availability} has been cancelled by the worker.
     */
    CANCELLED,
    /**
     * TODO: can't remember what this is for
     * The {@link Availability} is pending.
     */
    PENDING
}
