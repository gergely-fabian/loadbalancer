package com.assignment.loadbalancer.strategy;

import com.assignment.loadbalancer.provider.Provider;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;

import static java.util.Objects.isNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * Strategy describing the load balancer algorithm.
 */
public interface Strategy extends BiFunction<Provider, List<Provider>, Optional<Provider>> {

    /**
     * Random invocation strategy.
     *
     * @return a random provider
     */
    static Strategy random() {
        return (provider, list) -> {
            if (list.isEmpty()) {
                return empty();
            }
            final int index = ThreadLocalRandom.current().nextInt(list.size());
            return of(list.get(index));
        };
    }

    /**
     * Round Robin invocation strategy.
     *
     * @return the  provider
     */
    static Strategy roundRobin() {
        return (provider, list) -> {
            if (list.isEmpty()) {
                return empty();
            }
            final int index = isNull(provider) ? -1 : list.indexOf(provider);
            return of(index == list.size() - 1 || index < 0 ? list.get(0) : list.get(index + 1));
        };
    }
}