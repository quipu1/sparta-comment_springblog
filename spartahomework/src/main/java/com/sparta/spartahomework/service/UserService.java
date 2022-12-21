package com.sparta.spartahomework.service;

import com.sparta.spartahomework.dto.LoginRequestDto;
import com.sparta.spartahomework.dto.SignupRequestDto;
import com.sparta.spartahomework.dto.UserResponseDto;
import com.sparta.spartahomework.entity.User;
import com.sparta.spartahomework.entity.UserRoleEnum;
import com.sparta.spartahomework.exception.CustomException;
import com.sparta.spartahomework.jwt.JwtUtil;
import com.sparta.spartahomework.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.sparta.spartahomework.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    @Transactional
    public UserResponseDto signup(SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.getUsername();
        String password = signupRequestDto.getPassword();

        Optional<User> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new CustomException(DUPLICATE_USERNAME);
        }

        UserRoleEnum role = UserRoleEnum.USER;
        if (signupRequestDto.isAdmin()) {
            if (!signupRequestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                throw new CustomException(INVALID_AUTH_TOKEN);
            }
            role = UserRoleEnum.ADMIN;
        }

        User user = new User(username, password, role);
        userRepository.save(user);

        UserResponseDto signupResponseDto = new UserResponseDto(user);
        return signupResponseDto;
    }

    @Transactional(readOnly = true)
    public UserResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        // 사용자 확인
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        // 비밀번호 확인
        if(!user.getPassword().equals(password)){
            throw new CustomException(PASSWORD_NOT_FOUND);
        }

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.getUsername(), user.getRole()));

        UserResponseDto userResponseDto = new UserResponseDto(user);
        return userResponseDto;
    }

}
