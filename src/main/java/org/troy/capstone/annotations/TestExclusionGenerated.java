package org.troy.capstone.annotations;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

//Code sourced from https://www.baeldung.com/jacoco-report-exclude
//JaCoCo automatically excludes any code with an annotation ending in Generated from coverage reports.
@Documented
@Retention(RUNTIME)
@Target({TYPE, METHOD, CONSTRUCTOR, LOCAL_VARIABLE, PACKAGE})
public @interface TestExclusionGenerated {
}