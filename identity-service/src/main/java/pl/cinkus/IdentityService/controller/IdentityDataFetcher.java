package pl.cinkus.IdentityService.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import pl.cinkus.IdentityService.service.IdentityService;
import pl.cinkus.IdentityService.service.JwtService;
import pl.cinkus.backend.codegen.types.*;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor

public class IdentityDataFetcher {
    private final IdentityService identityService;
    private final JwtService jwtService;
    private final HttpServletRequest request;

    @MutationMapping
    public RegisterPayload register(@Argument NewUserData userData) {
        identityService.registerUser(userData);
        return RegisterPayload.newBuilder()
                .registered(true)
                .verificationRequired(true)
                .build();
    }

    @MutationMapping
    public VerifyUserPayload verifyUser(@Argument UserVerifyData userVerifyData) {
        identityService.verifyUser(userVerifyData);
        return VerifyUserPayload.newBuilder()
                .verified(true)
                .build();
    }

    @MutationMapping
    public LoginResult login(@Argument UserLoginData userLoginData) {
        return identityService.login(userLoginData);
    }

    @MutationMapping
    public LoginResult refresh(@Argument String refreshToken) {
        return identityService.refreshToken(refreshToken);
    }

    @MutationMapping
    public ResetPasswordRequestPayload resetPasswordRequest(@Argument String email) {
        identityService.resetPasswordRequest(email);
        return ResetPasswordRequestPayload.newBuilder()
                .requested(true)
                .build();
    }

    @MutationMapping
    public LogoutPayload logout(@Argument String refreshToken) {
        identityService.logout(refreshToken);
        return LogoutPayload.newBuilder()
                .loggedOut(true)
                .build();
    }

    @MutationMapping
    public ResetPasswordPayload resetPassword(@Argument ResetPasswordData resetPasswordData) {
        identityService.resetPassword(resetPasswordData);
        return ResetPasswordPayload.newBuilder()
                .reset(true)
                .build();
    }

    @MutationMapping
    public boolean setInstallationId(@Argument String installationId) {
        identityService.updateInstallationId(getUserId(), installationId);
        return true;
    }

    private UUID getUserId() {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization == null) {
             throw new RuntimeException();
        }

        String token = authorization.substring(7).trim();
        if (token.isEmpty()) {
            throw new RuntimeException();
        }

        String userId = jwtService.getUserId(token);
        return UUID.fromString(userId);
    }
}
