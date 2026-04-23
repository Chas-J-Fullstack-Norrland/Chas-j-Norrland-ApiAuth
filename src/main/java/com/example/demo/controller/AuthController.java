
package com.example.demo.controller;
import com.example.demo.model.AppUser;
import com.example.demo.repo.UserRepository;
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
    public String register(@RequestParam String username, @RequestParam String password){

        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setPassword(encoder.encode(password));
        try{
            AppUser newAppUser = repo.save(appUser);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Failed";
        }


        return "User: " + appUser.getUsername() + " was created";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {

        //authenticationManager.authenticate( new UsernamePasswordAuthenticationToken()
        //does the same thing as the next 4 lines but returns the authentication we are supposed to arrive to with JWT
        AppUser appUser = repo.findByUsername(username).orElseThrow(()-> new BadCredentialsException("Username and Password didnt match"));
        if(!encoder.matches(password, appUser.getPassword())){
            throw new BadCredentialsException("Username or Password Didn't match");
        }
        //2. Om vi kommer hit = Login OK
        String name = appUser.getUsername();
        //3. Skapa JWT
        //4. Returnera token
        return jwtUtil.generateToken(name);

    }
}
