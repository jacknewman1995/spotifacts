package com.jack.spotifygame.users;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController
public class UserController {

    private final AppUserRepository appUserRepository;

    @Inject
    public UserController(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/")
    public String sayHello() {
        return "Hello World";
    }

    @PostMapping(path = "/addUser", consumes = "application/json")
    @CrossOrigin
    public void addUser(@RequestBody AppUser newUser) {
        appUserRepository.save(newUser);
    }
}
