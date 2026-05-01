# Package Explanations

- Annotations
  - This holds one file, [Generated.java](../../src/main/java/org/troy/capstone/annotations/Generated.java). This simply tells Jacoco to ignore the annotated class or method when calculating code coverage. This is used on generated code, such as the Javadocs and dependency graphs, as well as on classes that are not part of the main execution flow, such as testing classes and the `ItemRepo` class.