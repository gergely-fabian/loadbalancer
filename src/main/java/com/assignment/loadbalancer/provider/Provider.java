package com.assignment.loadbalancer.provider;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.UUID.randomUUID;

/**
 * The provider.
 */
@EqualsAndHashCode
@ToString
public final class Provider implements Comparable<Provider> {

    public static final int MAXIMUM_PARALLEL_REQUESTS = 2;
    private final UUID uuid;

    public Provider() {
        this.uuid = randomUUID();
    }

    /**
     * Retrieves the provider's response.
     *
     * @return the response of the provider
     */
    public String get() {
        return uuid.toString();
    }

    /**
     * Performs the heart beat check.
     *
     * @return true if the provider is considered alive.
     */
    public boolean check() {
        return ThreadLocalRandom.current().nextDouble() < 0.9;
    }

    @Override
    public int compareTo(final Provider p) {
        return this.uuid.compareTo(p.uuid);
    }
}