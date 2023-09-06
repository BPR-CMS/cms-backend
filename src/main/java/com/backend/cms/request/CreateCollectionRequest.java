package com.backend.cms.request;

import com.backend.cms.model.Collection;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class CreateCollectionRequest {

    @NotBlank(message = "Name cannot be blank")
    @Pattern(regexp = "^[a-zA-Z\\s]{2,20}$", message = "Name must be 2-20 characters and contain only letters and spaces.")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9\\s.,!?]{10,500}$", message = "Description must be 10-500 characters.")
    private String description;

    public void setName(String name) {
        this.name =  name != null ? name.trim() : null;
    }

    public void setDescription(String description) {
        this.description = description!= null ? description.trim() : null;
    }

    public Collection toCollection() {
        return new Collection(name, description);
    }
}
