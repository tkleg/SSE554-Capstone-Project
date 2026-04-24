package org.troy.capstone.interfaces;

import java.util.List;
import java.util.Optional;
import org.troy.capstone.entities.Item;

/** Repository interface for managing items, simple getter method. */
public interface ItemRepo {

    /** Retrieves an item by its ID.
     * @pre itemId is not null.
      *
      * @param itemId The ID of the item to retrieve.
      * @return An Optional containing the Item if found, or empty if not found.
      */
    Optional<Item> getItem(String itemId);

    /** Retrieves the items in the repository as a list.
     * @return A list of all items in the repository.
     */
    List<Item> getItemsAsList();

    /** Retrieves the number of items in the repository.
     * @return The number of items in the repository.
     */
    int getSize();

}
