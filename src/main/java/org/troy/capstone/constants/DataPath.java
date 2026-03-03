package org.troy.capstone.constants;

public enum DataPath {
    CLEANED_DATA_CSV("data\\1000_items_catalog_v2_cleaned.csv"),
    ATTRIBUTED_DATA_CSV("data\\1000_items_catalog_v2_attributed.csv"),
    CLEANED_ATTRIBUTED_DATA_CSV("data\\1000_items_catalog_v2_cleaned_attributed.csv"),
    //These are used for the ipynb files, since they cannot access the files through the same path as the Java code
    ROOT("C:\\Users\\thkle\\SSE554\\SSE554-Capstone-Project\\"),
    CLEANED_DATA_CSV_LONG(ROOT.path + CLEANED_DATA_CSV.path),
    ATTRIBUTED_DATA_CSV_LONG(ROOT.path + ATTRIBUTED_DATA_CSV.path),
    CLEANED_ATTRIBUTED_DATA_CSV_LONG(ROOT.path + CLEANED_ATTRIBUTED_DATA_CSV.path);

    private final String path;

    DataPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }

    public String getPath() {
        return path;
    }

}
