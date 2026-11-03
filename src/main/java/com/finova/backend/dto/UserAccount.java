package com.finova.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAccount {

    private String username;
    private String passwordHash;
}