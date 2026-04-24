package org.troy.capstone.interfaces;

/**
 * The SearchedItemPanelSourceUI interface represents a UI component that can serve as a source for searched item panels.
 * It defines a method for adding a SearchedItemPanelInteractor to receive item selection events from the searched item panels.
 */
public interface SearchedItemPanelSourceUI {

    /** Adds a SearchedItemPanelInteractor to receive item selection events from the searched item panels.
     * @pre interactor is not null.
     * @param interactor The SearchedItemPanelInteractor to add.
     */
    void addSearchedItemPanelInteractor(SearchedItemPanelInteractor interactor);
    
}
