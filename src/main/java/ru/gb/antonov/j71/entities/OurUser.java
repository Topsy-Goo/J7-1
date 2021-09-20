package ru.gb.antonov.j71.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.gb.antonov.j71.beans.errorhandlers.UserCreatingException;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

import static ru.gb.antonov.j71.Factory.hasEmailFormat;
import static ru.gb.antonov.j71.Factory.validateString;

@Entity
@Table (name="ourusers")
@NoArgsConstructor
public class OurUser/* implements Comparable<OurUser>*/
{
    @Id @Getter
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name="id")
    private Long id;

    @Getter
    @Column(name="login", nullable=false, unique=true)
    private String login;

    @Getter
    @Column(name="password", nullable=false)
    private String password;

    @Getter
    @Column(name="email", nullable=false, unique=true)
    private String email;

    @Getter @Setter
    @CreationTimestamp
    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;

    @Getter @Setter
    @CreationTimestamp
    @Column(name="updated_at", nullable=false)
    private LocalDateTime updatedAt;
//--------------неколонки
    @Getter @Setter
    @ManyToMany
    @JoinTable (name="ourusers_roles",
                joinColumns        = @JoinColumn (name="ouruser_id"),
                inverseJoinColumns = @JoinColumn (name="role_id"))
    private Collection<Role> roles;

    @Setter
    @OneToMany (mappedBy = "ouruser")
    private List<Order> orders;
//------------------------ Конструкторы -------------------------------------

    public static OurUser dummyOurUser (String login, String password, String email)
    {
        OurUser u = new OurUser();
        if (!u.setLogin (login) || !u.setPass (password) || !u.setEmail (email))
        {
            throw new UserCreatingException (String.format (
                "\rНедопустимый набор значений:\r    логин = %s,\r    пароль = %s,\r    почта = %s",
                login, password, email));
        }
        u.roles = new HashSet<>();
        return u;
    }
//----------------------- Геттеры и сеттеры ---------------------------------

    private void setId (Long id) {   this.id = id;   }
    private void setPassword (String password) {   this.password = password;   }

    private boolean setLogin (String login)
    {
        String s = validateString (login, 3, 32);
        boolean ok = s != null;
        if (ok)
            this.login = s;
        return ok;
    }

    private boolean setEmail (String email)
    {
        String s = validateString (email, 5, 64);
        boolean ok = s != null && hasEmailFormat (email);
        if (ok)
            this.email = s;
        return ok;
    }

    public List<Order> getOrders() { return Collections.unmodifiableList (orders); }

//----------------------- Аутентификация ------------------------------------

/*  Отдельный метод для установки пароля вручную, чтобы иметь возможность сообщать юзеру о некорректно
заданном пароле и при этом выводить в сообщении пароль, а не хэш пароля.
*/
    private boolean setPass (String password)
    {
        String s = validateString (password, 3, 128);
        boolean ok = s != null;
        if (ok)
            setPassword (new BCryptPasswordEncoder ().encode (s));
        return ok;
    }

    public boolean addRole (Role role)  {   return (role != null) && roles.add (role);   }
//--------------------- Другие методы ---------------------------------------

    @Override public String toString()
    {   return String.format("OurUser:[id:%d, login:%s, email:%s].", id, login, email);
    }

    /**
     Compares this object with the specified object for order.  Returns a
     negative integer, zero, or a positive integer as this object is less
     than, equal to, or greater than the specified object.

     <p>The implementor must ensure
     {@code sgn(x.compareTo(y)) == -sgn(y.compareTo(x))}
     for all {@code x} and {@code y}.  (This
     implies that {@code x.compareTo(y)} must throw an exception iff
     {@code y.compareTo(x)} throws an exception.)

     <p>The implementor must also ensure that the relation is transitive:
     {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
     {@code x.compareTo(z) > 0}.

     <p>Finally, the implementor must ensure that {@code x.compareTo(y)==0}
     implies that {@code sgn(x.compareTo(z)) == sgn(y.compareTo(z))}, for
     all {@code z}.

     <p>It is strongly recommended, but <i>not</i> strictly required that
     {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any
     class that implements the {@code Comparable} interface and violates
     this condition should clearly indicate this fact.  The recommended
     language is "Note: this class has a natural ordering that is
     inconsistent with equals."

     <p>In the foregoing description, the notation
     {@code sgn(}<i>expression</i>{@code )} designates the mathematical
     <i>signum</i> function, which is defined to return one of {@code -1},
     {@code 0}, or {@code 1} according to whether the value of
     <i>expression</i> is negative, zero, or positive, respectively.

     @param o the object to be compared.

     @return a negative integer, zero, or a positive integer as this object
     is less than, equal to, or greater than the specified object.

     @throws NullPointerException if the specified object is null
     @throws ClassCastException if the specified object's type prevents it
     from being compared to this object.
     */
/*    @Override public int compareTo (OurUser o)
    {
        if (o == null) throw new NullPointerException ("Вызов OurUser.compareTo (null).");
        if (this.id == null)
            throw new NullPointerException ("Вызов OurUser.compareTo() сделан при this.id == null.");
        if (o.getId() == null)
            throw new NullPointerException ("В OurUser.compareTo() передан экземпляр OurUser с id == null.");
        return this.id.compareTo (o.getId());
    }*/

}
