package ru.gb.antonov.j71.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

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
//------------------------------------------------------------
    @Override
    public boolean equals (Object o)
    {
        if (this == o)   return true;
        if (o == null || getClass () != o.getClass ())   return false;
        Role role = (Role) o;
        return name.equals (role.name);
    }
    @Override
    public int hashCode () { return Objects.hash (name); }

}
