package presentation;

import domain.EventAccessDeniedException;
import domain.InvalidCredentialsException;
import domain.UserNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(InvalidCredentialsException.class)
    public String handleInvalidCredentialsException(InvalidCredentialsException e, Model model){
        model.addAttribute("errorMessage", e.getMessage());
        return "login";
    }
    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(UserNotFoundException e, Model model){
        model.addAttribute("errorMessage", e.getMessage());
        return "login";
    }
    @ExceptionHandler(EventAccessDeniedException.class)
    public String handleEventAccessDeniedException(EventAccessDeniedException e, Model model){
        model.addAttribute("errorMessage", e.getMessage());
        return "events";
    }

    @ExceptionHandler({DuplicateKeyException.class, DataIntegrityViolationException.class, SQLIntegrityConstraintViolationException.class})
    public String handleDuplicateEntry(Exception e, Model model){
        model.addAttribute("errorMessage", "You have already entered this event.");
        return "events";
    }
}