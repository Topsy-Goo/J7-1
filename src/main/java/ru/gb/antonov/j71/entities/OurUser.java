package ru.gb.antonov.j71.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.gb.antonov.j71.beans.errorhandlers.UserCreationException;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

import static ru.gb.antonov.j71.Factory.*;

@Entity
@Table (name="ourusers")
public class OurUser implements Buildable<OurUser> {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY) @Getter
    @Column (name="id")
    private Long id;

    @Column(name="login", nullable=false, unique=true, length= LOGIN_LEN_MAX)  @Getter
    private String login;

    @Column(name="password", nullable=false, length= PASS_LEN_MAX)            @Getter
    private String password;

    @Column(name="email", nullable=false, unique=true, length= EMAIL_LEN_MAX)  @Getter
    private String email;

    @CreationTimestamp    @Column(name= COLNAME_CREATED_AT)    @Getter @Setter
    private LocalDateTime createdAt;

    @UpdateTimestamp      @Column(name= COLNAME_UPDATED_AT)    @Getter @Setter
    private LocalDateTime updatedAt;
//--------------неколонки
    @ManyToMany
    @JoinTable (name="ourusers_roles",
                joinColumns        = @JoinColumn (name="ouruser_id"),
                inverseJoinColumns = @JoinColumn (name="role_id"))    @Getter @Setter
    private Collection<Role> roles;
/*  Роли и разрешения не стоит убирать из класса т.к. при создании юзера они должны сохарняться одновременно с ним.   */
    @ManyToMany
    @JoinTable (name="ourusers_ourpermissions",
                joinColumns        = @JoinColumn (name="ouruser_id"),
                inverseJoinColumns = @JoinColumn (name="ourpermission_id"))    @Getter @Setter
    private Collection<OurPermission> ourPermissions;

//------------------------ Конструкторы -------------------------------------
    private OurUser () {
        roles          = new HashSet<>();
        ourPermissions = new HashSet<>();
    }

/** Создаёт пустой объект OurUser и начинает цепочку методов, каждый из которых проверяет валидность
изменяемого параметра.
@return ссылка на объект OurUser   */
    public static OurUser create () {
        return new OurUser();
    }
/**
@return this
@throws UserCreationException */
    public OurUser withLogin (String newLogin) {
        if (!setLogin (newLogin))
            throw new UserCreationException ("\rнекорректный логин : "+ newLogin);
        return this;
    }
/**
@return this
@throws UserCreationException */
    public OurUser withPassword (String newPassword) {
        if (!setPass (newPassword))
            throw new UserCreationException ("\rнекорректный пароль : "+ newPassword);
        return this;
    }
/**
@return this
@throws UserCreationException */
    public OurUser withEmail (String newEmail) {
        if (!setEmail (newEmail))
            throw new UserCreationException ("\rнекорректный адрес электронной почты : "+ newEmail);
        return this;
    }
/** Завершает инициализацию объекта OurUser.
@return this    */
    @Override public OurUser build () {
        if (login == null || password == null || email == null)
            throw new UserCreationException (USE_DEFAULT_STRING);
        return this;
    }
//----------------------- Геттеры и сеттеры ---------------------------------

    private void setId (Long id) {   this.id = id;   }

    private void setPassword (String password) {   this.password = password;   }

    private boolean setLogin (String login) {
        String s = validateString (login, LOGIN_LEN_MIN, LOGIN_LEN_MAX);
        boolean ok = s != null;
        if (ok)
            this.login = s;
        return ok;
    }

    private boolean setEmail (String email) {
        String s = validateString (email, EMAIL_LEN_MIN, EMAIL_LEN_MAX);
        boolean ok = s != null && hasEmailFormat (email);
        if (ok)
            this.email = s;
        return ok;
    }

    private void setUpdatedAt (LocalDateTime value) { updatedAt = value; }
    private void setCreatedAt (LocalDateTime value) { createdAt = value; }
//----------------------- Аутентификация ------------------------------------
/**  Отдельный метод для установки пароля вручную, чтобы иметь возможность сообщать юзеру о некорректно
заданном пароле и при этом выводить в сообщении пароль, а не хэш пароля.
*/
    private boolean setPass (String password) {
        String s = validateString (password, PASS_LEN_MIN, PASS_LEN_MAX);
        boolean ok = s != null;
        if (ok)
            setPassword (new BCryptPasswordEncoder().encode(s));
        return ok;
    }

    public boolean addRole (Role role) { return (role != null) && roles.add (role); }

    public boolean addPermission (OurPermission permission) {
        return (permission != null) && ourPermissions.add (permission);
    }
//--------------------- Другие методы ---------------------------------------

    @Override  public String toString () {
        return String.format("OurUser:[id:%d, login:%s, email:%s].", id, login, email);
    }
}
