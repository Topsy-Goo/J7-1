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
import ru.gb.antonov.j71.entities.Role;
import ru.gb.antonov.j71.entities.dtos.UserInfoDto;

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
//-----------------------------------------------------------------------------------
//TODO: если юзера можно будет удалять из БД, то нужно не забыть удалить и его корзину из Memurai.

    public OurUser userByPrincipal (Principal principal)
    {
        String login = (principal != null) ? principal.getName() : STR_EMPTY;
        String errMsg = "Логин не зарегистрирован: " + login;
        return findByLogin(login).orElseThrow (()->new UserNotFoundException (errMsg));
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername (String login)
    {
        String errMsg = String.format ("Логин (%s) не зарегистрирован.", login);
        OurUser ourUser = findByLogin(login)
                            .orElseThrow(()->new UsernameNotFoundException (errMsg));

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
            OurUser saved = ourUserRepo.save (dummyUser);
            saved.addRole (optionalRole.get());
            return Optional.of (saved);
        }
        return Optional.empty();
    }

    public Optional<OurUser> findByLogin (String login)
    {
        return ourUserRepo.findByLogin (login);
    }

    @Transactional
    public UserInfoDto getUserInfoDto (Principal principal)
    {
        OurUser u = userByPrincipal (principal);
        return new UserInfoDto (u.getLogin(), u.getEmail());
    }
}
