package pl.cinkus.IdentityService.mapper;

import org.springframework.stereotype.Component;
import pl.cinkus.IdentityService.dto.UserDataDTO;
import pl.cinkus.IdentityService.model.UserRole;
import pl.cinkus.backend.codegen.types.NewUserData;

@Component
public class UserDataMapper {
    public UserDataDTO toUserDataDTO(NewUserData userData) {
        return UserDataDTO.builder()
                .name(userData.getName())
                .surname(userData.getSurname())
                .nickName(userData.getNickName())
                .email(userData.getEmail())
                .password(userData.getPassword())
                .build();
    }

    public UserRole toUserRole(pl.cinkus.backend.codegen.types.UserRole userRole) {
        switch (userRole) {
            case USER -> {
                return UserRole.USER;
            }
            case TEACHER -> {
                return UserRole.TEACHER;
            }
            case ADMIN -> {
                return UserRole.ADMIN;
            }
        }
        return null;
    }
}
