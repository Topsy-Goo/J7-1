package ru.gb.antonov.j71.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table (name="orderitems")
public class OrderItem
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne
    @JoinColumn (name="order_id", nullable=false)
    private Order order;

    @ManyToOne
    @JoinColumn(name="product_id", nullable=false)
    private Product product;

    @Column(name="buying_price")
    private double buyingPrice;

    @Column(name="quantity")
    private int quantity;

    @CreationTimestamp
    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column (name="updated_at", nullable=false)
    private LocalDateTime updatedAt;
//-----------------------------------------------------------------
    @Override public String toString()
    {
        return String.format ("OrderItem:[id:%d, oid:%d, prod:%s, bp:%.2f, qt:%d].",
                              id, order.getId(), product.getTitle(), buyingPrice, quantity);
    }
}
