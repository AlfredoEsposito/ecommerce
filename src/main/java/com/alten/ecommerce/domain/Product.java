package com.alten.ecommerce.domain;

import com.alten.ecommerce.enums.Categories;
import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonProperty("product name")
    @Column(name = "product_name", unique = true)
    private String productName;

    @Column(name = "category")
    @Enumerated(value = EnumType.STRING)
    private Categories category;

    @Column(name = "price")
    private Double price;

    @Column(name = "discount")
    private Double discount;

    @JsonProperty("quantity in stock")
    @Column(name = "quantity_in_stock")
    private Long quantityInStock;

    @JsonProperty("published at")
    @Column(name = "published")
    private LocalDateTime publishedAt;

    @JsonIgnore
    @ManyToMany(fetch =  FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "cart_products",
               joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "cart_id", referencedColumnName = "id"))
    private Set<Cart> cart;

    @JsonIgnore
    @ManyToMany(fetch =  FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "order_products",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "order_id", referencedColumnName = "id"))
    private Set<Order> orders;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name = "user_products",
               joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> users;

    @JsonProperty("cart items")
    @JsonIgnoreProperties({"id", "cart", "product"})
    @OneToMany(mappedBy = "product")
    private Set<CartItems> cartItems;

    public Product() {
    }

    public Product(String productName, Categories category, Double price, Double discount, Long quantityInStock, LocalDateTime publishedAt) {
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.discount = discount;
        this.quantityInStock = quantityInStock;
        this.publishedAt = publishedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Categories getCategory() {
        return category;
    }

    public void setCategory(Categories category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Long getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(Long quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Set<Cart> getCart() {
        return cart;
    }

    public void setCart(Set<Cart> cart) {
        this.cart = cart;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<CartItems> getCartItems() {
        return cartItems;
    }

    public void setCartItems(Set<CartItems> cartItems) {
        this.cartItems = cartItems;
    }

    //utility method: set the user seller to the product (M:M relationship)
    public void addUser(User user){
        if(users == null){
            users = new HashSet<>();
        }
        users.add(user);
    }

    public void addCartItem(CartItems cartItem){
        if(cartItems == null){
            cartItems = new HashSet<>();
        }
        cartItems.add(cartItem);
        cartItem.setProduct(this);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", category=" + category +
                ", price=" + price +
                ", discount=" + discount +
                ", quantityInStock=" + quantityInStock +
                ", publishedAt=" + publishedAt +
                '}';
    }
}
