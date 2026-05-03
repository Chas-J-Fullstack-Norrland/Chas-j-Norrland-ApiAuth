package com.example.demo;

import com.example.demo.model.AppUser;
import com.example.demo.repo.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JWTAuthFlowTests {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private UserRepository repository;


    @AfterEach
    void cleanup(){
        repository.deleteAll();
    }

    @Test
    void noAuthRequiredForPublicEndpoint(){
        ResponseEntity<String> response =
                restTemplate.getForEntity("/public",String.class);

        assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    void AuthRequiredForPrivateEndpoint(){
        ResponseEntity<String> response =
                restTemplate.getForEntity("/private",String.class);

        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusCode());
    }

    @Test
    void PrivateEndpointReturns200WithValidToken(){
        String username = "user";
        String rawPassword = "pass";

        AppUser user = new AppUser();
        user.setUsername("user");
        user.setPassword(encoder.encode(rawPassword));
        repository.save(user);

        String token = getToken(username, rawPassword);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/private"
                ,HttpMethod.GET
                ,request
                ,String.class
        );

        assertEquals(HttpStatus.OK,response.getStatusCode());

    }


    public String getToken(String user, String pass){

        HttpHeaders headers =  new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String,String> body = new HashMap<>();
        body.put("username",user);
        body.put("password",pass);

        HttpEntity<Map<String,String>> request =
                new HttpEntity<>(body,headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/login",request,String.class);

        assertEquals(HttpStatus.OK,response.getStatusCode());

        return response.getBody();

    }



}
