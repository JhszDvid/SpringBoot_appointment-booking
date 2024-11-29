package com.jddev.crmapp.feedback.controller;

import com.jddev.crmapp.authentication.model.AppUser;
import com.jddev.crmapp.authentication.model.UserPrincipal;
import com.jddev.crmapp.exception.APIResponseObject;
import com.jddev.crmapp.feedback.dto.CreateFeedbackRequest;
import com.jddev.crmapp.feedback.service.FeedbackService;
import jakarta.validation.Valid;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<?> CreateFeedback(@Valid @RequestBody CreateFeedbackRequest request){
        String loggedInUserEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        APIResponseObject responseObject = feedbackService.CreateFeedback(loggedInUserEmail, request.description());

        return responseObject.buildResponse();
    }
}
