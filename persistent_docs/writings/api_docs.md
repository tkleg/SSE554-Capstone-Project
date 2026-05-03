# API Docs

To view Javadocs for the project, go to [generated_docs/javadocs/index.html](../../generated_docs/javadocs/index.html) and open it in a web browser once the project is cloned (or view it in GitHub Pages) and stored locally. The Javadocs are generated using the maven plugin and auto generate from Javadoc comments on code.

## Excluded Files
- Main.java
  - The main entry point of the application. Excluded as it only holds the basic JavaFX launch code and a main method.
- ENV.java
  - Holds sensitive information so it is excluded from Javadocs.
- MyBM25.java
  - Standalone implementation of BM25 algorithm for testing. Not used in the main execution flow of the application, so it is excluded from the Javadocs.
- EntryPoints.java
  - Holds main methods from various classes for testing. Moving these main methods to this class allows the Javadocs to be generated without including their main method. These main methods are not used in the main execution flow of the application, so they are excluded from the Javadocs.
- SortingAnalysis.java
  - Used for testing and analyzing the sorting algorithms. Not used in the main execution flow of the application, so it is excluded from the Javadocs.
- SimilarityAnalysis.java
  - Creates a `.dat` file holding similarity scores between items to allow for analysis in a python script. Not used in the main execution flow of the application, so it is excluded from the Javadocs.