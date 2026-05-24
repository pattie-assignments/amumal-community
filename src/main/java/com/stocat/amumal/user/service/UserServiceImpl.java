package com.stocat.amumal.user.service;

import com.stocat.amumal.common.exception.ApiException;
import com.stocat.amumal.user.domain.User;
import com.stocat.amumal.user.dto.LoginRequest;
import com.stocat.amumal.user.dto.LoginResponse;
import com.stocat.amumal.user.dto.SignUpRequest;
import com.stocat.amumal.user.dto.SignUpResponse;
import com.stocat.amumal.user.dto.UserResponse;
import com.stocat.amumal.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,20}$");

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public SignUpResponse signUp(SignUpRequest request) {
        // 데이터 형식, 존재 여부와 같은 유효성 검증
        validate(request);

        // 중복 데이터 검증
        if (userRepository.existsByEmail(request.email().trim())) {
            throw new ApiException(HttpStatus.CONFLICT, "중복된 이메일 입니다.");
        }

        if (userRepository.existsByNickname(request.nickname().trim())) {
            throw new ApiException(HttpStatus.CONFLICT, "중복된 닉네임 입니다.");
        }

        // 회원 데이터 저장
        User savedUser = userRepository.save(new User(
                null,
                request.email().trim(),
                request.password(),
                request.nickname().trim(),
                request.profileImage().trim()
        ));

        return new SignUpResponse(savedUser.id());
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        validateLogin(request);

        User user = userRepository.findByEmail(request.email().trim())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!user.password().equals(request.password())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        return new LoginResponse(user.id());
    }

    @Override
    public UserResponse getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));

        return new UserResponse(
                user.id(),
                user.email(),
                user.nickname(),
                user.profileImage()
        );
    }

    private void validate(SignUpRequest request) {
        if (isBlank(request.email())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이메일을 입력해주세요.");
        }

        if (!EMAIL_PATTERN.matcher(request.email().trim()).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "올바른 이메일 주소 형식을 입력해주세요. (예: example@adapterz.kr)");
        }

        if (isBlank(request.password())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "비밀번호를 입력해주세요.");
        }

        if (!PASSWORD_PATTERN.matcher(request.password()).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다.");
        }

        if (isBlank(request.passwordConfirm())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "비밀번호를 한번더 입력해주세요.");
        }

        if (!request.password().equals(request.passwordConfirm())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "비밀번호가 다릅니다.");
        }

        if (isBlank(request.nickname())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "닉네임을 입력해주세요.");
        }

        if (request.nickname().chars().anyMatch(Character::isWhitespace)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "띄어쓰기를 없애주세요.");
        }

        if (request.nickname().length() > 10) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "닉네임은 최대 10자 까지 작성 가능합니다.");
        }

        if (isBlank(request.profileImage())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "프로필 사진을 추가해주세요.");
        }
    }

    private void validateLogin(LoginRequest request) {
        if (isBlank(request.email())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이메일을 입력해주세요.");
        }

        if (!EMAIL_PATTERN.matcher(request.email().trim()).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "올바른 이메일 주소 형식을 입력해주세요. (예: example@adapterz.kr)");
        }

        if (isBlank(request.password())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "비밀번호를 입력해주세요.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
