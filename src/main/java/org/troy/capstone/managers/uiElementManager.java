package org.troy.capstone.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javafx.scene.Node;

public class uiElementManager {
    private Map<String, Node> uiElements;
    
    public uiElementManager() {
        uiElements = new HashMap<>();
    }

    public Optional<Node> getElement(String key) {
        return Optional.ofNullable(uiElements.get(key));
    }

    public void addElement(String key, Node element) {
        uiElements.put(key, element);
    }

    public Set<String> getAllKeys() {
        return uiElements.keySet();
    }
    
}
