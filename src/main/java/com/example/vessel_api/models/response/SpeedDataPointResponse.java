package com.example.vessel_api.models.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SpeedDataPointResponse {
    double latitude;
    double longitude;
    double speedDifference;
}
