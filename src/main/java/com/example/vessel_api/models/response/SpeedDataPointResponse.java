package com.example.vessel_api.models.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class SpeedDataPointResponse {
    double latitude;
    double longitude;
    double speedDifference;
}
