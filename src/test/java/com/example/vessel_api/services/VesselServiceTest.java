package com.example.vessel_api.services;


import com.example.vessel_api.models.Vessel;
import com.example.vessel_api.models.VesselDataPoint;
import com.example.vessel_api.models.VesselMetric;
import com.example.vessel_api.models.response.AllDataResponse;
import com.example.vessel_api.models.response.VesselComplianceComparisonResponse;
import com.example.vessel_api.models.response.VesselSpeedDifferenceResponse;
import com.example.vessel_api.utils.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VesselServiceTest {


    @Mock
    private CsvLoaderService csvLoaderService;

    @InjectMocks
    private VesselService vesselService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSpeedDifference_withValidSpeedDifference() {
        String vesselCode = "vesselCode";
        VesselDataPoint dataPoint1 = mock(VesselDataPoint.class);
        when(dataPoint1.isSpeedDifferenceValid()).thenReturn(true);
        VesselMetric<Double> latitude = new VesselMetric<>(10.2894496917725, true, null);
        when(dataPoint1.getLatitude()).thenReturn(latitude);
        VesselMetric<Double> longitude = new VesselMetric<>(-14.7888498306274, true, null);
        when(dataPoint1.getLongitude()).thenReturn(longitude);
        when(dataPoint1.getSpeedDifference()).thenReturn(5.0);

        List<VesselDataPoint> dataPoints = List.of(dataPoint1);
        Vessel vessel = mock(Vessel.class);
        Map<String, Vessel> cachedData = new HashMap<>();
        cachedData.put(vesselCode, vessel);
        when(vessel.getVesselDataPoints()).thenReturn(dataPoints);
        when(csvLoaderService.importCsvData()).thenReturn(cachedData);

        VesselSpeedDifferenceResponse response = vesselService.getSpeedDifference(vesselCode);

        assertEquals(1, response.getSpeedDataPoints().size());
        assertEquals(5.0, response.getSpeedDataPoints().get(0).getSpeedDifference());
    }

    @Test
    void testGetSpeedDifference_noValidSpeedDifference() {
        String vesselCode = "vesselCode";
        VesselDataPoint dataPoint1 = mock(VesselDataPoint.class);
        when(dataPoint1.isSpeedDifferenceValid()).thenReturn(false);
        VesselMetric<Double> latitude = new VesselMetric<>(10.2894496917725, true, null);
        when(dataPoint1.getLatitude()).thenReturn(latitude);
        VesselMetric<Double> longitude = new VesselMetric<>(-14.7888498306274, true, null);
        when(dataPoint1.getLongitude()).thenReturn(longitude);
        when(dataPoint1.getSpeedDifference()).thenReturn(5.0);

        List<VesselDataPoint> dataPoints = List.of(dataPoint1);
        Vessel vessel = mock(Vessel.class);
        Map<String, Vessel> cachedData = new HashMap<>();
        cachedData.put(vesselCode, vessel);
        when(vessel.getVesselDataPoints()).thenReturn(dataPoints);
        when(csvLoaderService.importCsvData()).thenReturn(cachedData);

        VesselSpeedDifferenceResponse response = vesselService.getSpeedDifference(vesselCode);

        assertTrue(response.getSpeedDataPoints().isEmpty());
    }

    @Test
    void testGetDataErrorsByType_withErrors() {
        String vesselCode = "vesselCode";
        Map<ErrorType, Integer> errorReport = new HashMap<>();
        errorReport.put(ErrorType.MISSING, 10);
        errorReport.put(ErrorType.BELOW_ZERO, 5);

        Vessel vessel = mock(Vessel.class);
        Map<String, Vessel> cachedData = new HashMap<>();
        cachedData.put(vesselCode, vessel);
        when(vessel.getDataErrorsByType()).thenReturn(errorReport);
        when(csvLoaderService.importCsvData()).thenReturn(cachedData);

        Map<ErrorType, Integer> result = vesselService.getDataErrorsByType(vesselCode);

        assertEquals(2, result.size());
        assertEquals(10, result.get(ErrorType.MISSING));
        assertEquals(5, result.get(ErrorType.BELOW_ZERO));
    }

    @Test
    void testGetDataErrorsByType_noErrors() {
        String vesselCode = "vesselCode";
        Map<ErrorType, Integer> errorReport = new HashMap<>();

        Vessel vessel = mock(Vessel.class);
        Map<String, Vessel> cachedData = new HashMap<>();
        cachedData.put(vesselCode, vessel);
        when(vessel.getDataErrorsByType()).thenReturn(errorReport);
        when(csvLoaderService.importCsvData()).thenReturn(cachedData);

        Map<ErrorType, Integer> result = vesselService.getDataErrorsByType(vesselCode);

        assertTrue(result.isEmpty());
    }

    @Test
    void testCompareComplianceBetweenVessels() {
        String vesselCode1 = "vesselCode1";
        String vesselCode2 = "vesselCode2";

        Vessel vessel1 = mock(Vessel.class);
        when(vessel1.getVesselSpeedDeviation()).thenReturn(0.56);
        Vessel vessel2 = mock(Vessel.class);
        when(vessel2.getVesselSpeedDeviation()).thenReturn(0.46);
        Map<String, Vessel> cachedData = new HashMap<>();
        cachedData.put(vesselCode1, vessel1);
        cachedData.put(vesselCode2, vessel2);
        when(csvLoaderService.importCsvData()).thenReturn(cachedData);

        List<VesselComplianceComparisonResponse> result = vesselService.compareComplianceBetweenVessels(vesselCode1, vesselCode2);

        assertEquals(2, result.size());
        assertEquals(vesselCode2, result.get(0).getVesselCode()); // Lowest deviation is the most compliant
    }

    @Test
    void testGetAllData_withinTimeRange() {
        String vesselCode = "vesselCode1";
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 2, 0, 0);

        VesselDataPoint dataPoint = mock(VesselDataPoint.class);
        when(dataPoint.isSpeedDifferenceValid()).thenReturn(false);
        VesselMetric<Double> latitude = new VesselMetric<>(10.2894496917725, true, null);
        when(dataPoint.getLatitude()).thenReturn(latitude);
        VesselMetric<Double> longitude = new VesselMetric<>(-14.7888498306274, true, null);
        when(dataPoint.getLongitude()).thenReturn(longitude);
        when(dataPoint.getDatetime()).thenReturn(LocalDateTime.of(2023, 1, 1, 12, 0));

        List<VesselDataPoint> dataPoints = List.of(dataPoint);
        Vessel vessel = mock(Vessel.class);
        Map<String, Vessel> cachedData = new HashMap<>();
        cachedData.put(vesselCode, vessel);
        when(vessel.getVesselDataPoints()).thenReturn(dataPoints);
        when(csvLoaderService.importCsvData()).thenReturn(cachedData);

        List<AllDataResponse> result = vesselService.getAllData(vesselCode, start, end);

        assertEquals(1, result.size());
    }

    @Test
    void testGetAllData_outsideTimeRange() {
        String vesselCode = "vesselCode1";
        LocalDateTime start = LocalDateTime.of(2022, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2022, 1, 2, 0, 0);

        VesselDataPoint dataPoint = mock(VesselDataPoint.class);
        when(dataPoint.isSpeedDifferenceValid()).thenReturn(false);
        VesselMetric<Double> latitude = new VesselMetric<>(10.2894496917725, true, null);
        when(dataPoint.getLatitude()).thenReturn(latitude);
        VesselMetric<Double> longitude = new VesselMetric<>(-14.7888498306274, true, null);
        when(dataPoint.getLongitude()).thenReturn(longitude);
        when(dataPoint.getDatetime()).thenReturn(LocalDateTime.of(2023, 1, 1, 12, 0));

        List<VesselDataPoint> dataPoints = List.of(dataPoint);
        Vessel vessel = mock(Vessel.class);
        Map<String, Vessel> cachedData = new HashMap<>();
        cachedData.put(vesselCode, vessel);
        when(vessel.getVesselDataPoints()).thenReturn(dataPoints);
        when(csvLoaderService.importCsvData()).thenReturn(cachedData);

        List<AllDataResponse> result = vesselService.getAllData(vesselCode, start, end);

        assertTrue(result.isEmpty());
    }
}
