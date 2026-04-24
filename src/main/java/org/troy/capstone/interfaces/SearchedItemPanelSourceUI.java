package org.troy.capstone.interfaces;

/**
 * The {@code SearchedItemPanelSourceUI} interface represents a UI component that can serve as a source for searched item panels.
 * It defines a method for adding a {@code SearchedItemPanelInteractor} to receive item selection events from the searched item panels.
 */
public interface SearchedItemPanelSourceUI {

    /** Adds a {@code SearchedItemPanelInteractor} to receive item selection events from the searched item panels.
     * @pre interactor is not null.
     * @param interactor The {@code SearchedItemPanelInteractor} to add.
     */
    void addSearchedItemPanelInteractor(SearchedItemPanelInteractor interactor);
    
}
