package com.alten.ecommerce.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "cart")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "total_amount")
    private Double totalAmount = this.getTotalAmount();

    @Column(name = "total_items")
    private Long totalItems;

    @JsonIgnoreProperties({"firstName", "lastName", "username", "email", "password", "type", "company", "roles", "products", "orders"})
    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "cart_products",
               joinColumns = @JoinColumn(name = "cart_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
    private Set<Product> products;

    public Cart() {
    }

    public Cart(Long id, Double totalAmount, Long totalItems) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.totalItems = totalItems;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Double getTotalAmount(){
        Double total=0.0;
        if(products!=null){
            for(Product product : this.products){
                total = total + (product.getPrice()-product.getDiscount());
            }
        }
        return total;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Long totalItems) {
        this.totalItems = totalItems;
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



    public void addProduct(Product product){
        if(products == null){
            products = new HashSet<>();
        }
        products.add(product);
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", totalAmount=" + totalAmount +
                ", totalItems=" + totalItems +
                ", user=" + user +
                ", products=" + products +
                '}';
    }
}
