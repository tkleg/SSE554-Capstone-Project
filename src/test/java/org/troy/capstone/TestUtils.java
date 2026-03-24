package org.troy.capstone;

import org.troy.capstone.constants.TestFXId;
import org.testfx.api.FxRobot;
import javafx.scene.Node;

public class TestUtils {
    /**
     * Looks up a node by TestFXId and returns the result of query().
     * Only to be used in TestFX tests.
     * @param id The TestFXId enum value.
     * @param <T> The expected type of the node.
     * @return The node found by TestFX lookup().query().
     */
    @SuppressWarnings("unchecked")
    public static <T extends Node> T lookupByTestFXId(TestFXId id) {
        return (T) new FxRobot().lookup("#" + id.getId()).query();
    }
}