/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package org.troy.capstone;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

/**
 *
 * @author thkle
 */
public class Project {

    static {
        // Suppress SLF4J warnings
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "off");
    }

    public static void main(String[] args) {
        String[] animals = {"bear", "cat", "giraffe"};
        double[] cuteness = {90.1, 84.3, 99.7};

        Table cuteAnimals =
            Table.create("Cute Animals")
                .addColumns(
                    StringColumn.create("Animal types", animals),
                    DoubleColumn.create("rating", cuteness));

        System.out.println(cuteAnimals.print());
    }
}
