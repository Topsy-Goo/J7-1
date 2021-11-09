package ru.gb.antonov.j71.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity    @Data    @Table (name="orderstates")
public class OrderState {

    @Id    @Column (name="id")
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (name="short_name", nullable=false, unique=true)
    private String shortName;   //NONE, PENDING, SERVING, PAYED, CANCELED;

    @Column (name="friendly_name", nullable=false, unique=true)
    private String friendlyName;

    @CreationTimestamp    @Column (name="created_at")
    private LocalDateTime createdAt;

    @CreationTimestamp    @Column (name="updated_at")
    private LocalDateTime updatedAt;
//---------------------------------------------------------------------
    public OrderState (){}
//---------------------------------------------------------------------
    @Override public String toString () {
        return String.format("%d, %s, %s", id, shortName, friendlyName);
    }
}
