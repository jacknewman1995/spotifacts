package com.jack.spotifygame.users;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private long id;

    @Column
    private String name;

    @Column
    private String token;
}
