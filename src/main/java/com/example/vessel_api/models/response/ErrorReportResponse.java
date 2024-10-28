package com.example.vessel_api.models.response;

import com.example.vessel_api.utils.ErrorType;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class ErrorReportResponse {
    private Map<ErrorType, Integer> dataErrorsByType;

}
