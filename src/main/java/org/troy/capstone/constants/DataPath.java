package org.troy.capstone.constants;

/**
 * This enum defines the file paths for the cleaned, attributed, and cleaned &amp; attributed CSV files.
 */
public enum DataPath {
    /** Full path to the cleaned data CSV file. */
    CLEANED_DATA("C:\\Users\\thkle\\SSE554\\SSE554-Capstone-Project\\data\\\\1000_items_catalog_v2_cleaned.csv"),
    /** Full path to the attributed data CSV file. */
    ATTRIBUTED_DATA("C:\\Users\\thkle\\SSE554\\SSE554-Capstone-Project\\data\\\\1000_items_catalog_v2_attributed.csv"),
    /** Full path to the cleaned and attributed data CSV file. */
    CLEANED_ATTRIBUTED_DATA("C:\\Users\\thkle\\SSE554\\SSE554-Capstone-Project\\data\\\\1000_items_catalog_v2_cleaned_attributed.csv");

    /** The path represented by this enum constant. */
    private final String path;

    /** Constructor for the enum constant. 
     * 
     * @param path The file path associated with the enum constant.
    */
    DataPath(String path) {
        this.path = path;
    }

    /**
     * Getter for the path associated with this enum constant.
     * 
     * @return The file path as a string.
     */
    @Override
    public String toString() {
        return path;
    }

}
