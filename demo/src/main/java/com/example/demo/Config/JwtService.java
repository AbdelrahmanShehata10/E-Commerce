package com.example.demo.Config;

import com.example.demo.Models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Claims;

@Service
public class JwtService {

    private static final String secret_key="e9edfc5a60398a364515cc355cd3a257a6ad20360c331450a04104cb91b0b5ba";
    public <T> T extractclaim (String token, Function<Claims,T> claimsResorver){

        final Claims claims= extractAllClaims(token);
        return claimsResorver.apply(claims);
    }
    public  String extractUsername(String jwt){

        return extractclaim(jwt,Claims::getSubject);

    }


    public Claims extractAllClaims(String token){

        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSigningKey() {
        byte [] KeyBytes = Decoders.BASE64.decode(secret_key);
        return Keys.hmacShaKeyFor(KeyBytes);
    }
    public  String generateToken(Map<String, Object> extraclaims, UserDetails userDetails){
        System.out.println(userDetails.getUsername());
        if(userDetails instanceof User){
            User user=(User) userDetails;
            extraclaims.put("role",user.getRole().name());


        }
        return  Jwts.builder().setClaims(extraclaims).setSubject(userDetails.getUsername()).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + 1000*60*7)).signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    public String generateToken(UserDetails userDetails){
        return   generateToken(new HashMap<>(),userDetails);

    }
    public Boolean isTokenValid(String Token,UserDetails userDetails){
        final String username=extractUsername(Token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(Token));

    }

    private boolean isTokenExpired(String token) {

        return extractexpiration(token).before(new Date());
    }

    private Date extractexpiration(String token) {
        return   extractclaim(token, Claims::getExpiration);
    }
}
