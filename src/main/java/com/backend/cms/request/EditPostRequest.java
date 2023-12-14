package com.backend.cms.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
@Data
@NoArgsConstructor

public class EditPostRequest {

    private Map<String, Object> attributes;

}
