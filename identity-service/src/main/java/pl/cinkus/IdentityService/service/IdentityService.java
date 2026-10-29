package pl.cinkus.IdentityService.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.cinkus.IdentityService.client.RabbitMqMessageSender;
import pl.cinkus.IdentityService.entity.RefreshToken;
import pl.cinkus.IdentityService.entity.ResetPasswordRequest;
import pl.cinkus.IdentityService.exception.ErrorCode;
import pl.cinkus.IdentityService.exception.IdentityServiceException;
import pl.cinkus.IdentityService.mapper.UserDataMapper;
import pl.cinkus.IdentityService.entity.User;
import pl.cinkus.IdentityService.repository.RefreshTokenRepository;
import pl.cinkus.IdentityService.repository.ResetPasswordRequestRepository;
import pl.cinkus.IdentityService.repository.UserRepository;
import pl.cinkus.IdentityService.util.CodeGeneration;
import pl.cinkus.IdentityService.validation.UserRegistrationValidation;
import pl.cinkus.backend.codegen.types.*;
import pl.cinkus.messages.EmailType;
import pl.cinkus.messages.SendEmailWithCodeMessage;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentityService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRegistrationValidation validation;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserDataMapper userDataMapper;
    private final RabbitMqMessageSender rabbitMqMessageSender;
    private final ResetPasswordRequestRepository resetPasswordRequestRepository;

    @Transactional
    public void registerUser(NewUserData userData) {
        if(userRepository.existsByNickNameOrEmail(userData.getNickName(), userData.getEmail())) {
            throw new IdentityServiceException(ErrorCode.EMAIL_OR_NICKNAME_ALREADY_TAKEN, "User already exists!");
        }

        if(userData.getRole().equals(UserRole.ADMIN)) {
            throw new IdentityServiceException(ErrorCode.INVALID_ROLE, "You cannot this type of account");
        }

        validation.validate(userData);

        String registrationCode = CodeGeneration.generateCode();

        log.info("Register Code: " + registrationCode);

        userRepository.save(User.builder()
                .id(UUID.randomUUID())
                .name(userData.getName())
                .surname(userData.getSurname())
                .nickName(userData.getNickName())
                .role(userDataMapper.toUserRole(userData.getRole()))
                .email(userData.getEmail())
                .password(passwordEncoder.encode(userData.getPassword()))
                .verified(false)
                .registrationCode(registrationCode)
                .build());

        rabbitMqMessageSender.sendMessage(SendEmailWithCodeMessage.builder()
                .email(userData.getEmail())
                .emailType(EmailType.VERIFY_REGISTRATION)
                .code(registrationCode).build());
    }

    public LoginResult login(UserLoginData userLoginData) {
        User user = userRepository.findByEmail(userLoginData.getEmail())
                .orElseThrow(() -> new IdentityServiceException(ErrorCode.LOGIN_FAILED, "Login failed"));

        if(!user.isVerified()) {
            throw new IdentityServiceException(ErrorCode.UNVERIFIED_USER, "User is not verified with a registration code");
        }

        if (!passwordEncoder.matches(userLoginData.getPassword(), user.getPassword())) {
            throw new IdentityServiceException(ErrorCode.LOGIN_FAILED, "Login failed");
        }

        String token = jwtService.generateToken(user.getId(), user.getRole());
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(user.getId());

        log.info("Token {}", token);
        log.info("refresh {}", refreshToken);
        return LoginResult.newBuilder()
                .token(token)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    @Transactional
    public LoginResult refreshToken(String userRefreshToken) {
        RefreshToken oldRefreshToken = refreshTokenRepository.findByRefreshToken(userRefreshToken)
                .orElseThrow(() -> new IdentityServiceException(ErrorCode.INVALID_REFRESH_TOKEN, "Refresh token not found"));

        refreshTokenService.verifyExpiration(oldRefreshToken);

        User user = userRepository.findById(oldRefreshToken.getOwnerId())
                .orElseThrow(() -> new IdentityServiceException(ErrorCode.INVALID_REFRESH_TOKEN, "Owner of refresh token not found"));

        refreshTokenRepository.delete(oldRefreshToken);

        String token = jwtService.generateToken(user.getId(), user.getRole());
        RefreshToken newRefreshToken = refreshTokenService.generateRefreshToken(user.getId());

        return LoginResult.newBuilder()
                .token(token)
                .refreshToken(newRefreshToken.getRefreshToken())
                .build();

    }

    public List<String> getAllUsers() {
        return userRepository.findAll().stream().map(User::getEmail).toList();
    }

    @Transactional
    public void verifyUser(UserVerifyData userVerifyData) {
        User user = userRepository.findByEmail(userVerifyData.getEmail())
                .orElseThrow(() -> new IdentityServiceException(ErrorCode.USER_NOT_FOUND, "User with given email does not exists!"));

        if(user.isVerified()) {
            throw new IdentityServiceException(ErrorCode.USER_ALREADY_VERIFIED, "User is already verified");
        }

        if(!user.getRegistrationCode().equals(userVerifyData.getCode())) {
            throw new IdentityServiceException(ErrorCode.INVALID_VERIFICATION_CODE, "Verification code is wrong");
        }

        user.setVerified(true);
        user.setRegistrationCode(null);
    }

    @Transactional
    public void resetPasswordRequest(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IdentityServiceException(ErrorCode.USER_NOT_FOUND, "User with given email does not exists!"));

        if(!user.isVerified()) {
            throw new IdentityServiceException(ErrorCode.UNVERIFIED_USER, "Verify your account first!");
        }

        String resetCode = CodeGeneration.generateCode();

        resetPasswordRequestRepository.save(ResetPasswordRequest.builder()
                .resetCode(resetCode)
                .email(user.getEmail())
                .build());

        rabbitMqMessageSender.sendMessage(SendEmailWithCodeMessage.builder()
                .emailType(EmailType.RESET_PASSWORD)
                .email(user.getEmail())
                .code(resetCode)
                .build());
    }

    @Transactional
    public void resetPassword(ResetPasswordData resetPasswordData) {
        ResetPasswordRequest resetPasswordRequest = resetPasswordRequestRepository.findByEmail(resetPasswordData.getEmail())
                .orElseThrow(() -> new IdentityServiceException(ErrorCode.USER_NOT_FOUND, "We do not have any reset password request for this email"));

        if(!resetPasswordData.getCode().equals(resetPasswordRequest.getResetCode())) {
            throw new IdentityServiceException(ErrorCode.INVALID_RESET_PASSWORD_CODE, "Wrong password reset code!");
        }

        User user = userRepository.findByEmail(resetPasswordData.getEmail())
                .orElseThrow(() -> new IdentityServiceException(ErrorCode.USER_NOT_FOUND, "User with given email does not exists!"));

        validation.validatePassword(resetPasswordData.getNewPassword());
        user.setPassword(passwordEncoder.encode(resetPasswordData.getNewPassword()));

        resetPasswordRequestRepository.delete(resetPasswordRequest);
    }

    public void logout(String refreshToken) {
        RefreshToken savedRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IdentityServiceException(ErrorCode.INVALID_REFRESH_TOKEN, "Could not log out. Try again later"));

        refreshTokenRepository.delete(savedRefreshToken);
    }
}
