package com.lostandfound.exception;

import com.lostandfound.exception.claim.ClaimException;
import com.lostandfound.exception.claim.ClaimItemNotFoundException;
import com.lostandfound.exception.claim.ClaimQuantityException;
import com.lostandfound.exception.claim.ClaimingUserNotFoundException;
import com.lostandfound.exception.file.FileException;
import com.lostandfound.exception.file.FileNotFoundException;
import com.lostandfound.exception.file.UnSupportedFileFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationExceptionHandlerTest {

    private ApplicationExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new ApplicationExceptionHandler();
    }

    @Test
    void shouldHandleFileNotFoundException() {
        FileNotFoundException exception = new FileNotFoundException("File not found");
        ProblemDetail problemDetail = exceptionHandler.handleFileException(exception);

        assertThat(problemDetail.getTitle()).isEqualTo("File not found");
        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("File not found");
    }

    @Test
    void shouldHandleUnSupportedFileFormatException() {
        UnSupportedFileFormatException exception = new UnSupportedFileFormatException("Wrong file format");
        ProblemDetail problemDetail = exceptionHandler.handleFileException(exception);

        assertThat(problemDetail.getTitle()).isEqualTo("Wrong file format");
        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Wrong file format");
    }

    @Test
    void shouldHandleFileException() {
        FileException exception = new FileException("File Exception");
        ProblemDetail problemDetail = exceptionHandler.handleFileException(exception);

        assertThat(problemDetail.getTitle()).isEqualTo("Exception while processing file");
        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(problemDetail.getDetail()).isEqualTo("File Exception");
    }

    @Test
    void shouldHandleClaimQuantityException() {
        ClaimQuantityException exception = new ClaimQuantityException("Wrong Quantity");
        ProblemDetail problemDetail = exceptionHandler.handleClaimException(exception);

        assertThat(problemDetail.getTitle()).isEqualTo("Claim not possible : Wrong Quantity");
        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Wrong Quantity");
    }

    @Test
    void shouldHandleClaimingUserNotFoundException() {
        ClaimingUserNotFoundException exception = new ClaimingUserNotFoundException("Wrong userId");
        ProblemDetail problemDetail = exceptionHandler.handleClaimException(exception);

        assertThat(problemDetail.getTitle()).isEqualTo("Claim not possible: Wrong userId");
        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Wrong userId");
    }

    @Test
    void shouldHandleClaimItemNotFoundException() {
        ClaimItemNotFoundException exception = new ClaimItemNotFoundException("Wrong itemId");
        ProblemDetail problemDetail = exceptionHandler.handleClaimException(exception);

        assertThat(problemDetail.getTitle()).isEqualTo("Claim not possible: Wrong itemId");
        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Wrong itemId");
    }

    @Test
    void shouldHandleClaimException() {
        ClaimException exception = new ClaimException("Something went wrong while claiming");
        ProblemDetail problemDetail = exceptionHandler.handleClaimException(exception);

        assertThat(problemDetail.getTitle()).isEqualTo("Exception while claiming the item");
        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Something went wrong while claiming");
    }

    @Test
    void shouldHandleNoResourceFoundException() {
        NoResourceFoundException exception = new NoResourceFoundException(null, "Resource not found");
        ProblemDetail problemDetail = exceptionHandler.handleNoResourceFoundException(exception);

        assertThat(problemDetail.getTitle()).isEqualTo("Resource you are trying to access not found!");
        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("No static resource Resource not found.");
    }

    @Test
    void shouldHandleAllOtherExceptions() {
        Exception exception = new Exception("Unexpected error");
        ProblemDetail problemDetail = exceptionHandler.handleAllOtherExceptions(exception);

        assertThat(problemDetail.getTitle()).isEqualTo("Something went wrong!");
        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Unexpected error");
    }
}
