package pl.cinkus.IdentityService.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import pl.cinkus.IdentityService.client.RabbitMqMessageSender;
import pl.cinkus.IdentityService.mapper.UserDataMapper;
import pl.cinkus.IdentityService.service.IdentityService;
import pl.cinkus.backend.codegen.types.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor

public class IdentityDataFetcher {
    private final IdentityService identityService;

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

    @QueryMapping
    public List<String> getAllUsers() {
        //toDo check if authenticated and authorize only for admin
        return identityService.getAllUsers();
    }
}
