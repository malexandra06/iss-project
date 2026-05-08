package minishop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

@jakarta.persistence.Entity
@Table(name = "Products")
public class Product extends Entity<String>{
    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String category;

    @Column(nullable = false)
    private double price;

    @Column(length = 100, nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate fabricationDate;

    @Column(nullable = false)
    private int noItems;

    @Column(length = 50, nullable = false)
    private String photoName;

    public Product() {}

    public Product(String name, String category, Double price, String description, LocalDate fabricationDate, int noItems, String photoName) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.fabricationDate = fabricationDate;
        this.noItems = noItems;
        this.photoName = photoName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getFabricationDate() {
        return fabricationDate;
    }

    public void setFabricationDate(LocalDate fabricationDate) {
        this.fabricationDate = fabricationDate;
    }

    public int getNoItems() {
        return noItems;
    }

    public void setNoItems(int noItems) {
        this.noItems = noItems;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Product product = (Product) o;
        return noItems == product.noItems && Objects.equals(name, product.name) && Objects.equals(category, product.category) && Objects.equals(price, product.price) && Objects.equals(description, product.description) && Objects.equals(fabricationDate, product.fabricationDate) && Objects.equals(photoName, product.photoName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, category, price, description, fabricationDate, noItems, photoName);
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", fabricationDate=" + fabricationDate +
                ", noItems=" + noItems +
                ", photoName='" + photoName + '\'' +
                '}';
    }
}
