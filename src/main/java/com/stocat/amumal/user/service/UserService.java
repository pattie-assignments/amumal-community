package com.stocat.amumal.user.service;

import com.stocat.amumal.user.dto.SignUpRequest;
import com.stocat.amumal.user.dto.SignUpResponse;

public interface UserService {

    SignUpResponse signUp(SignUpRequest request);
}
