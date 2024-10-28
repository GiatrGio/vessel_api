package com.example.vessel_api.services;

import com.example.vessel_api.models.Vessel;
import com.example.vessel_api.models.VesselDataPoint;
import com.example.vessel_api.models.response.AllDataResponse;
import com.example.vessel_api.models.response.VesselComplianceComparissonResponse;
import com.example.vessel_api.models.response.SpeedDataPointResponse;
import com.example.vessel_api.models.response.VesselSpeedDifferenceResponse;
import com.example.vessel_api.utils.ErrorType;
import com.example.vessel_api.utils.Utils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VesselService {
    private final CsvLoaderService csvLoaderService;

    public VesselService(CsvLoaderService csvLoaderService) {
        this.csvLoaderService = csvLoaderService;
    }

    public VesselSpeedDifferenceResponse getSpeedDifference(String vesselCode) {
        VesselSpeedDifferenceResponse response = new VesselSpeedDifferenceResponse(new ArrayList<>());

        List<VesselDataPoint> dataPoints = csvLoaderService.importCsvData().get(vesselCode).getVesselDataPoints();

        dataPoints.forEach(dataPoint -> {
                    if (dataPoint.isSpeedDifferenceValid()) {
                        addSpeedDataPointToResponse(dataPoint, response);
                    }});

        return response;
    }

    private static void addSpeedDataPointToResponse(VesselDataPoint dataPoint, VesselSpeedDifferenceResponse response) {
        response.getSpeedDataPoints().add(
                new SpeedDataPointResponse(
                        dataPoint.getLatitude().getValue(),
                        dataPoint.getLongitude().getValue(),
                        dataPoint.getSpeedDifference())
        );
    }

    public Map<ErrorType, Integer> getDataErrorsByType(String vesselCode) {
        Map<ErrorType, Integer> errorReport = csvLoaderService.importCsvData().get(vesselCode).getDataErrorsByType();
        return Utils.sortMapByValueDescending(errorReport);
    }

    public List<VesselComplianceComparissonResponse> compareComplianceBetweenVessels(String vesselCode1, String vesselCode2) {
        Vessel vessel1 = csvLoaderService.importCsvData().get(vesselCode1);
        Vessel vessel2 = csvLoaderService.importCsvData().get(vesselCode2);

        VesselComplianceComparissonResponse vesselResponse1 = new VesselComplianceComparissonResponse(
                vesselCode1, vessel1.getVesselSpeedDeviation());
        VesselComplianceComparissonResponse vesselResponse2 = new VesselComplianceComparissonResponse(
                vesselCode2, vessel2.getVesselSpeedDeviation());

        return Stream.of(vesselResponse1, vesselResponse2)
                .sorted(Comparator.comparing(VesselComplianceComparissonResponse::getSpeedDeviation))
                .collect(Collectors.toList());
    }

    public List<AllDataResponse> getAllData(String vesselCode, LocalDateTime start, LocalDateTime end) {
        return csvLoaderService.importCsvData().get(vesselCode).getVesselDataPoints().stream()
                .filter(data -> data.getDatetime().isAfter(start) && data.getDatetime().isBefore(end))
                .map(VesselService::createResponseObjectForDatapoint)
                .toList();
    }

    private static AllDataResponse createResponseObjectForDatapoint(VesselDataPoint data) {
        AllDataResponse dataResponse = new AllDataResponse();

        dataResponse.setDateTime(data.getDatetime());
        dataResponse.setLatitude(data.getValueIfValid(data.getLatitude()));
        dataResponse.setLongitude(data.getValueIfValid(data.getLongitude()));
        dataResponse.setPower(data.getValueIfValid(data.getPower()));
        dataResponse.setFuelConsumption(data.getValueIfValid(data.getFuelConsumption()));
        dataResponse.setActualSpeedOverground(data.getValueIfValid(data.getActualSpeedOverground()));
        dataResponse.setProposedSpeedOverground(data.getValueIfValid(data.getProposedSpeedOverground()));
        dataResponse.setPredictedFuelConsumption(data.getValueIfValid(data.getPredictedFuelConsumption()));
        dataResponse.setSpeedDifference(data.getSpeedDifference());

        return dataResponse;
    }
}
