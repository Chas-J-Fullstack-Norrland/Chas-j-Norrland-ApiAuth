
package com.example.demo.controller;
import com.example.demo.model.AppUser;
import com.example.demo.repo.UserRepository;
import com.example.demo.requestbody.CredentialsRequest;
import com.example.demo.security.JwtUtil;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    private final JwtUtil jwtUtil;
    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder;
    public AuthController(UserRepository repo, JwtUtil jwtUtil, BCryptPasswordEncoder encoder) {
        this.jwtUtil = jwtUtil;
        this.repo = repo;
        this.encoder = encoder;
    }

    //Needed since we added in password encryption while the assignment assumed none
    @PostMapping("/register")
    public String register(@RequestBody CredentialsRequest request){

        AppUser appUser = new AppUser();
        appUser.setUsername(request.username());
        appUser.setPassword(encoder.encode(request.password()));
        try{
            AppUser newAppUser = repo.save(appUser);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Failed";
        }


        return "User: " + appUser.getUsername() + " was created";
    }

    @PostMapping("/login")
    public String login(@RequestBody CredentialsRequest request) {

        //authenticationManager.authenticate( new UsernamePasswordAuthenticationToken()
        //does the same thing as the next 4 lines but returns the authentication we are supposed to arrive to with JWT
        AppUser appUser = repo.findByUsername(request.username()).orElseThrow(()-> new BadCredentialsException("Username and Password didnt match"));
        if(!encoder.matches(request.password(), appUser.getPassword())){
            throw new BadCredentialsException("Username or Password Didn't match");
        }
        //2. Om vi kommer hit = Login OK
        String name = appUser.getUsername();
        //3. Skapa JWT
        //4. Returnera token
        return jwtUtil.generateToken(name);

    }
}
