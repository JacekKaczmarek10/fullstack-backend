package com.kaczmarek.fullstackbackend.ut.exception;

import com.kaczmarek.fullstackbackend.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    @Test
    void handleValidationExceptions_shouldReturnBadRequestWithFieldErrors() {
        var bindingResult = mock(BindingResult.class);
        var ex = new MethodArgumentNotValidException(null, bindingResult);
        var fieldErrors = List.of(
            new FieldError("objectName", "username", "must not be blank"),
            new FieldError("objectName", "password", "must be at least 8 characters")
        );
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        var handler = new GlobalExceptionHandler();

        final var response = handler.handleValidationExceptions(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        var body = response.getBody();
        assertNotNull(body);
        assertThat(body.size()).isEqualTo(2);
        assertThat(body.get("username")).isEqualTo("must not be blank");
        assertThat(body.get("password")).isEqualTo("must be at least 8 characters");
    }

}