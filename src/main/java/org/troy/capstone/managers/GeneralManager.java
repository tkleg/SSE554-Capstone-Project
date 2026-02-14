package org.troy.capstone.managers;

import java.util.Map;

import org.troy.capstone.constants.uiDataNames;
import org.troy.capstone.constants.uiElementName;

import javafx.scene.Node;

public class GeneralManager {
    private final UIElementManager uiManager;

    public GeneralManager() {
        uiManager = new UIElementManager();
    }

    public UIElementManager getUiManager() {
        return uiManager;
    }

    public Map<uiDataNames, Object> getSearchData() {
        return uiManager.getSearchData();
    }

    public void addUIElement(uiElementName key, Node element) {
        uiManager.addElement(key, element);
    }

}
