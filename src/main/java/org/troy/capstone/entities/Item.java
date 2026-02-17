package org.troy.capstone.entities;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.datafaker.Faker;

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
    private float price;
    private float reviewScore;
    private short reviewCount;
    private short stockQuantity;
    private Date dateAdded;

    public static Item randomItem(){
        return Item.builder()
            .imageUrl( faker.internet().image() )
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
            .id( faker.internet().uuid() )
            .build();
    }
}
