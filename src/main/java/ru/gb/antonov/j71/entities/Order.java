package ru.gb.antonov.j71.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table (name="orders")
public class Order
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name="id")
    private Long id;

    @Column (name="phone")
    private String phone;

    @Column (name="address")
    private String address;

/*  @ManyToOne
    @JoinColumn (name="orderstates", nullable=false)
    private String state;*/

    @ManyToOne
    @JoinColumn(name="ouruser_id", nullable=false)
    private OurUser ouruser;

    @CreationTimestamp
    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column (name="updated_at", nullable=false)
    private LocalDateTime updatedAt;

//--------неколонки
    @OneToMany (mappedBy="order", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<OrderItem> orderItems;
    //У OrderItem'мов не нужно указывать cascade, т.к. мы их тянем за собой в БД,
    // а не они нас.
//----------------------------------------------------------------------
    @Override public String toString()
    {   return String.format ("Order:[id:%d, u:%s, phone:%s, addr:%s]_with_[%s]",
                              id, ouruser.getLogin(), phone, address, orderItems);
    }
}
