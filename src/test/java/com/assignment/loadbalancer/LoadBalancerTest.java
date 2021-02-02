package com.assignment.loadbalancer;

import com.assignment.loadbalancer.provider.Provider;
import com.assignment.loadbalancer.strategy.Strategy;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class LoadBalancerTest {

    @Test
    void testProviderList() {
        final LoadBalancer loadBalancer = LoadBalancer.getInstance(Strategy::roundRobin);
        final List<Provider> providerList = List.of(new Provider(), new Provider(), new Provider());
        loadBalancer.register(providerList);
        providerAction(providerList, loadBalancer::remove);
    }

    @Test
    void testRoundRobin() {
        final LoadBalancer loadBalancer = LoadBalancer.getInstance(Strategy::roundRobin);
        final List<Provider> providerList = List.of(new Provider(), new Provider(), new Provider(), new Provider(), new Provider());
        providerAction(providerList, loadBalancer::register);
        final List<Provider> providers = providerList.stream().sorted().collect(Collectors.toList());
        for (int i = 0; i < providers.size() * 3; i++) {
            int index = i % providers.size();
            assertSoftly(softAssertions -> softAssertions
                    .assertThat(loadBalancer.get())
                    .as("Result")
                    .isEqualTo(providers.get(index).get()));
        }

        providerAction(providerList, loadBalancer::remove);
    }

    @Test
    void testMaxNumberOfProviders() {
        final LoadBalancer loadBalancer = LoadBalancer.getInstance(Strategy::random);
        final List<Provider> providerList = IntStream.rangeClosed(0, 9)
                .boxed()
                .map(i -> new Provider())
                .collect(Collectors.toUnmodifiableList());
        providerAction(providerList, loadBalancer::register);
        final boolean registered = loadBalancer.register(new Provider());
        assertSoftly(softAssertions -> softAssertions
                .assertThat(registered)
                .as("Another provider added")
                .isFalse()
        );
        providerAction(providerList, loadBalancer::remove);
    }

    private void providerAction(final List<Provider> providerList, final Function<Provider, Boolean> mapper) {
        final long count = providerList.stream()
                .map(mapper)
                .filter(b -> b)
                .count();
        assertSoftly(softAssertions -> softAssertions
                .assertThat(count)
                .as("Changed providers")
                .isEqualTo(providerList.size())
        );
    }
}