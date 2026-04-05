package org.troy.capstone.entities;

import static org.junit.Assert.assertThrows;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.troy.capstone.constants.TableColumnName;

import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class ItemTest {
    private static Item testItem;

    @BeforeAll
    public static void setup() {
        testItem = Item.builder()
                .index((short) 1)
                .imageUrl("http://example.com/test-image.jpg")
                .name("Test Item")
                .publisher("Test Publisher")
                .description("This is a test item.")
                .category("Test Category")
                .tags( Set.of("tag1", "tag2") )
                .price(19.99f)
                .reviewScore(4.5f)
                .reviewCount((short) 100)
                .stockQuantity((short) 50)
                .dateAdded(new Date())
                .photoAuthor("Test Author")
                .photoAuthorUrl("http://example.com/test-author")
                .id("test-item-1")
                .build();
    }

    @ParameterizedTest
    @EnumSource(value = TableColumnName.class,
        names = {"RELEVANCE"}, //Exclude relevance since it's not a part of the item class, but is added later during search query filtering
        mode = EnumSource.Mode.EXCLUDE
    )
    @DisplayName("Test that getAttribute returns the correct value for each column")
    public void testGetAttribute(TableColumnName column){
        Object data = testItem.getAttribute(column);
        assert data != null : "getAttribute should not return null for column: " + column.name();
    }

    @Test
    @DisplayName("Test that getAttribute throws IllegalArgumentException for invalid column")
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public void testGetAttributeInvalidColumn(){
        assertThrows(IllegalArgumentException.class, () -> testItem.getAttribute(TableColumnName.RELEVANCE));
    }

    @Test
    @DisplayName("Test fromRow creates an Item with the expected attributes")
    public void testFromRow(){
        Table table = Table.create(
            StringColumn.create(TableColumnName.ID.getColumnName(), new String[]{testItem.getId()}),
            ShortColumn.create(TableColumnName.INDEX.getColumnName(), new short[]{testItem.getIndex()}),
            StringColumn.create(TableColumnName.IMAGE_URL.getColumnName(), new String[]{testItem.getImageUrl()}),
            StringColumn.create(TableColumnName.NAME.getColumnName(), new String[]{testItem.getName()}),
            StringColumn.create(TableColumnName.PUBLISHER.getColumnName(), new String[]{testItem.getPublisher()}),
            StringColumn.create(TableColumnName.DESCRIPTION.getColumnName(), new String[]{testItem.getDescription()}),
            StringColumn.create(TableColumnName.CATEGORY.getColumnName(), new String[]{testItem.getCategory()}),
            StringColumn.create(TableColumnName.TAGS.getColumnName(), new String[]{String.join(", ", testItem.getTags())}),
            StringColumn.create(TableColumnName.PHOTO_AUTHOR.getColumnName(), new String[]{testItem.getPhotoAuthor()}),
            StringColumn.create(TableColumnName.PHOTO_AUTHOR_URL.getColumnName(), new String[]{testItem.getPhotoAuthorUrl()}),
            FloatColumn.create(TableColumnName.PRICE.getColumnName(), new float[]{testItem.getPrice()}),
            FloatColumn.create(TableColumnName.REVIEW_SCORE.getColumnName(), new float[]{testItem.getReviewScore()}),
            ShortColumn.create(TableColumnName.REVIEW_COUNT.getColumnName(), new short[]{testItem.getReviewCount()}),
            ShortColumn.create(TableColumnName.STOCK_QUANTITY.getColumnName(), new short[]{testItem.getStockQuantity()}),
            DateColumn.create(TableColumnName.DATE_ADDED.getColumnName(), 
                new LocalDate[]{testItem.getDateAdded().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()})
        );
        Row row = table.row(0);
        Item itemFromRow = Item.fromRow(row);
        assert itemFromRow.getId().equals(testItem.getId()) : "ID should match";
        assert itemFromRow.getName().equals(testItem.getName()) : "Name should match";
        assert itemFromRow.getPrice() == testItem.getPrice() : "Price should match";
        //Compare only the date part since conversion between Date and LocalDate loses time precision
        LocalDate originalDate = testItem.getDateAdded().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate recreatedDate = itemFromRow.getDateAdded().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        assert recreatedDate.equals(originalDate) : "Date added should match (date part only)";
        assert itemFromRow.getTags().equals(testItem.getTags()) : "Tags should match";
        assert itemFromRow.getDescription().equals(testItem.getDescription()) : "Description should match";
        assert itemFromRow.getPublisher().equals(testItem.getPublisher()) : "Publisher should match";
        assert itemFromRow.getCategory().equals(testItem.getCategory()) : "Category should match";
        assert itemFromRow.getImageUrl().equals(testItem.getImageUrl()) : "Image URL should match";
        assert itemFromRow.getPhotoAuthor().equals(testItem.getPhotoAuthor()) : "Photo author should match";
        assert itemFromRow.getPhotoAuthorUrl().equals(testItem.getPhotoAuthorUrl()) : "Photo author URL should match";
        assert itemFromRow.getReviewScore() == testItem.getReviewScore() : "Review score should match";
        assert itemFromRow.getReviewCount() == testItem.getReviewCount() : "Review count should match";
        assert itemFromRow.getStockQuantity() == testItem.getStockQuantity() : "Stock quantity should match";
        assert itemFromRow.getIndex() == testItem.getIndex() : "Index should match";
    }

    @Test
    @DisplayName("Test Random Item Generation")
    public void testRandomItemGeneration(){
        Item randomItem = Item.randomItem();
        
        //ID
        assert randomItem.getId() != null && !randomItem.getId().isEmpty() : "Random item ID should not be null or empty";

        //Index
        assert randomItem.getIndex() >= 0 : "Random item index should be non-negative";

        //Image URL
        assert randomItem.getImageUrl() != null && !randomItem.getImageUrl().isEmpty() : "Random item image URL should not be null or empty";

        //Name
        assert randomItem.getName() != null && !randomItem.getName().isEmpty() : "Random item name should not be null or empty";

        //Publisher
        assert randomItem.getPublisher() != null && !randomItem.getPublisher().isEmpty() : "Random item publisher should not be null or empty";

        //Description
        assert randomItem.getDescription() != null && !randomItem.getDescription().isEmpty() : "Random item description should not be null or empty";

        //Category
        assert randomItem.getCategory() != null && !randomItem.getCategory().isEmpty() : "Random item category should not be null or empty";

        //Tags
        assert randomItem.getTags() != null && !randomItem.getTags().isEmpty() : "Random item tags should not be null or empty";
        assert randomItem.getTags().stream().allMatch(tag -> tag != null && !tag.isEmpty()) : "Random item tags should not contain null or empty values";

        //Photo Author
        assert randomItem.getPhotoAuthor() != null && !randomItem.getPhotoAuthor().isEmpty() : "Random item photo author should not be null or empty";

        //Photo Author URL
        assert randomItem.getPhotoAuthorUrl() != null && !randomItem.getPhotoAuthorUrl().isEmpty() : "Random item photo author URL should not be null or empty";

        //Price
        float price = randomItem.getPrice();
        assert price >= 5.0f && price <= 500.0f : "Random item price should be between 5.0 and 500.0";

        //Review Score
        float reviewScore = randomItem.getReviewScore();
        assert reviewScore >= 1.0f && reviewScore <= 5.0f : "Random item review score should be between 1.0 and 5.0";

        //Review Count
        short reviewCount = randomItem.getReviewCount();
        assert reviewCount >= 0 && reviewCount <= 1000 : "Random item review count should be between 0 and 1000";

        //Stock Quantity
        short stockQuantity = randomItem.getStockQuantity();
        assert stockQuantity >= 0 && stockQuantity <= 100 : "Random item stock quantity should be between 0 and 100";

        //Date Added
        Date dateAdded = randomItem.getDateAdded();
        assert dateAdded != null : "Random item date added should not be null";
        Date now = new Date();
        assert dateAdded.after(now) : "Random item date added should be in the future";

    }
}
