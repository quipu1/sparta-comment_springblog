package com.sparta.spartahomework.dto;

import com.sparta.spartahomework.entity.User;
import com.sparta.spartahomework.entity.UserRoleEnum;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private String username;
    private UserRoleEnum role;

    public UserResponseDto(User user) {
        this.username = user.getUsername();
        this.role = user.getRole();
    }
}

