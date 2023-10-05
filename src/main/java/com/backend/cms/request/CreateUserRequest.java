package com.backend.cms.request;

import com.backend.cms.model.AccountStatus;
import com.backend.cms.model.User;
import com.backend.cms.model.UserType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class CreateUserRequest {

    @NotBlank(message = "First name cannot be blank")
    @Pattern(regexp = "^[a-zA-Z\\s]{2,20}$", message = "First name must be 2-20 characters and contain only letters and spaces.")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Pattern(regexp = "^[a-zA-Z\\s]{2,20}$", message = "Last name must be 2-20 characters and contain only letters and spaces.")
    private String lastName;
    @NotBlank(message = "Email cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$", message = "Email must be valid.")
    private String email;

    private UserType userType;
    private AccountStatus accountStatus;

    public void setFirstName(String firstName) {
        this.firstName = firstName != null ? firstName.trim() : null;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName != null ? lastName.trim() : null;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : null;
    }

    public User toUser() {
        return new User(firstName, lastName, email, userType, accountStatus);
    }
}
