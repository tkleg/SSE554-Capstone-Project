# String Matching Algorithms

- **Rabin-Karp Rolling Hash**: This algorithm is used in the [`IdHashKey`](../../../src/main/java/org/troy/capstone/data_structures/item_table/IdHashKey.java) class to generate hash codes for the items based on their IDs. The Rabin-Karp algorithm is usually used for string matching, but as seen in this case, a part of the algorithm can be extracted to be used simply as a hash function.
  - Why it was chosen
    - This function provided a way to collapse a string (the item ID) into a single integer hash code with a low collision probability.
- **BM25**: This algorithm is used to score items based on a search query. It gives a score to each item based on an advanced algorithm designed to give the most relevant results a higher score. The scores are then used to filter out items that are not relevant to the search query. Citations are in the files.
    - Used in the [QueryFilter](../../../src/main/java/org/troy/capstone/search_engine/QueryFilter.java) and [MyBM25](../../../src/main/java/org/troy/capstone/search_engine/query/MyBM25.java) classes.
    - Why it was chosen
      - Allowed for custom weighting of different fields.
      - Deals with overruse of terms with saturation.
      - Deals with long vs short content.
      - Widely used and lots of documentation on the algorithm and implementation in java.
    - Implementation Details
      - An analyzer was built that tokenized the query from 2 to 5 characters. This allowed for typos to not be penalized too hard, while still giving better scores to items with longer matches. The analyzer also removes stop words (common english words such as 'the', 'a', etc.).
      - Directory is all in RAM, as data is loaded in from objects in memory, and the access is faster.
      - When filtering, the highest score is found, and items with scores less than 15% of the highest score are filtered out. This allows for precise queries to have less results, while broad queries can have many results. For example, "elec" yields far more results than "electric".
      - When searching, the name and description fields are used. The algorithm also stores the ID, but this is not used for searching, simply for retrieving the item after the search is done. The name field is given a higher weight than the description field, as it is more important for the name to match the query than the description.