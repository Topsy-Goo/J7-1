package ru.gb.antonov.j71.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data   @Entity    @Table (name="productreviews")
public class ProductReview
{
    @Id    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name="id")
    private Long id;

    @Column(name="text", nullable=false)
    private String text;

    @ManyToOne    @JoinColumn(name="ouruser_id", nullable=false)
    private OurUser ourUser;

    @Column(name="product_id", nullable=false)
    private Long productId;

    @CreationTimestamp    @Column(name="created_at")
    private LocalDateTime createdAt;

    @CreationTimestamp    @Column(name="updated_at")
    private LocalDateTime updatedAt;
//-----------------------------------------------------------
    public ProductReview (){}
//-----------------------------------------------------------
    @Override public String toString()
    {   return String.format ("review:[%d, u:%s, p:%s, «%s»]",
                              id, (ourUser == null) ? null : ourUser.getLogin(),
                              productId, text);
    }
}
