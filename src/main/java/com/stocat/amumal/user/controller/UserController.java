package com.stocat.amumal.user.controller;

import com.stocat.amumal.common.response.ApiResponse;
import com.stocat.amumal.user.dto.LoginRequest;
import com.stocat.amumal.user.dto.LoginResponse;
import com.stocat.amumal.user.dto.SignUpRequest;
import com.stocat.amumal.user.dto.SignUpResponse;
import com.stocat.amumal.user.dto.UserResponse;
import com.stocat.amumal.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SignUpResponse> signUp(@RequestBody SignUpRequest request) {
        return ApiResponse.of("회원가입이 완료되었습니다.", userService.signUp(request));
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.of("로그인이 완료되었습니다.", userService.login(request));
    }

    @GetMapping("/{user_id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UserResponse> getUser(@PathVariable("user_id") Long userId) {
        return ApiResponse.of("회원정보 조회에 성공했습니다.", userService.getUser(userId));
    }
}
