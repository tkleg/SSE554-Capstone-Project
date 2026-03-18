package org.troy.capstone.constants;

/**
 * This enum defines the file paths for the cleaned, attributed, and cleaned &amp; attributed CSV files. It also includes the root path for use in Jupyter notebooks, which cannot access files through the same relative paths as the Java code.
 */
public enum DataPath {
    /** Path to the cleaned data CSV file starting from the project root */
    CLEANED_DATA_CSV("data\\1000_items_catalog_v2_cleaned.csv"),
    /** Path to the attributed data CSV file starting from the project root */
    ATTRIBUTED_DATA_CSV("data\\1000_items_catalog_v2_attributed.csv"),
    /** Path to the cleaned and attributed data CSV file starting from the project root */
    CLEANED_ATTRIBUTED_DATA_CSV("data\\1000_items_catalog_v2_cleaned_attributed.csv"),

    /** Root path for use in Jupyter notebooks */
    ROOT("C:\\Users\\thkle\\SSE554\\SSE554-Capstone-Project\\"),
    /** Full path to the cleaned data CSV file */
    CLEANED_DATA_CSV_LONG(ROOT.path + CLEANED_DATA_CSV.path),
    /** Full path to the attributed data CSV file */
    ATTRIBUTED_DATA_CSV_LONG(ROOT.path + ATTRIBUTED_DATA_CSV.path),
    /** Full path to the cleaned and attributed data CSV file */
    CLEANED_ATTRIBUTED_DATA_CSV_LONG(ROOT.path + CLEANED_ATTRIBUTED_DATA_CSV.path);

    /** The path represented by this enum constant */
    private final String path;

    /** Constructor for the enum constant 
     * 
     * @param path The file path associated with the enum constant
    */
    DataPath(String path) {
        this.path = path;
    }


    /**
     * Getter for the path associated with this enum constant
     * @return The file path as a string
     */
    @Override
    public String toString() {
        return path;
    }

}
