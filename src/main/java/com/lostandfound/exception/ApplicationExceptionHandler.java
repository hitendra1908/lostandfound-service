package com.lostandfound.exception;

import com.lostandfound.exception.claim.ClaimException;
import com.lostandfound.exception.claim.ClaimItemNotFoundException;
import com.lostandfound.exception.claim.ClaimQuantityException;
import com.lostandfound.exception.claim.ClaimingUserNotFoundException;
import com.lostandfound.exception.file.FileException;
import com.lostandfound.exception.file.FileNotFoundException;
import com.lostandfound.exception.file.UnSupportedFileFormatException;
import com.lostandfound.exception.user.InvalidUserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(FileException.class)
    public ProblemDetail handleFileException(FileException exception) {
        return switch (exception) {
            case FileNotFoundException e -> createProblemDetail(e, HttpStatus.BAD_REQUEST, "File not found");
            case UnSupportedFileFormatException e -> createProblemDetail(e, HttpStatus.BAD_REQUEST, "Wrong file format");
            default -> createProblemDetail(exception, HttpStatus.INTERNAL_SERVER_ERROR, "Exception while processing file");
        };
    }

    @ExceptionHandler(ClaimException.class)
    public ProblemDetail handleClaimException(ClaimException exception) {
        return switch (exception) {
            case ClaimQuantityException e -> createProblemDetail(e, HttpStatus.BAD_REQUEST, "Claim not possible : Wrong Quantity");
            case ClaimingUserNotFoundException e -> createProblemDetail(e, HttpStatus.BAD_REQUEST, "Claim not possible: Wrong userId");
            case ClaimItemNotFoundException e -> createProblemDetail(e, HttpStatus.BAD_REQUEST, "Claim not possible: Wrong itemId");
            default -> createProblemDetail(exception, HttpStatus.BAD_REQUEST, "Exception while claiming the item");
        };
    }

    @ExceptionHandler(InvalidUserException.class)
    public ProblemDetail handleInvalidUserException(InvalidUserException exception) {
        log.error("Invalid User: ", exception);
        return createProblemDetail(exception, HttpStatus.BAD_REQUEST, "Invalid User request");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNoResourceFoundException(NoResourceFoundException exception) {
        log.error("Resource not found: ", exception);
        return createProblemDetail(exception, HttpStatus.BAD_REQUEST, "Resource you are trying to access not found!");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {
        log.error("Access denied ", exception);
        return createProblemDetail(exception, HttpStatus.FORBIDDEN, "You are not authorized to do this activity!");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleAccessDeniedException(DataIntegrityViolationException exception) {
        log.error("Bad Request ", exception);
        return createProblemDetail(exception, HttpStatus.BAD_REQUEST, "Invalid request");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAllOtherExceptions(Exception exception) {
        log.error("Generic exception occurred while processing the request: ", exception);
        return createProblemDetail(exception, HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
    }

    private static ProblemDetail createProblemDetail(Exception exception, HttpStatus status, String title) {
        var problemDetail = ProblemDetail.forStatusAndDetail(status, exception.getMessage());
        problemDetail.setTitle(title);
        return problemDetail;
    }
}
