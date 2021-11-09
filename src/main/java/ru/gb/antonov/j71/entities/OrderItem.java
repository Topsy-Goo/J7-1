package ru.gb.antonov.j71.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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

    @Column (name="buying_price", nullable=false)
    private BigDecimal buyingPrice = BigDecimal.ZERO;

    @Column (name="quantity", nullable=false)
    private int quantity;

    @CreationTimestamp    @Column (name="created_at")
    private LocalDateTime createdAt;

    @CreationTimestamp    @Column (name="updated_at")
    private LocalDateTime updatedAt;
//-----------------------------------------------------------------
    public OrderItem () {}
//-----------------------------------------------------------------
    @Override public String toString() {
        return String.format ("OrderItem:[id:%d, oid:%d, pid:%s, bp:%.2f, qt:%d].",
                              id, order.getId(), product.getTitle(), buyingPrice, quantity);
    }
}
