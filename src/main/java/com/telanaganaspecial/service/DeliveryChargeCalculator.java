package com.telanaganaspecial.service;

import org.springframework.stereotype.Component;

@Component
public class DeliveryChargeCalculator {

    private static final double BASE_FEE = 30.0;
    private static final double PER_KM_RATE = 6.0;

    public double calculateCharge(double distanceKm) {
        double rawCharge = BASE_FEE + (distanceKm * PER_KM_RATE);
        return Math.ceil(rawCharge / 5.0) * 5.0;
    }
}