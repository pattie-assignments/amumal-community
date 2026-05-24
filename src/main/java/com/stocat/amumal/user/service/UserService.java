package com.stocat.amumal.user.service;

import com.stocat.amumal.user.dto.LoginRequest;
import com.stocat.amumal.user.dto.LoginResponse;
import com.stocat.amumal.user.dto.SignUpRequest;
import com.stocat.amumal.user.dto.SignUpResponse;

public interface UserService {

    SignUpResponse signUp(SignUpRequest request);

    LoginResponse login(LoginRequest request);
}
