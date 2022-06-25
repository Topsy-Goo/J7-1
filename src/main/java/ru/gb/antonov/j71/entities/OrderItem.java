package ru.gb.antonov.j71.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity     @Data    @Table (name="orderitems")
public class OrderItem {

    @Id    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne    @JoinColumn (name="order_id", nullable=false)
    private Order order;

    @ManyToOne    @JoinColumn (name="product_id", nullable=false)
    private Product product;

    @Column (name="buying_price", nullable=false, precision=10, scale=2)
    private BigDecimal buyingPrice = BigDecimal.ZERO;

    @Column (name="quantity", nullable=false)
    private int quantity;

    @CreationTimestamp    @Column (name="created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp    @Column (name="updated_at")
    private LocalDateTime updatedAt;
//-----------------------------------------------------------------
    public OrderItem () {}
//-----------------------------------------------------------------
    private void setId (Long value) { id = value; }
    private void setUpdatedAt (LocalDateTime value) { updatedAt = value; }
    private void setCreatedAt (LocalDateTime value) { createdAt = value; }
//-----------------------------------------------------------------
    @Override public String toString() {
        return String.format ("Oitem:[id:%d, oid:%d, pid:%s, bp:%.2f, qt:%d].",
                              id, order.getId(), product.getTitle(), buyingPrice, quantity);
    }
}
