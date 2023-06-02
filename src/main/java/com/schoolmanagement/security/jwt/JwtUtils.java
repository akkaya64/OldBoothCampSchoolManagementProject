package com.schoolmanagement.security.jwt;

import com.schoolmanagement.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    //LWT Token nin olusturulabilmesi icin iki tane arguman lazim
    //1) Secret Key
    //2) Sure
    // Hayati oneme sahip bu tarz argumanlari Abbilation yml uzeride de setleyebiliriz.
    @Value("${backendapi.app.jwtSecret}")//application.properties den degisken ismi bu olan data nin value si ne ise
    // onu buraya ata der gibi $ isaretini koyuyoruz degiskenin key degerini {} icine koyuyoruz.
    private String jwtSecret;

    @Value("${backendapi.app.jwtExpressionMS}")
    private long jwtExpirationMs;


    // Not: Generate JWT *************************************************

    //jwt bir token gelecek bunun icin return type ini String olarak alabiliriz.
    public String generateJwtToken(Authentication authentication) { //Authentication edilmis bir kullaniciya ulasabilmek icin
                                                                    // Spring security den gelen bir Authentication
                                                                    // parametresini methoda veriyoruz


        //authentication uzerinden anlik olarak login islemi yapan kullanici bilgisi :
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();//getPrincipal() en parent
        // class olan Object i dondurur. Bu Object 'i Spring in UserDetails interfacesinden iden turettigimiz
        // UserDetailsImpl Type inda ki bir degiskene atiyoruz.
        //Artik userPrincipal uzerinden anlik olarak login olmus kullanicinin bilgilerine ulasabiliyoruz.


        // username bilgisi ile JWT token uretiliyor code kalabaligi olmasin diye bunu ayri bir method uzerinden yapiyoruz
        return  generateTokenFromUsername(userPrincipal.getUsername());

    }

    //Methodun ismi ne is yapiyorsa onu ifade etmeli parametre olarak bir username veriyoruz.
    public String generateTokenFromUsername(String username) {
        //poma JJWT bagimliligini eklmemistik Jwts JwT kutuphanesinde benim kullanabilecegimiz bir class
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())// olusturulma zamani
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();// yukarida setlemis oldugmuz bilgilerle birlikte bir JWT Token uretiyor ve geri donduruyor.
        //Bu methodu generateJwtToken methodunda kullaniyoruz.
    }




    // Not: Validate JWT *************************************************
    public boolean validateJwtToken(String authToken){

        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("Jwt token is expired : {}" , e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT Token is unsupported : {}" , e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid Jwt Token: {}" , e.getMessage());
        } catch (SignatureException e) {
            logger.error("Invalid Jwt signature : {}" , e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Jwt claims string is empty: {}" , e.getMessage());
        }

        return false;
    }


    // Not: getUsernameForJWT ********************************************
    //JWT token uretilirken kullanicinin uniqou degeri olan username i icinden cekeriz
    public String getUserNameFromJwtToken(String token) {

        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }
}