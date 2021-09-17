package ru.gb.antonov.j71.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Column (name="state", nullable=false, unique=true)
    private String state;       //NONE, PENDING, SERVING, PAYED, CANCELED;

    @Column (name="friendly_name")
    private String friendlyName;
}
