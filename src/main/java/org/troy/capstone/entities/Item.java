package org.troy.capstone.entities;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.troy.capstone.constants.ItemSimilarityWeights;
import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.constants.URL;
import org.troy.capstone.utils.Converters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datafaker.Faker;
import tech.tablesaw.api.Row;


/**
 * A shopping item with various attributes. Main entity of the application.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    /** Faker instance for generating random data. */
    private static final Faker faker = new Faker();

    /** Unique identifier for the item. */
    private String id;
    /** Index of the item in the collection. */
    private short index;
    /** URL of the item's image. */
    private String imageUrl;
    /** Name of the item. */
    private String name;
    /** Publisher of the item. */
    private String publisher;
    /** Description of the item. */
    private String description;
    /** Category of the item. */
    private String category;
    /** Tags associated with the item. */
    private Set<String> tags;
    /** Author of the item's photo. */
    private String photoAuthor;
    /** URL of the photo's author. */
    private String photoAuthorUrl;
    /** Price of the item. */
    private float price;
    /** Review score of the item. */
    private float reviewScore;
    /** Number of reviews for the item. */
    private short reviewCount;
    /** Quantity of the item in stock. */
    private short stockQuantity;
    /** Date the item was added to the collection. */
    private Date dateAdded;
    /** Jaro-Winkler similarity instance for calculating string similarity. */
    private static final JaroWinklerSimilarity JARO_WINKLER_SIMILARITY = new JaroWinklerSimilarity();

    /**
     * Returns the value of the specified attribute for this item. The attribute is determined by the provided TableColumnName enum value.
     * 
     * @pre column is not null and corresponds to a valid attribute of the Item class.
     * 
     * @param column An enum value representing the attribute to retrieve from this item.
     * @return The value of the specified attribute for this item, returned as an Object. The caller must cast result.
     * @throws IllegalArgumentException If the provided column does not correspond to a valid attribute of the Item class.
     */
    public Object getAttribute( TableColumnName column ){
        return switch(column){
            case INDEX -> index;
            case ID -> id;
            case IMAGE_URL -> imageUrl;
            case NAME -> name;
            case PUBLISHER -> publisher;
            case DESCRIPTION -> description;
            case CATEGORY -> category;
            case TAGS -> tags;
            case PRICE -> price;
            case REVIEW_SCORE -> reviewScore;
            case REVIEW_COUNT -> reviewCount;
            case STOCK_QUANTITY -> stockQuantity;
            case DATE_ADDED -> dateAdded;
            case PHOTO_AUTHOR -> photoAuthor;
            case PHOTO_AUTHOR_URL -> photoAuthorUrl;
            default -> throw new IllegalArgumentException("Unexpected value: " + column);
        };
    }
    
    /**
     * Generates a random Item object with realistic values for each attribute using the Faker library.
     * 
     * @return A randomly generated Item object with all attributes populated with realistic random values.
     */
    public static Item randomItem(){
        return Item.builder()
            .imageUrl( URL.DEFAULT_IMAGE_URL.getUrl() )
            .name( faker.commerce().productName() )
            .publisher( faker.company().name() )
            .description( String.join(" ", faker.lorem().sentences(2) ) )
            .category( faker.commerce().department() )
            .tags( Set.copyOf( faker.lorem().words(3) ) )
            .price( (float) faker.number().randomDouble(2, 5, 500) )
            .reviewScore( (float) faker.number().randomDouble(1, 1, 5) )
            .reviewCount( (short) faker.number().numberBetween(0, 1000) )
            .stockQuantity( (short) faker.number().numberBetween(0, 100) )
            .dateAdded( Date.from( faker.timeAndDate().future(365, TimeUnit.DAYS) ) )
            .photoAuthor( URL.DEFAULT_AUTHOR_NAME.getUrl() )
            .photoAuthorUrl( URL.DEFAULT_AUTHOR_URL.getUrl() )
            .id( faker.internet().uuid() )
            .build();
    }

    /**
     * Creates an Item object from a tablesaw Row. The Row must contain columns corresponding to the attributes of the Item class.
     * 
     * @pre itemRow is not null and contains the expected columns for creating an Item (ID, Name, etc.).
     * 
     * @param itemRow A Row from a tablesaw Table containing item info.
     * @return An Item object created from the data in the provided Row.
     */
    public static Item fromRow( Row itemRow ){
        return Item.builder()
                .index( itemRow.getShort(TableColumnName.INDEX.getColumnName()) )
                .imageUrl( itemRow.getString(TableColumnName.IMAGE_URL.getColumnName()) )
                .name( itemRow.getString(TableColumnName.NAME.getColumnName()) )
                .publisher( itemRow.getString(TableColumnName.PUBLISHER.getColumnName()) )
                .description( itemRow.getString(TableColumnName.DESCRIPTION.getColumnName()) )
                .category( itemRow.getString(TableColumnName.CATEGORY.getColumnName()) )
                .tags( new HashSet<>( Arrays.asList( itemRow.getString(TableColumnName.TAGS.getColumnName()).split(", ") ) ) )
                .price( itemRow.getFloat(TableColumnName.PRICE.getColumnName()) )
                .reviewScore( itemRow.getFloat(TableColumnName.REVIEW_SCORE.getColumnName()) )
                .reviewCount( itemRow.getShort(TableColumnName.REVIEW_COUNT.getColumnName()) )
                .stockQuantity( itemRow.getShort(TableColumnName.STOCK_QUANTITY.getColumnName()) )
                .id( itemRow.getString(TableColumnName.ID.getColumnName()) )
                .photoAuthor( itemRow.getString(TableColumnName.PHOTO_AUTHOR.getColumnName()) )
                .photoAuthorUrl( itemRow.getString(TableColumnName.PHOTO_AUTHOR_URL.getColumnName()) )
                .dateAdded( Converters.localDateToDate(itemRow.getDate(TableColumnName.DATE_ADDED.getColumnName())) )
            .build();
    }

    /**
     * Calculates a similarity score between this item and another item based on shared attributes such as category, publisher, tags, and price. The similarity score is a float value between 0.0 and 1.0, where higher values indicate greater similarity.
     * 
     * @param other The other Item to compare against this item for similarity.
     * @return A float value representing the similarity between the two items.
    */
    public float similarity(Item other){
        float similarity = 0.0f;
        similarity += ItemSimilarityWeights.NAME.getWeight() * JARO_WINKLER_SIMILARITY.apply(this.name, other.name);
        similarity += ItemSimilarityWeights.PUBLISHER.getWeight() * (this.publisher.equals(other.publisher) ? 1.0f : 0.0f);
        similarity += ItemSimilarityWeights.DESCRIPTION.getWeight() * JARO_WINKLER_SIMILARITY.apply(this.description, other.description);
        similarity += ItemSimilarityWeights.CATEGORY.getWeight() * (this.category.equals(other.category) ? 1.0f : 0.0f);
        similarity += ItemSimilarityWeights.TAGS.getWeight() * (float) this.tags.stream().filter(other.tags::contains).count();
        similarity += ItemSimilarityWeights.PHOTO_AUTHOR.getWeight() * (this.photoAuthor.equals(other.photoAuthor) ? 1.0f : 0.0f);
        similarity += ItemSimilarityWeights.PRICE.getWeight() * Math.abs(this.price - other.price) / 500.0f; //Normalize price difference to [0, 1] range based on a high price
        similarity += ItemSimilarityWeights.REVIEW_SCORE.getWeight() * Math.abs(this.reviewScore - other.reviewScore) / 5.0f; //Normalize review score difference to [0, 1] range based on max score of 5
        similarity += ItemSimilarityWeights.DATE_ADDED.getWeight() * ((float) Math.abs(this.dateAdded.getTime() - other.dateAdded.getTime()) / TimeUnit.DAYS.toMillis(1000)); //Normalize date difference to [0, 1] range based on 1000 days
        return similarity;
    }
}
