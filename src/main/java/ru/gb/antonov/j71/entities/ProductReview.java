package ru.gb.antonov.j71.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table (name="productreviews")
public class ProductReview
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name="id")
    private Long id;

    @Column(name="text")
    private String text;

    @ManyToOne
    @JoinColumn(name="ouruser_id", nullable=false)
    private OurUser ourUser;

    @ManyToOne
    @JoinColumn(name="product_id", nullable=false)
    private Product product;

    @CreationTimestamp
    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;
//-----------------------------------------------------------
    @Override public String toString()
    {   return String.format ("review:[%d, u:%s, p:%s, «%s»]",
                              id,
                              (ourUser == null) ? null : ourUser.getLogin(),
                              (product == null) ? null : product.getTitle(),
                              text);
    }
}
