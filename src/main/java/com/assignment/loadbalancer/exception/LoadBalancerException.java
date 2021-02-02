package com.assignment.loadbalancer.exception;

/**
 * Custom exception for the application.
 */
public class LoadBalancerException extends RuntimeException {
    public LoadBalancerException(final Exception e) {
        super(e);
    }

    public LoadBalancerException(final String message) {
        super(message);
    }
}