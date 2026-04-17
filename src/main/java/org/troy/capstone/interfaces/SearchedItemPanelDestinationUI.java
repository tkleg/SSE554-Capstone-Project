package org.troy.capstone.interfaces;

import java.util.List;

import javafx.scene.Node;

/**
 * The SearchedItemPanelDestinationUI interface represents a UI component that can serve as a destination for displaying searched item panels. It allows for Node and not just SearchedItemPanel to allow for a subset of the information to be displayed through this interface.
 */
public interface SearchedItemPanelDestinationUI {

    /** Sets the content of the destination UI with the given list of searched item panels.
     * @pre panels is not null.
     * @post The content of the destination UI is updated with the given list of searched item panels.
     * @param panels The list of searched item panels to display in the destination UI.
     */
    void setContent(List<Node> panels);
}
