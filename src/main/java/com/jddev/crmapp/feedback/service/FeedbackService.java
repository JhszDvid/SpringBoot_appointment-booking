package com.jddev.crmapp.feedback.service;

import com.jddev.crmapp.authentication.model.AppUser;
import com.jddev.crmapp.exception.APIResponseObject;

public interface FeedbackService {
    public APIResponseObject CreateFeedback(String email, String feedback);
}
