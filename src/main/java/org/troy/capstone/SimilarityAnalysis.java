package org.troy.capstone;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.troy.capstone.constants.DataPath;
import org.troy.capstone.entities.Item;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.Table;

public class SimilarityAnalysis {
    public static void main(String[] args) throws Exception {
        Table table = TableUtils.readData(DataPath.CLEANED_ATTRIBUTED_DATA);
        List<Item> items = TableUtils.tableToRowList(table).stream().map(Item::fromRow).toList();
        File outputFile = new File("similarity_analysis_output.dat");
        try (PrintWriter writer = new PrintWriter(outputFile)) {
            for (int i = 0; i < items.size(); i++) {
                for (int j = 0; j < items.size(); j++) {
                    if (i == j)
                        continue; // Skip similarity with itself
                    Item item1 = items.get(i);
                    Item item2 = items.get(j);
                    writer.printf("%.7f\n", item1.similarity(item2));
                }
                System.out.printf("Processed item %d/%d\n", i + 1, items.size());
            }
        }
    }
}
