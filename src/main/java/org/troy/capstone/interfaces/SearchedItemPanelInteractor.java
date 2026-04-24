package org.troy.capstone.interfaces;

/**
 * Interface for interacting with instances of the {@code SearchedItemPanel} class, allowing actions to be taken when an item is selected.
 */
public interface SearchedItemPanelInteractor {

    /** Method to be called when an item is selected in the search results.
     * @param itemId The ID of the selected item.
     */
    void onItemSelected(String itemId);
}
