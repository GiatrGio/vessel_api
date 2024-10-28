package com.example.vessel_api.models.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class VesselComplianceComparissonResponse {
    private String vesselCode;
    private double speedDeviation;
}
