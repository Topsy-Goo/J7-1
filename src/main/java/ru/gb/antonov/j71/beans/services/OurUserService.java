package ru.gb.antonov.j71.beans.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gb.antonov.j71.beans.errorhandlers.UserNotFoundException;
import ru.gb.antonov.j71.beans.repositos.OurUserRepo;
import ru.gb.antonov.j71.entities.OurUser;
import ru.gb.antonov.j71.entities.Product;
import ru.gb.antonov.j71.entities.Role;

import java.security.Principal;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.gb.antonov.j71.Factory.STR_EMPTY;

@Service
@RequiredArgsConstructor
public class OurUserService implements UserDetailsService
{
    private final OurUserRepo ourUserRepo;
    private final RoleService roleService;
    //private final CartService cartService;
    //private final CartService cartService;

//-----------------------------------------------------------------------------------
    @Override
    @Transactional
    public UserDetails loadUserByUsername (String login)
    {
        String errMsg = String.format ("Логин (%s) не зарегистрирован.", login);
        OurUser ourUser = findByLogin(login)
                            .orElseThrow(()->new UsernameNotFoundException (errMsg));
                            //^ должно отправлять err.401 клиенту, но не отправляет, а пишет
                            // в консоль IDE. Регистрация в GlobalExceptionHandler не помогла.
                            // В общем, зарегистрированный юзер при перезапуске приложения не
                            // вызывает 401, а вызывает обычное исключение.
                            // И JwtRequestFilter.doFilterInternal ругается.

        //Заполняем и возвращаем спринговую версию юзера (у него есть ещё более подробный
        // конструктор)
        return new User(ourUser.getLogin(),
                        ourUser.getPassword(),
                        mapRolesToAuthorities (ourUser.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities (Collection<Role> roles)
    {
        return roles.stream()
                    .map (role->new SimpleGrantedAuthority(role.getName()))
                    .collect (Collectors.toList());
    }

    @Transactional
    public Optional<OurUser> createNewOurUser (String login, String password, String email)
    {
        OurUser dummyUser = OurUser.dummyOurUser (login, password, email);
        Optional<Role> optionalRole = roleService.getRoleUser();

        if (optionalRole.isPresent())
        {
            OurUser saved = ourUserRepo.save (dummyUser); //< is always 'not null' when returned
            saved.addRole (optionalRole.get()); //< как оказалось, создать для нового юзера
            // запись в сводной таблице можно просто добавив сущность-роль в список ролей юзера
            return Optional.of (saved);
        }
        return Optional.empty();
    }

    public Optional<OurUser> findByLogin (String login)
    {
        return ourUserRepo.findByLogin (login);
    }

    private OurUser findUserByPrincipal (Principal principal)
    {
        String login = (principal != null) ? principal.getName() : STR_EMPTY;
        String errMsg = "Логин не зарегистрирован: " + login;
        return findByLogin(login).orElseThrow (()->new UserNotFoundException (errMsg));
    }
//-------------------- Корзина ------------------------------------------------------

    public Integer addToCart (Product product, Principal principal, int quantity)
    {
        OurUser ourUser = findUserByPrincipal (principal);
        //CartService cartService = ourUser.getCart();
/*        cartService.addProductToUserCart (ourUser, product, quantity);
        return cartService.getUserCartSize (ourUser);*/
        return 0;
    }

    //@Transactional    TODO: раскомментировать, если будет вызываться непосредственно из контроллера.
    public Integer getCartItemsCount (Principal principal)
    {
/*        OurUser ourUser = findUserByPrincipal (principal);
        return cartService.getUserCartSize (ourUser);*/
        return 0;
    }

/*
    public List<Product> getUnmodifiableCart (Principal principal)
    {
        return findUserByPrincipal (principal).getCart();
    }

    public Integer removeFromCart (Product product, Principal principal)
    {
        OurUser ourUser = findUserByPrincipal (principal);
        ourUser.removeFromCart (product);
        return ourUser.getCartSize();
    }*/
}
