package com.backend.cms.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class SetPasswordRequest {
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,16}$", message = "Password must be 8-16 characters and meet special criteria ")
    private String password;

    public void setPassword(String password) {
        this.password = password != null ? password.trim() : null;
    }
}
