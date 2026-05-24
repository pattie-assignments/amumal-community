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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,20}$");
    private static final Pattern IMAGE_FILE_PATTERN =
            Pattern.compile("^.+\\.(png|jpg|jpeg|gif|webp)$", Pattern.CASE_INSENSITIVE);

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

        // null이 아니라면(수정할 데이터를 받았다면) 수정
        if (request.nickname() != null) {
            user.setNickname(request.nickname().trim());
        }

        if (request.profileImage() != null) {
            user.setProfileImage(request.profileImage().trim());
        }

        User updatedUser = userRepository.save(user);

        return new UpdateProfileResponse(
                updatedUser.getId(),
                updatedUser.getNickname(),
                updatedUser.getProfileImage()
        );
    }

    @Override
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        // 비밀번호 업데이트 하려는 유저가 존재하는가?
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));

        // 비밀번호 업데이트를 위한 조건을 충족하는가? (비밀번호 형식, 중복 입력 성공 여부)
        validatePasswordUpdate(request);

        user.setPassword(request.password());
        userRepository.save(user);
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

    private void validateProfileUpdate(UpdateProfileRequest request, User user) {
        boolean hasNickname = request.nickname() != null;
        boolean hasProfileImage = request.profileImage() != null;

        if (!hasNickname && !hasProfileImage) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "닉네임 또는 프로필 이미지를 입력해주세요.");
        }

        if (hasNickname) {
            if (isBlank(request.nickname())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "닉네임을 입력해주세요.");
            }

            String nickname = request.nickname().trim();

            if (nickname.length() > 10) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "닉네임은 최대 10자 까지 작성 가능합니다.");
            }

            if (!nickname.equals(user.getNickname()) && userRepository.existsByNickname(nickname)) {
                throw new ApiException(HttpStatus.CONFLICT, "중복된 닉네임 입니다.");
            }
        }

        // 문자열 정보만 전달받으므로 파일의 실제 크기가 N mb 이하만 가능하다는 것을 검증할 수 없다.

        if (hasProfileImage) {
            if (isBlank(request.profileImage()) || !IMAGE_FILE_PATTERN.matcher(request.profileImage().trim()).matches()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "유효한 파일이 아닙니다.(올바른 사진 확장자가 아닐 경우)");
            }
        }
    }

    private void validatePasswordUpdate(UpdatePasswordRequest request) {
        if (isBlank(request.password())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "비밀번호를 입력해주세요");
        }

        if (!PASSWORD_PATTERN.matcher(request.password()).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다.");
        }

        if (isBlank(request.passwordConfirm())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "비밀번호를 한번 더 입력해주세요");
        }

        if (!request.password().equals(request.passwordConfirm())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "비밀번호 확인과 다릅니다.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
