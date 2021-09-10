package ru.gb.antonov.j71.beans.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtokenUtil
{
    @Value("${jwt.secret}") //< синтаксис ссылается на проперти-файл
    private String secret;      //< обычно выносится в к-л конфиг (см. yaml)

    @Value("${jwt.lifetime}")
    private Integer lifetime;   //< обычно выносится в к-л конфиг (см. yaml)

    public String generateJWToken (UserDetails userDetails)
    {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userDetails
                                .getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList());

        claims.put("roles", roles);
        Date dateIssued = new Date();
        Date dateExpired = new Date (dateIssued.getTime() + lifetime);
        return Jwts.builder()
                   .setClaims(claims)
                   .setSubject(userDetails.getUsername())
                   .setIssuedAt(dateIssued)
                   .setExpiration(dateExpired)
                   .signWith(SignatureAlgorithm.HS256, secret)
                   .compact();
    }

    public String getLoginFromToken (String token)
    {
        return getClaimFromToken(token, Claims::getSubject);
    }

    private <T> T getClaimFromToken (String token, Function<Claims, T> claimsResolver)
    {
        Claims claims = getAllClaimsFromToken (token);
        return claimsResolver.apply (claims);
    }

    private Claims getAllClaimsFromToken(String token)
    {
        return Jwts.parser()
                   .setSigningKey (secret)  //< нужет для проверки подлинности и актуальности токена
                   .parseClaimsJws (token)
                   .getBody();
    }

    public List<String> getRoles(String token)
    {
    /*    return getClaimFromToken (
                token,
                (Function<Claims, List<String>>) claims -> claims.get("roles", List.class));*/
        return getClaimFromToken (token, claims -> claims.get("roles", List.class));
    }
}
