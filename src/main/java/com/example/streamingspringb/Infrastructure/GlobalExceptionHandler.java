package com.example.streamingspringb.Infrastructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.SessionException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception e, Model model) {
        model.addAttribute("exception", "Uventet fejl: " + e.getClass().getSimpleName());
        return "error";
    }

    @ExceptionHandler
    public String handleEmptyResultDataAccessException(EmptyResultDataAccessException e, Model model){
        model.addAttribute("exception", "Brugernavn eller kodeord eksisterer ikke");
        return "error";
    }
    @ExceptionHandler
    public String handleSessionAuthenticationException(SessionAuthenticationException e, Model model){
        model.addAttribute("exception", "Du er ikke logget ind");
        return "error";
    }
    @ExceptionHandler
    public String handleNullPointerException(NullPointerException e, Model model){
        model.addAttribute("exception", "Du skal være logget ind");
        return "error";
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolation(DataIntegrityViolationException e, Model model) {
        String message = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();
        if (message.contains("null value") || message.contains("not-null constraint")) {
            model.addAttribute("exception", "Et felt mangler – tjek at alle felter er udfyldt.");
        } else if (message.contains("duplicate key") || message.contains("Duplicate entry")) {
            model.addAttribute("exception", "Denne post findes allerede – brug en anden ID eller email.");
        } else if (message.contains("foreign key constraint")) {
            model.addAttribute("exception", "Handlingen kunne ikke gennemføres pga. afhængigheder i databasen.");
        } else {
            model.addAttribute("exception", "Der opstod en databasefejl – prøv igen.");
        }
        return "error";
    }
    @ExceptionHandler
    public String handleNoResourceFoundException(NoResourceFoundException e, Model model) {
        model.addAttribute("exception", "Siden findes ikke eller du er ikke logget ind");
        return "error";
    }
}
