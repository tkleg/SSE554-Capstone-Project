# Testing

All tests can be found under the [`src/test`](../../src/test) directory. The tests files can be categorized into three different categories.
- Unit Tests - These tests are located in the subdirectories of [`src/test/java/org/troy/capstone/`](../../src/test/java/org/troy/capstone/). They test individual methods and classes in isolation from most of the program. Tests have been designed to target most methods individually, with test cases being used to go after each branch of each method. 
- Integration Tests - There is one integration test file, [`src/test/java/org/troy/capstone/MainTest.java`](../../src/test/java/org/troy/capstone/MainTest.java). This file tests the `SearchEngine` class, which is responsible for applying all filters and sorting to the data. This class is a good candidate for integration testing, as it relies on many different classes and methods to operate correctly.
  - The first test `testDisplayInSimilarItemsAndRecentlyViewedItems` clicks on an item in the `SearchedItemPagination` and checks that it gets displayed in the `RecentlyViewedItemsContainer` as well as checking that the similar items are correctly displayed in the `SimilarItemsContainer`. A video of the test running can be seen [HERE](../resources/click_panel_integration_test.mp4).
  - The second test `testFilteredSearch` makes a selection for each filter type and checks that the right number of restults are returned, and that the `SearchedItemPagination` is updated with the correct number of pages. A video of the test running can be seen [HERE](../resources/filtered_search_test.mp4).
- Coverage
  - Test coverage is calculated with the Jacoco reporting plugin and can be found in the `generated_docs/coverage/` directory. To properly view the report open the `index.html` file in that directory in a web browser. This detailed report includes coverage for methods, lines, and branches for each class in the project. The tests have been designed to give as much coverage as possible.
  - Missed coverage
    - Instructions - 23/6379 missed (0.36% missed)
    - Branches - 1/273 missed (0.37% missed)
    - 