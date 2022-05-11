package com.alten.ecommerce.domain;

import com.alten.ecommerce.enums.Status;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "order")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonProperty("shipping address")
    @Column(name = "shipping_address")
    private String shippingAddress;

    @JsonProperty("shipping costs")
    @Column(name = "shipping_costs")
    private Double shippingCosts;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @JsonProperty("products quantity")
    @Column(name = "products_quantity")
    private Long productsQuantity;

    @Column(name = "total")
    private Double total = totalOrder();

    @JsonProperty("ordered at")
    @Column(name = "ordered")
    private LocalDateTime orderedAt;

    @JsonProperty("shipping date")
    @Column(name = "shipping_date")
    private LocalDateTime shippingDate;

    @JsonIgnoreProperties({"firstName", "lastName", "password", "type", "company", "cart", "products", "orders"})
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @JsonProperty("order products")
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "order_products",
            joinColumns = @JoinColumn(name = "order_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
    private Set<Product> products;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    public Order() {
    }

    public Order(String shippingAddress, Double shippingCosts, Status status, Double total, LocalDateTime orderedAt, LocalDateTime shippingDate, Long productsQuantity) {
        this.shippingAddress = shippingAddress;
        this.shippingCosts = shippingCosts;
        this.status = status;
        this.total = total;
        this.orderedAt = orderedAt;
        this.shippingDate = shippingDate;
        this.productsQuantity = productsQuantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Double getShippingCosts() {
        return shippingCosts;
    }

    public void setShippingCosts(Double shippingCosts) {
        this.shippingCosts = shippingCosts;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(LocalDateTime orderedAt) {
        this.orderedAt = orderedAt;
    }

    public LocalDateTime getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(LocalDateTime shippingDate) {
        this.shippingDate = shippingDate;
    }

    public Long getProductsQuantity() {
        return productsQuantity;
    }

    public void setProductsQuantity(Long productsQuantity) {
        this.productsQuantity = productsQuantity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }


    public Double totalOrder(){
        Double total = null;
        if(this.user!=null){
            total = this.user.getCart().getTotalAmount();
        }
        return total;
    }

    public void addProduct(Product product){
        if(products == null){
            products = new HashSet<>();
        }
        products.add(product);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", shippingCosts=" + shippingCosts +
                ", status=" + status +
                ", total=" + total +
                ", orderedAt=" + orderedAt +
                ", shippingDate=" + shippingDate +
                ", user=" + user +
                '}';
    }
}
