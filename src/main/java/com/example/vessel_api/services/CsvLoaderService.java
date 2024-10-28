package com.example.vessel_api.services;

import com.example.vessel_api.exceptions.ApplicationException;
import com.example.vessel_api.models.Vessel;
import com.example.vessel_api.models.VesselDataPoint;
import com.example.vessel_api.models.VesselMetric;
import com.example.vessel_api.utils.ErrorType;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.vessel_api.utils.Utils.convertStringToLocalDateTime;

@Service
public class CsvLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(CsvLoaderService.class);
    private static final String CSV_PATH = "src/main/resources/data/vessel_data.csv";

    @Cacheable("vesselsData")
    public Map<String, Vessel> importCsvData() {
        Map<String, Vessel> vesselsData = new HashMap<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(CSV_PATH))) {
            String[] csvLine;

            csvReader.readNext();
            while ((csvLine = csvReader.readNext()) != null) {
                String vesselId = csvLine[0];
                Vessel vessel = vesselsData.getOrDefault(vesselId, new Vessel(vesselId, new ArrayList<>()));

                VesselDataPoint dataPoint = transformCsvLineToVesselDataObject(csvLine, vessel);
                vessel.getVesselDataPoints().add(dataPoint);

                increaseVesselSumSpeedDeviation(dataPoint, vessel);

                vesselsData.put(vesselId, vessel);
            }
        } catch (FileNotFoundException e) {
            logger.error("CSV file not found in location {} ", CSV_PATH);
            throw new ApplicationException(HttpStatus.NOT_FOUND,
                    String.format("CSV file not found in location %s ", CSV_PATH));
        } catch (IOException e) {
            logger.error("Problem when reading the csv file");
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Problem when reading the csv file");
        } catch (CsvValidationException e) {
            logger.error("There is been issue with the structure, content, or validity of the CSV data.");
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "There is been issue with the structure, content, or validity of the CSV data.");
        }

        return vesselsData;
    }

    private static void increaseVesselSumSpeedDeviation(VesselDataPoint dataPoint, Vessel vessel) {
        if (Objects.nonNull(dataPoint.getSpeedDifference())) {
            vessel.setSpeedDataPoints(vessel.getSpeedDataPoints() + 1);
            vessel.setSpeedSumDeviation(vessel.getSpeedSumDeviation() + dataPoint.getSpeedDifference());
        }
    }

    private VesselDataPoint transformCsvLineToVesselDataObject(String[] csvLine, Vessel vessel) {
        VesselDataPoint vesselDataPoint = new VesselDataPoint();
        vesselDataPoint.setDatetime(convertStringToLocalDateTime(csvLine[1]));
        vesselDataPoint.setLatitude(convertMetricStringToVesselMetricObject(csvLine[2], vessel, false));
        vesselDataPoint.setLongitude(convertMetricStringToVesselMetricObject(csvLine[3], vessel, false));
        vesselDataPoint.setPower(convertMetricStringToVesselMetricObject(csvLine[4], vessel, true));
        vesselDataPoint.setFuelConsumption(convertMetricStringToVesselMetricObject(csvLine[5], vessel, true));
        vesselDataPoint.setActualSpeedOverground(convertMetricStringToVesselMetricObject(csvLine[6], vessel, true));
        vesselDataPoint.setProposedSpeedOverground(convertMetricStringToVesselMetricObject(csvLine[7], vessel, true));
        vesselDataPoint.setPredictedFuelConsumption(convertMetricStringToVesselMetricObject(csvLine[8], vessel, true));

        calculateSpeedDifference(vesselDataPoint);

        return vesselDataPoint;
    }

    private static void calculateSpeedDifference(VesselDataPoint vesselDataPoint) {
        VesselMetric<Double> actualSpeed = vesselDataPoint.getActualSpeedOverground();
        VesselMetric<Double> proposedSpeed = vesselDataPoint.getProposedSpeedOverground();

        if (vesselDataPoint.coordinatesAreValid() && actualSpeed.isValid() && proposedSpeed.isValid()) {
            vesselDataPoint.setSpeedDifference(Math.abs(proposedSpeed.getValue() - actualSpeed.getValue()));
        }
    }

    private VesselMetric<Double> convertMetricStringToVesselMetricObject(String metric, Vessel vessel, boolean checkBelowZero) {
        VesselMetric<Double> vesselMetric = new VesselMetric<>();

        if (isMetricMissing(metric)) {
            vesselMetric.setErrorType(ErrorType.MISSING);
            vesselMetric.setValid(false);
            vessel.addErrorToReport(ErrorType.MISSING);
            return vesselMetric;
        }

        Double metricValue = Double.valueOf(metric);

        if (checkBelowZero && metricValue < 0) {
            vesselMetric.setErrorType(ErrorType.BELOW_ZERO);
            vesselMetric.setValid(false);
            vessel.addErrorToReport(ErrorType.BELOW_ZERO);
            return vesselMetric;
        }

        vesselMetric.setValue(metricValue);
        vesselMetric.setValid(true);

        return vesselMetric;
    }

    private static boolean isMetricMissing(String metric) {
        return Objects.isNull(metric) || metric.isEmpty() || metric.equals("NULL");
    }
}
