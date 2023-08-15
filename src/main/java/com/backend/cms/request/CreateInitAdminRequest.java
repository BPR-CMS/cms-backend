package com.backend.cms.request;

import com.backend.cms.model.User;
import lombok.Data;

@Data
public class CreateInitAdminRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public User toAdmin() {
        return new User(firstName, lastName, email, password);
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
