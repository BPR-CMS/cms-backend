package com.backend.cms.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Unsupported content type")
public class UnsupportedContentTypeException extends RuntimeException {
}