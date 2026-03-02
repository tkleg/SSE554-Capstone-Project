package org.troy.capstone.constants;

public class uiSizeControls {

    public static final int HEIGHT_PADDING = 10;
    public static final int WIDTH_PADDING = 10;

    public static final int LEFT_SIDE_MAIN_PAGE_WIDTH = 550;
    public static final int RIGHT_SIDE_MAIN_PAGE_WIDTH = 500;
    public static final int MAIN_PAGE_HEIGHT = 800;

    public static final int SEARCH_BAR_WIDTH = LEFT_SIDE_MAIN_PAGE_WIDTH;
    public static final int SEARCH_BAR_HEIGHT = 50;

    public static final int SEARCHED_ITEM_PAGINATION_WIDTH = LEFT_SIDE_MAIN_PAGE_WIDTH;
    public static final int SEARCHED_ITEM_PAGINATION_HEIGHT = MAIN_PAGE_HEIGHT - SEARCH_BAR_HEIGHT - 100;
    public static final int SEARCHED_ITEM_CONTAINER_WIDTH = SEARCHED_ITEM_PAGINATION_WIDTH;
    public static final int SEARCHED_ITEM_CONTAINER_HEIGHT = SEARCHED_ITEM_PAGINATION_HEIGHT;
    public static final int SEARCHED_ITEM_SPACING = 5;

    public static final int SEARCHED_ITEM_PANEL_WIDTH = SEARCHED_ITEM_CONTAINER_WIDTH - WIDTH_PADDING*2;
    public static final int SEARCHED_ITEM_PANEL_HEIGHT = 150;

    public static final int ATTRIBUTED_ITEM_IMAGE_WIDTH = 150;
    public static final int ATTRIBUTED_ITEM_IMAGE_HEIGHT = 150;

    public static final int SEARCHED_ITEM_LABEL_MAX_WIDTH = SEARCHED_ITEM_PANEL_WIDTH - ATTRIBUTED_ITEM_IMAGE_WIDTH - WIDTH_PADDING*3;
}
