package org.troy.capstone.entities;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.troy.capstone.constants.URLs;
import org.troy.capstone.constants.tableColumns;
import org.troy.capstone.utils.Converters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.datafaker.Faker;
import tech.tablesaw.api.Row;

@Data
@AllArgsConstructor
@Builder
public class Item {
    private static final Faker faker = new Faker();

private String id;
    private String imageUrl;
    private String name;
    private String publisher;
    private String description;
    private String category;
    private Set<String> tags;
    private String photoAuthor;
    private String photoAuthorUrl;
    private float price;
    private float reviewScore;
    private short reviewCount;
    private short stockQuantity;
    private Date dateAdded;

    public static Item randomItem(){
        return Item.builder()
            .imageUrl( URLs.DEFAULT_IMAGE_URL )
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
            .photoAuthor( URLs.DEFAULT_AUTHOR_NAME )
            .photoAuthorUrl( URLs.DEFAULT_AUTHOR_URL )
            .id( faker.internet().uuid() )
            .build();
    }

    public static Item fromRow( Row itemRow ){
        return Item.builder()
                .imageUrl( itemRow.getString(tableColumns.IMAGE_URL.getColumnName()) )
                .name( itemRow.getString(tableColumns.NAME.getColumnName()) )
                .publisher( itemRow.getString(tableColumns.PUBLISHER.getColumnName()) )
                .description( itemRow.getString(tableColumns.DESCRIPTION.getColumnName()) )
                .category( itemRow.getString(tableColumns.CATEGORY.getColumnName()) )
                .tags( new HashSet<>( Arrays.asList( itemRow.getString(tableColumns.TAGS.getColumnName()).split(", ") ) ) )
                .price( itemRow.getFloat(tableColumns.PRICE.getColumnName()) )
                .reviewScore( itemRow.getFloat(tableColumns.REVIEW_SCORE.getColumnName()) )
                .reviewCount( itemRow.getShort(tableColumns.REVIEW_COUNT.getColumnName()) )
                .stockQuantity( itemRow.getShort(tableColumns.STOCK_QUANTITY.getColumnName()) )
                .id( itemRow.getString(tableColumns.ID.getColumnName()) )
                .photoAuthor( itemRow.getString(tableColumns.PHOTO_AUTHOR.getColumnName()) )
                .photoAuthorUrl( itemRow.getString(tableColumns.PHOTO_AUTHOR_URL.getColumnName()) )
                .dateAdded( Converters.localDateToDate(itemRow.getDate(tableColumns.DATE_ADDED.getColumnName())) )
            .build();
    }
}
