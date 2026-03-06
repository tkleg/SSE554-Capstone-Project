package org.troy.capstone.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.troy.capstone.annotations.TestExclusionGenerated;

public class Converters {

    //Never called, just prevents Jacoco from complaining about missing code coverage for the default constructor
    @TestExclusionGenerated
    private Converters() {
    }

    //Algorithm sourced from https://www.tutorialspoint.com/java-program-to-convert-localdate-to-java-util-date
    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
