package org.troy.capstone.constants;

import java.io.File;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class DataPathTest {

    @ParameterizedTest
    @EnumSource(DataPath.class)
    @DisplayName("Test that all data paths in DataPaths enum point to existing files")
    public void testDataPaths(DataPath dataPath) {
        assert new File( dataPath.toString() ).exists() : "File should exist at path: " + dataPath.toString();
    }
}
