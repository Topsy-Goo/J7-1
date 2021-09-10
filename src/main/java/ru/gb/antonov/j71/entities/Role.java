package ru.gb.antonov.j71.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table (name="roles")
public class Role
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name="id")
    private Long id;

    @Column (name="name", nullable=false, unique=true)
    private String name;

    @CreationTimestamp
    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(name="updated_at", nullable=false)
    private LocalDateTime updatedAt;


/*  @ManyToMany   < Видимо, эта часть нужна только, если мы работаем с ролями и их списками юзеров.
    @JoinTable (name="ourusers_roles",
                joinColumns        = @JoinColumn (name="role_id"),
                inverseJoinColumns = @JoinColumn (name="ouruser_id"))
    private Collection<OurUser> ourusers; //*/

    //public Role() {}
}
