package org.troy.capstone.constants;

/**
 * Enum for UI size control, to centralize all size-related constants for the UI in one place. This allows for easy adjustments to the UI layout and ensures consistency across different components.
 */
public enum UISizeControl {

    /** Padding height for UI elements */
    HEIGHT_PADDING(10),
    /** Padding width for UI elements */
    WIDTH_PADDING(10),
    
    /** Width of the left side of the main page, which contains the search results and filters */
    LEFT_SIDE_MAIN_PAGE_WIDTH(550),
    /** Width of the right side of the main page, which contains the filters and other controls */
    RIGHT_SIDE_MAIN_PAGE_WIDTH(500),
    /** Total height of the main page */
    MAIN_PAGE_HEIGHT(800),
    
    /** Width of the search bar at the top of the main page */
    SEARCH_BAR_WIDTH(LEFT_SIDE_MAIN_PAGE_WIDTH.value),
    /** Height of the search bar at the top of the main page */
    SEARCH_BAR_HEIGHT(50),
    
    /** Width of the pagination controls for searched items */
    SEARCHED_ITEM_PAGINATION_WIDTH(LEFT_SIDE_MAIN_PAGE_WIDTH.value),
    /** Height of the pagination controls for searched items, calculated as the main page height minus the search bar height and some additional padding */
    SEARCHED_ITEM_PAGINATION_HEIGHT(MAIN_PAGE_HEIGHT.value - SEARCH_BAR_HEIGHT.value - 100),
    /** Width of the container holding the searched items */
    SEARCHED_ITEM_CONTAINER_WIDTH(SEARCHED_ITEM_PAGINATION_WIDTH.value),
    /** Height of the container holding the searched items, calculated as the pagination height minus some additional padding */
    SEARCHED_ITEM_CONTAINER_HEIGHT(SEARCHED_ITEM_PAGINATION_HEIGHT.value),
    /** Spacing between individual searched item panels within the container */
    SEARCHED_ITEM_SPACING(5),
    
    /** Width of each individual searched item panel, calculated as the container width minus padding on both sides */
    SEARCHED_ITEM_PANEL_WIDTH(SEARCHED_ITEM_CONTAINER_WIDTH.value - WIDTH_PADDING.value * 2),
    /** Height of each individual searched item panel, set to a fixed value for consistency */
    SEARCHED_ITEM_PANEL_HEIGHT(150),
    /** Width of the image displayed for each attributed item, set to a fixed value for consistency */
    ATTRIBUTED_ITEM_IMAGE_WIDTH(150),
    /** Height of the image displayed for each attributed item, set to a fixed value for consistency */
    ATTRIBUTED_ITEM_IMAGE_HEIGHT(150),
    
    /** Width of the container holding the attributed items, calculated as the image width plus some additional padding */
    ATTRIBUTED_ITEM_CONTAINER_WIDTH(ATTRIBUTED_ITEM_IMAGE_WIDTH.value + 50),
    
    /** Maximum width of the label displaying the name and other details of each attributed item, calculated as the panel width minus the image width and some additional padding */
    SEARCHED_ITEM_LABEL_MAX_WIDTH(SEARCHED_ITEM_PANEL_WIDTH.value - ATTRIBUTED_ITEM_IMAGE_WIDTH.value - WIDTH_PADDING.value * 3),
    
    /** Width of the container holding the filters on the right side of the main page, set to match the width of the right side for consistency */
    FILTERS_CONTAINER_WIDTH(RIGHT_SIDE_MAIN_PAGE_WIDTH.value),
    /** Height of the container holding the filters on the right side of the main page, set to half of the main page height to allow space for other controls */
    FILTERS_CONTAINER_HEIGHT(MAIN_PAGE_HEIGHT.value / 2);

    /** The integer value associated with this enum constant, used for size control in the UI */
    private final int value;

    /** Constructor for the enum constants
     * @param value The integer value to associate with this enum constant, typically used for size control in the UI
    */
    UISizeControl(int value) {
        this.value = value;
    }

    /** Getter for the integer value associated with this enum constant
     * @return The integer value that can be used for size control in the UI
    */
    public int getValue() {
        return value;
    }
}
