package com.example.vessel_api.models;

import com.example.vessel_api.utils.ErrorType;
import lombok.Getter;
import lombok.Setter;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Vessel {
    private String vesselCode;
    private List<VesselDataPoint> vesselDataPoints;
    private Map<ErrorType, Integer> dataErrorsByType;
    private double speedSumDeviation;
    private int speedDataPoints;

    public Vessel(String vesselCode, List<VesselDataPoint> vesselDataPoints) {
        this.vesselCode = vesselCode;
        this.vesselDataPoints = vesselDataPoints;
        this.dataErrorsByType = new EnumMap<>(ErrorType.class);
    }

    public double getVesselSpeedDeviation() {
        return speedSumDeviation / speedDataPoints;
    }

    public void addErrorToReport(ErrorType errorType) {
        dataErrorsByType.merge(errorType, 1, Integer::sum);
    }
}
