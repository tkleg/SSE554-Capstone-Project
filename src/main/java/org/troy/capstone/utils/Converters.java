package org.troy.capstone.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/** Utility class for converting between different date types. */
public class Converters {

    /**
     * Only exists to prevent Jacoco from complaining about the default constructor not being tested.
     * As the only function of this class is to provide static methods, there is no reason for it to be instantiated, so the constructor is private.
     */
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
