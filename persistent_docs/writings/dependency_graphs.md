# Dependency Graphs

Notice that there are no circular dependencies in any of the generated graphs.
Dependency graphs are seen in the [generated_docs/dependency_graph](../../generated_docs/dependency_graph) directory. The `.png` files are the generated graphs. All graphs are filtered to exclude the following edges.
- Edges from a class to itself
- Edges from a class to its inner classes
- Edges involving classes not part of the main execution flow
- Edges involving classes not written as part of this project

There are three different graphs, each with a different level of filtering.
- [deps.png](../../generated_docs/dependency_graph/deps.png) - This graph is filtered to only include classes that are part of the main execution flow, but all edges between those classes are included.
- [deps_cleaner.png](../../generated_docs/dependency_graph/deps_cleaner.png) - This graph excludes the `constants`, `utils`, and `annotations` packages, as these are not important for understanding the main execution flow.
- [deps_no_item_repo.png](../../generated_docs/dependency_graph/deps_no_item_repo.png) - This graph excludes the `ItemRepo` class, as it is a large class that is used by many other classes, but is simply an entity accessor interface.
- [deps_no_item_repo_no_entities.png](../../generated_docs/dependency_graph/deps_no_item_repo_no_entities.png) - This graph also excludes the `Item` class, as it is referenced in many places, but it is a simple data class.