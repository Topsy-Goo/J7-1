package ru.gb.antonov.j71.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.gb.antonov.j71.Factory.*;

@Entity    @Data   @Table (name="productreviews")
public class ProductReview {

    @Id    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name="id")
    private Long id;

    @Column(name="text", nullable=false, length= PRODUCTREVIEW_LEN_MAX)
    private String text;

    @ManyToOne    @JoinColumn(name="ouruser_id", nullable=false)
    private OurUser ourUser;

    @Column(name="product_id", nullable=false)
    private Long productId;

    @CreationTimestamp    @Column(name= COLNAME_CREATED_AT)
    private LocalDateTime createdAt;

    @UpdateTimestamp    @Column(name= COLNAME_UPDATED_AT)
    private LocalDateTime updatedAt;
//-----------------------------------------------------------
    public ProductReview (){}
//-----------------------------------------------------------
    private void setId (Long value) { id = value; }
    private void setUpdatedAt (LocalDateTime value) { updatedAt = value; }
    private void setCreatedAt (LocalDateTime value) { createdAt = value; }
//-----------------------------------------------------------
    @Override public String toString() {
        return String.format ("review:[%d, u:%s, p:%s, «%s»]",
                              id, (ourUser == null) ? null : ourUser.getLogin(),
                              productId, text);
    }
}
