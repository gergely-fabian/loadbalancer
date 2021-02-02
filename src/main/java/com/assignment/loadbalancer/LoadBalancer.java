package com.assignment.loadbalancer;

import com.assignment.loadbalancer.exception.LoadBalancerException;
import com.assignment.loadbalancer.provider.Provider;
import com.assignment.loadbalancer.strategy.Strategy;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * The load balancer.
 */
public final class LoadBalancer {

    private static LoadBalancer instance;
    private final Registry registry;
    private final AtomicInteger currentRequests;
    private final AtomicReference<Provider> previousProvider;
    private Supplier<Strategy> strategySupplier;

    private LoadBalancer(final Supplier<Strategy> strategySupplier) {
        this.registry = Registry.getInstance();
        this.strategySupplier = strategySupplier;
        currentRequests = new AtomicInteger();
        previousProvider = new AtomicReference<>();
    }

    /**
     * Retrieves the registry instance.
     *
     * @return the registry
     */
    public static synchronized LoadBalancer getInstance(final Supplier<Strategy> strategySupplier) {
        if (instance == null) {
            instance = new LoadBalancer(strategySupplier);
        } else {
            instance.setStrategySupplier(strategySupplier);
        }
        return instance;
    }

    /**
     * Sets thesupplier for the load balancing strategy.
     *
     * @param strategySupplier
     */
    public synchronized void setStrategySupplier(final Supplier<Strategy> strategySupplier) {
        this.strategySupplier = strategySupplier;
    }

    /**
     * Registers a provider.
     *
     * @param provider the provider to register
     *
     * @return true if the provider is registered
     */
    public boolean register(final Provider provider) {
        return registry.add(provider);
    }

    /**
     * Registers a list of providers.
     *
     * @param providers the provider list to register
     */
    public void register(final List<Provider> providers) {
        providers.forEach(this::register);
    }

    /**
     * Removes a provider.
     *
     * @param provider the provider to remove
     *
     * @return true if the provider is removed (but was registered already)
     */
    public boolean remove(final Provider provider) {
        return registry.remove(provider);
    }

    /**
     * Invokes the load balancer and returns a response from one of the registered providers.
     *
     * @return
     *
     * @throws LoadBalancerException if an error occurs
     */
    public String get() throws LoadBalancerException {
        checkCapacity();
        final CompletableFuture<String> completableFuture = supplyAsync(() -> {
            currentRequests.incrementAndGet();
            return loadBalancerStrategy().get();
        });
        completableFuture.thenRun(currentRequests::decrementAndGet);
        try {
            return completableFuture.get();
        } catch (final InterruptedException | ExecutionException e) {
            throw new LoadBalancerException(e);
        }
    }

    private synchronized void checkCapacity() {
        if ((registry.getActiveProviders().size() * Provider.MAXIMUM_PARALLEL_REQUESTS) <= currentRequests.get()) {
            throw new LoadBalancerException("Maximum number of parallel requests reached, no more requests allowed");
        }
    }

    private synchronized Provider loadBalancerStrategy() {
        final Optional<Provider> providerOptional = strategySupplier.get().apply(previousProvider.get(), registry.getActiveProviders());
        final Provider provider = providerOptional.orElseThrow(() -> new LoadBalancerException("The request cannot be distributed to any provider"));
        previousProvider.set(provider);
        return provider;
    }
}