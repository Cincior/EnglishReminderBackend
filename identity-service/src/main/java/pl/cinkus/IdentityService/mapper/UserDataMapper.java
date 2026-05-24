package pl.cinkus.IdentityService.mapper;

import org.springframework.stereotype.Component;
import pl.cinkus.IdentityService.model.UserRole;
import pl.cinkus.backend.codegen.types.NewUserData;

@Component
public class UserDataMapper {

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
