package com.example.vessel_api.models.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class VesselSpeedDifferenceResponse {
    @Schema(description = "Speed difference between actual and proposed speed per waypoint")
    private List<SpeedDataPointResponse> speedDataPoints;
}
