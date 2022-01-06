package com.example.demo.controller;

import com.example.demo.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {

    User user = new User();
    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe
    private Date currentDate = new Date(System.currentTimeMillis());
    private Date expireDate = new Date(System.currentTimeMillis() + 600000);

    @PostMapping("auth")
    public User login(@RequestParam("user") String username, @RequestParam("password") String pwd) {
        user = new User("mahiii", "137029@Ms");

        if (!user.getUser().equals(username) && !user.getPassword().equals(pwd)) {
            user.setNullParams();
        } else {
            final String token = getJWTToken(user);
            user.setUser(user.getUser());
            user.setToken(token);
            user.setNullParams1();
        }
        return user;
    }

    private String getJWTToken(User user) {
        String secretKey = "mySecretKey";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");

        String token = Jwts
                .builder()
                .setId("tokenJWT")
                .setSubject(user.getUser())
                .setSubject(user.getPassword())
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256,
                        secretKey.getBytes()).compact();

        return "Bearer " + token;
    }

    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return "Bearer " + base64Encoder.encodeToString(randomBytes);
    }
}