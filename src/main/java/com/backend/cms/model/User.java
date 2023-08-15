package com.backend.cms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Document(collection = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private ObjectId _id;

    @Indexed(unique = true)
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserType userType;

    public User( String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
}
