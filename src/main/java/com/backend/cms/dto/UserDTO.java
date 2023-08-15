package com.backend.cms.dto;

import com.backend.cms.model.User;
import com.backend.cms.model.UserType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserType userType;

    public UserDTO(User user) {
        this.userId = user.getUserId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.userType = user.getUserType();
    }

    public static UserDTO fromUser(User user) {
        return new UserDTO(user);
    }
}
