package ru.gb.antonov.j71.beans.configs;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.gb.antonov.j71.beans.services.OurUserService;
import ru.gb.antonov.j71.beans.utils.JwtokenUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.gb.antonov.j71.Factory.AUTHORIZATION_HDR_TITLE;
import static ru.gb.antonov.j71.Factory.BEARER_;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtokenUtil    jwtokenUtil;
    private final OurUserService ourUserService;

    @Override
    protected void doFilterInternal (HttpServletRequest request,
                                     @NotNull HttpServletResponse response,
                                     @NotNull FilterChain filterChain) throws ServletException, IOException {
        String login = null;
        String jwt   = null;
        //String prefixBearer = BEARER_;
        String authHeader = request.getHeader (AUTHORIZATION_HDR_TITLE);

        if (authHeader != null && authHeader.startsWith (BEARER_)) {

            jwt = authHeader.substring (BEARER_.length());
            try {
                login = jwtokenUtil.getLoginFromToken (jwt);
                }
            catch (ExpiredJwtException e) { log.debug ("The token is expired"); }
        }
        if (login != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            //UsernamePasswordAuthenticationToken token = trustYourUser (login, jwt);
            UsernamePasswordAuthenticationToken token = trustDatabaseOnly (login, jwt, request);
            SecurityContextHolder.getContext().setAuthentication (token);
        }
        filterChain.doFilter (request, response);
    }

    private UsernamePasswordAuthenticationToken trustDatabaseOnly (
                                        String login, String jwt, HttpServletRequest request) {

        UserDetails userDetails = ourUserService.loadUserByUsername (login);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken (
                    userDetails,
                    null,
                    userDetails.getAuthorities());

        token.setDetails (new WebAuthenticationDetailsSource().buildDetails (request));
        return token;
    }

    private UsernamePasswordAuthenticationToken trustYourUser (String login, String jwt) {

        Collection<GrantedAuthority> gaCollection = jwtokenUtil //
                       .getRoles (jwt)
                       .stream()
                       .map (SimpleGrantedAuthority::new)
                       .collect (Collectors.toList ());

        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken (login, null, gaCollection);
        return token;
    }
}
