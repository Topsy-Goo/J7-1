package ru.gb.antonov.j71.beans.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gb.antonov.j71.beans.errorhandlers.ErrorMessage;
import ru.gb.antonov.j71.beans.errorhandlers.OurValidationException;
import ru.gb.antonov.j71.beans.services.OurUserService;
import ru.gb.antonov.j71.beans.utils.JwtokenUtil;
import ru.gb.antonov.j71.entities.OurUser;
import ru.gb.antonov.j71.entities.dtos.AuthRequest;
import ru.gb.antonov.j71.entities.dtos.AuthResponse;
import ru.gb.antonov.j71.entities.dtos.RegisterRequest;

import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping ("/api/v1/auth")
@RestController
@RequiredArgsConstructor
public class AuthController
{
    private final OurUserService ourUserService;
    private final JwtokenUtil    jwtokenUtil;
    private final AuthenticationManager authenticationManager;


    //http://localhost:8189/market/api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser (@RequestBody AuthRequest authRequest)
    {
        String login = authRequest.getLogin();
        String password = authRequest.getPassword();

/*        try
        {   authenticationManager.authenticate (new UsernamePasswordAuthenticationToken (login, password));
        }
        catch (BadCredentialsException e)
        {
            String errMsg = String.format ("Incorrect login (%s) or password (%s).", login, password);
            return new ResponseEntity<> (new ErrorMessage (errMsg), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){e.printStackTrace();}

        UserDetails userDetails = ourUserService.loadUserByUsername (login);
        String token = jwtokenUtil.generateJWToken (userDetails);

        return ResponseEntity.ok (new AuthResponse (token));*/
        return inlineAuthentificateAndResponseWithJwt (login, password);
    }

    //http://localhost:8189/market/api/v1/auth/register
    @PostMapping ("/register")
    public ResponseEntity<?> registerNewUser (@RequestBody @Validated RegisterRequest registerRequest,
                                              BindingResult br)
    {   if (br.hasErrors())
        {
            throw new OurValidationException (br.getAllErrors()
                                                .stream()
                                                .map (ObjectError::getDefaultMessage)
                                                .collect (Collectors.toList ()));
        }
        String login    = registerRequest.getLogin ();
        String password = registerRequest.getPassword ();
        String email = String.format ("%s@%s.%s",
                                      registerRequest.getEmailUser(),
                                      registerRequest.getEmailServer(),
                                      registerRequest.getEmailDomain());

        Optional<OurUser> optionalOurUser = ourUserService.createNewOurUser (
                                                    login, password, email);
        if (optionalOurUser.isPresent())
        {
            return inlineAuthentificateAndResponseWithJwt (login, password);
        }
        return new ResponseEntity<> (new ErrorMessage ("Новый пользователь создан некорректно."),
                                     HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<?> inlineAuthentificateAndResponseWithJwt (String login, String password)
    {
        try
        {   authenticationManager.authenticate (new UsernamePasswordAuthenticationToken (login, password));
        }
        catch (BadCredentialsException e)
        {
            String errMsg = String.format ("Некорректные логин (%s) и/или пароль (%s).", login, password);
            return new ResponseEntity<> (new ErrorMessage (errMsg), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){e.printStackTrace();}

        UserDetails userDetails = ourUserService.loadUserByUsername (login);
        String token = jwtokenUtil.generateJWToken (userDetails);

        return ResponseEntity.ok (new AuthResponse (token));
    }
}
