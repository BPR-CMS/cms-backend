package com.backend.cms.request;

import lombok.Data;

import java.util.Map;

@Data
public class CreatePostRequest {

    private Map<String, Object> attributes;

}
