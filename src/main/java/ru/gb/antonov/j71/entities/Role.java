package ru.gb.antonov.j71.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity    @Data    @Table (name="roles")
public class Role {

    @Id    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name="id")
    private Long id;

    @Column (name="name", nullable=false, unique=true, length=64)
    private String name;

    @CreationTimestamp    @Column(name="created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp    @Column(name="updated_at")
    private LocalDateTime updatedAt;
//------------------------------------------------------------
    private Role (){}
//------------------------------------------------------------
    private void setId (Long value) { id = value; }
    private void setUpdatedAt (LocalDateTime value) { updatedAt = value; }
    private void setCreatedAt (LocalDateTime value) { createdAt = value; }
//------------------------------------------------------------
    @Override public boolean equals (Object o) {
        if (this == o)   return true;
        if (o == null || getClass () != o.getClass ())   return false;
        Role role = (Role) o;
        return name.equals (role.name);
    }
    @Override public int hashCode () { return Objects.hash (name); }
}
