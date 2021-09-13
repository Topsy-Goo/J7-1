package ru.gb.antonov.j71.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Column(name="bying_price")
    private double byingPrice;

    @Column(name="quantity")
    private int quantity;
//-----------------------------------------------------------------
    @Override public String toString()
    {
        return String.format ("OrderItem:[id:%d, oid:%d, prod:%s, bp:%.2f, qt:%d].",
                              id, order.getId(), product.getTitle(), byingPrice, quantity);
    }
}
