package org.troy.capstone.constants;

/** Enum representing the IDs of various UI components for {@code TestFX} testing. */
public enum TestFXId {
    /** ID for the sort option dropdown in the UI */
    SORT_OPTION_DROPDOWN("sortOptionDropdown"),

    /** ID prefix for attributed item image views in the UI */
    ATTRIBUTED_IMAGE_VIEW_PREFIX("attributedImageView_"),

    /** ID prefix for attributed item attribution flows in the UI */
    ATTRIBUTED_AUTHOR_NAME_PREFIX("attributedAuthorName_"),

    /** ID prefix for attributed item source name in the UI */
    ATTRIBUTED_SOURCE_NAME_PREFIX("attributedSourceName_");
    /** The string ID associated with the {@code TestFXId} enum value. */
    private final String id;

    /** Constructs a {@code TestFXId} enum value with the specified string ID.
     * @param id The string ID to be associated with the {@code TestFXId} enum value.
     */
    TestFXId(String id) {
        this.id = id;
    }

    /** Returns the string ID associated with this {@code TestFXId} enum value.
     * @return The string ID associated with this {@code TestFXId} enum value.
     */
    public String getId() {
        return id;
    }
}
