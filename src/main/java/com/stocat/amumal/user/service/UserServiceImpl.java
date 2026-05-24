package com.stocat.amumal.user.service;

import com.stocat.amumal.common.exception.ApiException;
import com.stocat.amumal.user.domain.User;
import com.stocat.amumal.user.dto.LoginRequest;
import com.stocat.amumal.user.dto.LoginResponse;
import com.stocat.amumal.user.dto.SignUpRequest;
import com.stocat.amumal.user.dto.SignUpResponse;
import com.stocat.amumal.user.dto.UpdatePasswordRequest;
import com.stocat.amumal.user.dto.UpdateProfileRequest;
import com.stocat.amumal.user.dto.UpdateProfileResponse;
import com.stocat.amumal.user.dto.UserResponse;
import com.stocat.amumal.user.repository.UserRepository;
import com.stocat.amumal.user.validator.UserValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;

    public UserServiceImpl(UserRepository userRepository, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
    }

    @Override
    public SignUpResponse signUp(SignUpRequest request) {
        // 데이터 형식, 존재 여부와 같은 유효성 검증
        validateSignUp(request);

        // 중복 데이터 검증
        if (userRepository.existsByEmail(request.email().trim())) {
            throw new ApiException(HttpStatus.CONFLICT, "중복된 이메일 입니다.");
        }

        if (userRepository.existsByNickname(request.nickname().trim())) {
            throw new ApiException(HttpStatus.CONFLICT, "중복된 닉네임 입니다.");
        }

        // 회원 데이터 저장
        User savedUser = userRepository.save(
                request.email().trim(),
                request.password(),
                request.nickname().trim(),
                request.profileImage().trim()
        );

        return new SignUpResponse(savedUser.getId());
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        validateLogin(request);

        User user = userRepository.findByEmail(request.email().trim())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!user.getPassword().equals(request.password())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        return new LoginResponse(user.getId());
    }

    @Override
    public UserResponse getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImage()
        );
    }

    @Override
    public UpdateProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));

        validateProfileUpdate(request, user);

        String nickname = request.nickname() == null ? user.getNickname() : request.nickname().trim();
        String profileImage = request.profileImage() == null ? user.getProfileImage() : request.profileImage().trim();

        User updatedUser = userRepository.updateProfile(userId, nickname, profileImage);

        return new UpdateProfileResponse(
                updatedUser.getId(),
                updatedUser.getNickname(),
                updatedUser.getProfileImage()
        );
    }

    @Override
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        // 비밀번호 업데이트 하려는 유저가 존재하는가?
        userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));

        // 비밀번호 업데이트를 위한 조건을 충족하는가? (비밀번호 형식, 중복 입력 성공 여부)
        validatePasswordUpdate(request);

        userRepository.updatePassword(userId, request.password());
    }

    @Override
    public void deleteUser(Long userId) {
        // 삭제할 유저 id가 데이터베이스에 존재하지 않으면 예외
        userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));

        userRepository.deleteById(userId);
    }

    private void validateSignUp(SignUpRequest request) {
        userValidator.validateEmail(request.email());
        userValidator.validatePassword(request.password());
        userValidator.validatePasswordConfirm(request.password(), request.passwordConfirm(), "비밀번호를 한번더 입력해주세요.", "비밀번호가 다릅니다.");
        userValidator.validateNickname(request.nickname());
        userValidator.validateRequiredProfileImage(request.profileImage());
    }

    private void validateLogin(LoginRequest request) {
        userValidator.validateEmail(request.email());
        userValidator.validateRequiredPassword(request.password());
    }

    private void validateProfileUpdate(UpdateProfileRequest request, User user) {
        boolean hasNickname = request.nickname() != null;
        boolean hasProfileImage = request.profileImage() != null;

        if (!hasNickname && !hasProfileImage) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "닉네임 또는 프로필 이미지를 입력해주세요.");
        }

        if (hasNickname) {
            String nickname = request.nickname().trim();
            userValidator.validateNickname(request.nickname());

            if (!nickname.equals(user.getNickname()) && userRepository.existsByNickname(nickname)) {
                throw new ApiException(HttpStatus.CONFLICT, "중복된 닉네임 입니다.");
            }
        }

        // 문자열 정보만 전달받으므로 파일의 실제 크기가 N mb 이하만 가능하다는 것을 검증할 수 없다.

        if (hasProfileImage) {
            userValidator.validateOptionalProfileImage(request.profileImage());
        }
    }

    private void validatePasswordUpdate(UpdatePasswordRequest request) {
        userValidator.validatePassword(request.password());
        userValidator.validatePasswordConfirm(request.password(), request.passwordConfirm(), "비밀번호를 한번 더 입력해주세요", "비밀번호 확인과 다릅니다.");
    }
}
