package org.koulibrary.koulibraryreservationapp.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LibraryAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDetail handleDuplicatedRepositoryException(LibraryAlreadyExistsException ex, HttpServletRequest request) {
        return ErrorDetail.builder()
                .error(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<FieldErrorResponse> fields = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorResponse(
                        error.getField(),
                        error.getDefaultMessage()))
                .toList();

        ValidationErrorResponse response = new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                request.getRequestURI(),
                fields
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(LibrariesTableIsEmptyException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public ErrorDetail handleLibrariesTableIsEmptyException(LibrariesTableIsEmptyException ex, HttpServletRequest request) {
        return ErrorDetail.builder()
                .error(ex.getMessage())
                .status(HttpStatus.NO_CONTENT.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(LibraryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDetail handleLibraryNotFoundException(LibraryNotFoundException ex, HttpServletRequest request) {
        return ErrorDetail.builder()
                .error(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .build();
    }


    @ExceptionHandler(EndDateCannotBeBeforeStartDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDetail handleEndDateCannotBeBeforeStartDateException(EndDateCannotBeBeforeStartDateException ex, HttpServletRequest request) {
        return ErrorDetail.builder()
                .error(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(IntervalDateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDetail handleIntervalDateException(IntervalDateException ex, HttpServletRequest request) {
        return ErrorDetail.builder()
                .error(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(LibraryWorkingHoursAlreadyCreatedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDetail handleLibraryWorkingHoursAlreadyCreatedException(LibraryWorkingHoursAlreadyCreatedException ex, HttpServletRequest request) {
        return ErrorDetail.builder()
                .error(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(InvalidWorkingHourRangeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDetail handleInvalidWorkingHourRangeException(InvalidWorkingHourRangeException ex, HttpServletRequest request) {
        return ErrorDetail.builder()
                .error(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(LibraryWorkingHoursNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDetail handleLibraryWorkingHoursNotFoundException(LibraryWorkingHoursNotFoundException ex, HttpServletRequest request) {
        return ErrorDetail.builder()
                .error(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(SaloonAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDetail handleSaloonAlreadyExistException(SaloonAlreadyExistException ex, HttpServletRequest request) {
        return ErrorDetail.builder()
                .error(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .timestamp(LocalDateTime.now())
                .build();
    }







}
