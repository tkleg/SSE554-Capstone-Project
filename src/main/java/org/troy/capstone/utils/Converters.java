package org.troy.capstone.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.troy.capstone.annotations.TestExclusionGenerated;

/** Utility class for converting between different date types. */
public class Converters {

    //Never called, just prevents Jacoco from complaining about missing code coverage for the default constructor
    @TestExclusionGenerated
    private Converters() {
    }

    /**Algorithm sourced from https://www.tutorialspoint.com/java-program-to-convert-localdate-to-java-util-date
     * Converts a LocalDate to a java.util.Date.
     * 
     * @param localDate The LocalDate to be converted.
     * @return The corresponding java.util.Date.
     */
    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
