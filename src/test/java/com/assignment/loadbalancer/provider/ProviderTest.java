package com.assignment.loadbalancer.provider;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class ProviderTest {

    @Test
    void testUniqueString() {
        final Provider provider1 = new Provider();
        final Provider provider2 = new Provider();
        final UUID uuid1 = UUID.fromString(provider1.get());
        final UUID uuid2 = UUID.fromString(provider2.get());
        SoftAssertions.assertSoftly(softAssertions -> softAssertions
                .assertThat(uuid1)
                .as("UUIDs")
                .isNotEqualTo(uuid2)
        );
    }
}