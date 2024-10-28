package com.example.vessel_api.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {

    private Utils(){}

    public static LocalDateTime convertStringToLocalDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    public static Map<ErrorType, Integer> sortMapByValueDescending(Map<ErrorType, Integer> dataErrorsByType) {
        return dataErrorsByType.entrySet()
                .stream()
                .sorted(Map.Entry.<ErrorType, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // Handle duplicate keys
                        LinkedHashMap::new // Preserve order
                ));
    }
}
