package org.troy.capstone.uiMock;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.datafaker.Faker;

@Data
@AllArgsConstructor
@Builder
public class Item {
    private static final Faker faker = new Faker();

    private Image image;
    private String name;
    private String publisher;
    private String description;
    private String category;
    private Set<String> tags;
    private double price;
    private double reviewScore;
    private int reviewCount;
    private int stockQuantity;
    private Date dateAdded;

    public static Item randomItem(){
        return Item.builder()
            .image( new Image( faker.internet().image() ) )
            .name( faker.commerce().productName() )
            .publisher( faker.company().name() )
            .description( String.join("", faker.lorem().sentences(2) ) )
            .category( faker.commerce().department() )
            .tags( Set.copyOf( faker.lorem().words(3) ) )
            .price( faker.number().randomDouble(2, 5, 500) )
            .reviewScore( faker.number().randomDouble(1, 1, 5) )
            .reviewCount( faker.number().numberBetween(0, 1000) )
            .stockQuantity( faker.number().numberBetween(0, 100) )
            .dateAdded( Date.from( faker.timeAndDate().future(365, TimeUnit.DAYS) ) )
            .build();
    }
}
