package com.backend.cms.request;

import com.backend.cms.model.User;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class CreateInitAdminRequest {

    @NotBlank(message = "First name cannot be blank")
    @Pattern(regexp = "^[a-zA-Z\\s]{2,20}$", message = "First name must be 2-20 characters and contain only letters and spaces.")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Pattern(regexp = "^[a-zA-Z\\s]{2,20}$", message = "Last name must be 2-20 characters and contain only letters and spaces.")
    private String lastName;
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$", message = "Email must be valid.")
    private String email;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,16}$", message = "Password must be 8-16 characters and meet special criteria ")
    private String password;

    public User toAdmin() {
        return new User(firstName, lastName, email, password);
    }

    public void setFirstName(String firstName) {
        this.firstName =  firstName != null ? firstName.trim() : null;
    }

    public void setLastName(String lastName) {
        this.lastName =  lastName != null ? lastName.trim() : null;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : null;
    }

    public void setPassword(String password) {
        this.password = password != null ? password.trim() : null;
    }

    @Override
    public String toString() {
        return "CreateAdminRequest{" +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email=" + email +
                '}';
    }
}
