package com.example.vessel_api.models;

import com.example.vessel_api.utils.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VesselMetric<T> {
    private T value;
    private boolean isValid;
    private ErrorType errorType;
}
