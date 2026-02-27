package org.troy.capstone.constants;

public class dataPaths {
    public static final String CLEANED_DATA_CSV = "data\\1000_items_catalog_v2_cleaned.csv";
    public static final String ATTRIBUTED_DATA_CSV = "data\\1000_items_catalog_v2_attributed.csv";
    public static final String CLEANED_ATTRIBUTED_DATA_CSV = "data\\1000_items_catalog_v2_cleaned_attributed.csv";

    //These are used for the ipynb files, since they cannot access the files through the same path as the Java code
    public static final String ROOT = "C:\\Users\\thkle\\SSE554\\SSE554-Capstone-Project\\";
    public static final String CLEANED_DATA_CSV_LONG = ROOT + CLEANED_DATA_CSV;
    public static final String ATTRIBUTED_DATA_CSV_LONG = ROOT + ATTRIBUTED_DATA_CSV;
    public static final String CLEANED_ATTRIBUTED_DATA_CSV_LONG = ROOT + CLEANED_ATTRIBUTED_DATA_CSV;
}
