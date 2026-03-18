package org.troy.capstone.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ConvertersTest {

    @ParameterizedTest
    @CsvSource({
        "2024-01-01",
        "2023-12-31",
        "2020-02-29",
        "1990-06-15"
    })
    @DisplayName("Test localDateToDate conversion")
    public void testLocalDateToDate(LocalDate localDate) {
        Date date = Converters.localDateToDate(localDate);

        assert date != null : "Converted date should not be null";
        assert date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(localDate) : "Converted date should match the original LocalDate";
    }
}
