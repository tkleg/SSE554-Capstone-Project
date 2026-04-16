package org.troy.capstone.interfaces;

import java.util.List;

import org.troy.capstone.ui_components.items.SearchedItemPanel;

/**
 * The SearchedItemPanelDestinationUI interface represents a UI component that can serve as a destination for displaying searched item panels.
 */
public interface SearchedItemPanelDestinationUI {

    /** Sets the content of the destination UI with the given list of searched item panels.
     * @pre panels is not null.
     * @post The content of the destination UI is updated with the given list of searched item panels.
     * @param panels The list of searched item panels to display in the destination UI.
     */
    void setContent(List<SearchedItemPanel> panels);
}
