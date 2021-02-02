package com.assignment.loadbalancer;

import com.assignment.loadbalancer.provider.Provider;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.assignment.loadbalancer.ProviderState.ACTIVE;
import static com.assignment.loadbalancer.ProviderState.INACTIVE;
import static com.assignment.loadbalancer.ProviderState.RECOVERING;

/**
 * The registry.
 */
final class Registry {

    private static final int MAX_ELEMENTS = 10;
    private static Registry instance;
    private final ScheduledExecutorService executor;
    private final Map<Provider, ProviderState> providers;

    private Registry() {
        providers = new ConcurrentSkipListMap<>();
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::checkProviders, 0, 2, TimeUnit.SECONDS);
    }

    /**
     * Retrieves the registry instance.
     *
     * @return the registry
     */
    static synchronized Registry getInstance() {
        if (instance == null) {
            instance = new Registry();
        }
        return instance;
    }

    /**
     * Adds a provider to the registry.
     *
     * @param provider the provider to add
     *
     * @return true if the provider was added to the registry
     */
    synchronized boolean add(final Provider provider) {
        final int size = providers.size();
        if (size >= MAX_ELEMENTS) {
            return false;
        }
        providers.put(provider, ACTIVE);
        return size < providers.size();
    }

    /**
     * Removes a provider from the registry.
     *
     * @param provider the provider to remove
     *
     * @return true if the provider was removed from the registry
     */
    synchronized boolean remove(final Provider provider) {
        final int size = providers.size();
        providers.remove(provider);
        return size > providers.size();
    }

    /**
     * Returns an unmodifiable view of the active providers.
     *
     * @return an unmodifiable view of the active providers
     */
    synchronized List<Provider> getActiveProviders() {
        return providers.entrySet().stream()
                .filter(e -> e.getValue() == ACTIVE)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toUnmodifiableList());
    }

    private synchronized void activate(final Provider provider) {
        providers.computeIfPresent(provider, (p, ps) -> ps == INACTIVE ? RECOVERING : ACTIVE);
    }

    private synchronized void deActivate(final Provider provider) {
        providers.computeIfPresent(provider, (p, ps) -> INACTIVE);
    }

    private void checkProviders() {
        providers.keySet().forEach(provider -> {
            if (provider.check()) {
                activate(provider);
            } else {
                deActivate(provider);
            }
        });
    }
}