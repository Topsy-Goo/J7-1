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
import ru.gb.antonov.j71.entities.OurPermission;
import ru.gb.antonov.j71.entities.Role;
import ru.gb.antonov.j71.entities.dtos.UserInfoDto;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.gb.antonov.j71.Factory.STR_EMPTY;

@Service
@RequiredArgsConstructor
public class OurUserService implements UserDetailsService {

    private final OurUserRepo          ourUserRepo;
    private final RoleService          roleService;
    private final OurPermissionService ourPermissionService;

    public static final String NO_SUCH_LOGIN_ = "Логин не зарегистрирован: ";
//-----------------------------------------------------------------------------------
//TODO: если юзера можно будет удалять из БД, то нужно не забыть удалить и его корзину из Memurai.

/** @throws UserNotFoundException */
    public OurUser userByPrincipal (Principal principal) {

        String login = (principal != null) ? principal.getName() : STR_EMPTY;
        return findByLogin(login).orElseThrow (()->new UserNotFoundException (NO_SUCH_LOGIN_+ login));
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername (String login)
    {
        OurUser ourUser = findByLogin (login)
                            .orElseThrow(()->new UsernameNotFoundException (NO_SUCH_LOGIN_+ login));

        return new User (ourUser.getLogin(),
                         ourUser.getPassword(),
                         mapRolesToAuthorities (ourUser.getRoles(), ourUser.getOurPermissions()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities (
                                    Collection<Role> roles,
                                    Collection<OurPermission> permissions)
    {
        List<String> list = roles.stream().map (Role::getName)
                                 .collect (Collectors.toList());

        list.addAll (permissions.stream().map (OurPermission::getName)
                                .collect (Collectors.toList()));

        return list.stream().map (SimpleGrantedAuthority::new)
                   .collect (Collectors.toList());
    }

    @Transactional
    public Optional<OurUser> createNewOurUser (String login, String password, String email)
    {
        OurUser newOurUser = OurUser.create()
                                    .withLogin (login)
                                    .withPassword (password)
                                    .withEmail (email)
                                    .build();

        Role roleUser = roleService.getRoleUser();
        OurPermission ourDefaultPermission = ourPermissionService.getPermissionDefault();

        OurUser saved = ourUserRepo.save (newOurUser);
        saved.addRole (roleUser);
        saved.addPermission (ourDefaultPermission);
        return Optional.of (saved);
    }

    public Optional<OurUser> findByLogin (String login) {
        return ourUserRepo.findByLogin (login);
    }

    @Transactional
    public UserInfoDto getUserInfoDto (Principal principal) {
        OurUser u = userByPrincipal (principal);
        return new UserInfoDto (u.getLogin(), u.getEmail());
    }

/** Редактировать информацию о товарах могут только те пользователи, у которых есть
разрешение {@code PERMISSION_EDIT_PRODUCT}. */
    @Transactional
    public Boolean canEditProduct (Principal principal) {

        OurUser ourUser = userByPrincipal (principal);
        Collection<OurPermission> permissions = ourUser.getOurPermissions();
        return permissions.contains (ourPermissionService.getPermissionEditProducts());
    }
}
