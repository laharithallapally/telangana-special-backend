package com.telanaganaspecial.service;

import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class DistanceService {

    private static final Map<String, Double> AREA_DISTANCES_KM = Map.of(
            "kondapur", 6.0,
            "madhapur", 3.0,
            "gachibowli", 8.0,
            "kukatpally", 10.0,
            "hitech city", 2.0
    );

    private static final double DEFAULT_DISTANCE_KM = 12.0; // fallback for unknown areas

    public double calculateDistanceKm(String address) {
        String normalized = address.toLowerCase();
        for (Map.Entry<String, Double> entry : AREA_DISTANCES_KM.entrySet()) {
            if (normalized.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return DEFAULT_DISTANCE_KM;
    }
}