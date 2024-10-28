package com.example.vessel_api.controllers;

import com.example.vessel_api.models.response.AllDataResponse;
import com.example.vessel_api.models.response.VesselComplianceComparisonResponse;
import com.example.vessel_api.models.response.VesselSpeedDifferenceResponse;
import com.example.vessel_api.services.VesselService;
import com.example.vessel_api.utils.ErrorType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vessel")
public class VesselController {

    private final VesselService vesselService;
    private static final Logger logger = LoggerFactory.getLogger(VesselController.class);

    public VesselController(VesselService vesselService) {
        this.vesselService = vesselService;
    }

    @GetMapping("/speed-difference")
    @Operation(summary = "Returns speed difference between the vessel’s actual speed over ground and the speed over\n" +
            "ground that was proposed by the system, at each waypoint ")
    public VesselSpeedDifferenceResponse getSpeedDifference(
            @RequestParam(defaultValue = "3001")
            @Parameter(description = "The unique code of the vessel") String vesselCode
    ) {
        logger.info("Getting speed difference at each waypoint for vessel {}", vesselCode);
        return vesselService.getSpeedDifference(vesselCode);
    }

    @GetMapping("/invalid-data-report")
    @Operation(summary = "Returns problems identified in the original data, sorted by\n" +
            "frequency of occurrence, descending.")
    public Map<ErrorType, Integer> getInvalidDataReport(
            @RequestParam(defaultValue = "3001")
            @Parameter(description = "The unique code of the vessel.") String vesselCode
    ){
        logger.info("Getting invalid data report for vessel {}", vesselCode);
        return vesselService.getDataErrorsByType(vesselCode);
    }

    @GetMapping("/compliance")
    @Operation(summary = "Compares the speed deviation(actual vs proposed speed) of two vessels over all the valid timestamps. " +
            "The vessel with the smaller speed deviation is the more compliant vessel with the system’s suggestions.")
    public List<VesselComplianceComparisonResponse> getComplianceReportBetweenTwoVessels(
            @RequestParam(defaultValue = "3001")
            @Parameter(description = "The unique code of the first vessel") String vessel1,

            @RequestParam(defaultValue = "19310")
            @Parameter(description = "The unique code of the second vessel") String vessel2
    ) {
        logger.info("Compares the speed deviation between vessel {} and vessel {}", vessel1, vessel2);
        return vesselService.compareComplianceBetweenVessels(vessel1, vessel2);
    }

    @GetMapping("/data")
    @Operation(summary = "Returns raw and calculated data for a vessel between two dates. " +
            "If a value was invalid in the original file it is returned as null.")
    public List<AllDataResponse> getAllData(
            @RequestParam(defaultValue = "3001")
            @Parameter(description = "The unique code of the vessel.") String vesselCode,

            @RequestParam(defaultValue = "2023-01-01T00:00:00")
            @Parameter(description = "The start date and time for the data retrieval in the format 'yyyy-MM-dd'T'HH:mm:ss'. Default is '2024-01-01T00:00:00'.")
            LocalDateTime start,

            @RequestParam(defaultValue = "2023-12-31T23:59:59")
            @Parameter(description = "The end date and time for the data retrieval in the format 'yyyy-MM-dd'T'HH:mm:ss'. Default is '2024-12-31T23:59:59'.")
            LocalDateTime end
    ) {
        logger.info("Returns all data for vessel {}", vesselCode);
        return vesselService.getAllData(vesselCode, start, end);
    }
}
