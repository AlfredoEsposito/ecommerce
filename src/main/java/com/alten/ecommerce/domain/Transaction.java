package com.alten.ecommerce.domain;

import com.alten.ecommerce.enums.PaymentMethods;
import com.alten.ecommerce.enums.Status;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @JsonProperty("payment method")
    @Column(name = "payment_method")
    @Enumerated(value = EnumType.STRING)
    private PaymentMethods paymentMethods;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @JsonProperty("extecuted at")
    @Column(name = "executed")
    private LocalDateTime executedAt;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
              mappedBy = "transaction")
    private Order order;

    public Transaction() {
    }

    public Transaction(String code, PaymentMethods paymentMethods, Status status, LocalDateTime executedAt) {
        this.code = code;
        this.paymentMethods = paymentMethods;
        this.status = status;
        this.executedAt = executedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public PaymentMethods getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(PaymentMethods paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", paymentMethods=" + paymentMethods +
                ", status=" + status +
                ", executedAt=" + executedAt +
                ", order=" + order +
                '}';
    }
}
