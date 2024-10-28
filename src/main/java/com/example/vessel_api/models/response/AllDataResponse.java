package com.example.vessel_api.models.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AllDataResponse {
    private LocalDateTime dateTime;
    private Double latitude;
    private Double longitude;
    private Double power;
    private Double fuelConsumption;
    private Double actualSpeedOverground;
    private Double proposedSpeedOverground;
    private Double predictedFuelConsumption;
    private Double speedDifference;
}
