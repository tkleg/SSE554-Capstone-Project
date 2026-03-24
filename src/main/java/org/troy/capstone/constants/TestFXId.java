package org.troy.capstone.constants;

/** Enum representing the IDs of various UI components for TestFX testing. */
public enum TestFXId {
    /** ID for the sort option dropdown in the UI */
    SORT_OPTION_DROPDOWN("sortOptionDropdown");

    /** The string ID associated with the TestFXId enum value. */
    private final String id;

    /** Constructs a TestFXId enum value with the specified string ID.
     * @param id The string ID to be associated with the TestFXId enum value.
     */
    TestFXId(String id) {
        this.id = id;
    }

    /** Returns the string ID associated with this TestFXId enum value.
     * @return The string ID associated with this TestFXId enum value.
     */
    public String getId() {
        return id;
    }
}
