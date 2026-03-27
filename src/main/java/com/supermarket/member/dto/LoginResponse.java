package com.supermarket.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;

    private String memberNo;

    private String name;

    private String level;

    private String message;

    public LoginResponse(String token, String memberNo, String name, String level) {
        this.token = token;
        this.memberNo = memberNo;
        this.name = name;
        this.level = level;
    }

    public LoginResponse(String message) {
        this.message = message;
    }
}
