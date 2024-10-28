package com.example.vessel_api.models;

import com.opencsv.bean.CsvDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class VesselDataPoint {
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datetime;
    private VesselMetric<Double> latitude;
    private VesselMetric<Double> longitude;
    private VesselMetric<Double> power;
    private VesselMetric<Double> fuelConsumption;
    private VesselMetric<Double> actualSpeedOverground;
    private VesselMetric<Double> proposedSpeedOverground;
    private VesselMetric<Double> predictedFuelConsumption;

    private Double speedDifference;

    public boolean isSpeedDifferenceValid() {
        return coordinatesAreValid() && Objects.nonNull(speedDifference);
    }

    public boolean coordinatesAreValid() {
        return this.longitude.isValid() && this.latitude.isValid();
    }

    public Double getValueIfValid(VesselMetric<Double> vesselMetric) {
        return vesselMetric.isValid() ? vesselMetric.getValue() : null;
    }

}
