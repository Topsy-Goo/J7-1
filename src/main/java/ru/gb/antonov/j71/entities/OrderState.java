package ru.gb.antonov.j71.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table (name="orderstates")
public class OrderState
{
    @Id
    @Column (name="id")
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (name="short_name", nullable=false, unique=true)
    private String shortName;       //NONE, PENDING, SERVING, PAYED, CANCELED;

    @Column (name="friendly_name", nullable=false, unique=true)
    private String friendlyName;

    @CreationTimestamp    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;

    @CreationTimestamp    @Column (name="updated_at", nullable=false)
    private LocalDateTime updatedAt;
}
