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

@Entity
@Table (name="ourusers")
@NoArgsConstructor
public class OurUser
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

//------------------------ Конструкторы -------------------------------------
    public static OurUser dummyOurUser (String login, String password, String email)
    {
        OurUser u = new OurUser();
        if (!u.setLogin (login) || !u.setPass (password) || !u.setEmail (email))
        {
            throw new UserCreatingException (String.format (
                "Недопустимый набор значений:\r    логин = %s,\r    пароль = %s,\r    почта = %s\r",
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

    public static String  validateString (String s, int minLen, int maxLen)
    {
        if (s != null && minLen > 0 && minLen <= maxLen)
        {
            s = s.trim();
            int len = s.length();
            if (len >= minLen && len <= maxLen)
            {
                return s;
            }
        }
        return null;
    }

    public static boolean hasEmailFormat (String email)
    {
        boolean ok = false;
        int at = email.indexOf ('@');
        if (at > 0 && email.indexOf ('@', at +1) < 0)
        {
            int point = email.indexOf ('.', at);
            ok = point >= at +2;
        }
        return ok;
    }
//--------------------- Другие методы ---------------------------------------

    @Override public String toString()
    {   return String.format("OurUser:[id:%d, login:%s, email:%s].", id, login, email);
    }
}
