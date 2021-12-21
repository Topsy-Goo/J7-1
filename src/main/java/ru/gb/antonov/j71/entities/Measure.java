package ru.gb.antonov.j71.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity     @Table (name="measures")
public class Measure {

    @Id    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name="id")
    private Long id;

    @Column (name="name", nullable=false, unique=true)
    private String name;
//------------------------------------------------------------ конструкторы
    private Measure () {}
//------------------------------------------------------------ геттеры и сеттеры
    private void setId (Long value) { id = value; }
    public Long getId () { return id; }

    private void   setName (String value) { name = value; }
    public String getName ()             { return name; }
//------------------------------------------------------------ @Overrides
    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Measure measure = (Measure) o;
        return name.equals(measure.name);
    }

    @Override
    public int hashCode () {
        return Objects.hash (name);
    }

    @Override public String toString () { return String.format ("Measure[%d, %s]", id, name); }
//------------------------------------------------------------ */

    public static boolean isMeasureValid (Measure value) {
        String name;
        return value != null
               &&  (name = value.getName()) != null
               &&  !name.trim().isEmpty();
    }
}
