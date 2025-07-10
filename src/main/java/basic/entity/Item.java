package basic.entity;

import basic.exception.NotEnoughStockException;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    private String imageFileName;

    protected Item() {}

    private Item(String name, int price, int stockQuantity, String imageFileName){
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imageFileName = imageFileName;
    }

    public static Item of(String name, int price, int stockQuantity, String imageFileName) {
        return new Item(name, price, stockQuantity, imageFileName);
    }

    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }


}