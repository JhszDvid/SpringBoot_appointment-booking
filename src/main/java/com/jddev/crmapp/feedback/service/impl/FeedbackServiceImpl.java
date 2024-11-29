package com.jddev.crmapp.feedback.service.impl;

import com.jddev.crmapp.exception.APIResponseObject;
import com.jddev.crmapp.exception.UserNotFoundException;
import com.jddev.crmapp.feedback.model.Feedback;
import com.jddev.crmapp.feedback.repository.FeedbackRepository;
import com.jddev.crmapp.feedback.service.FeedbackService;
import com.jddev.crmapp.utility.db.DbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    Logger logger = LoggerFactory.getLogger(FeedbackService.class);
    private final DbService dbService;

    public FeedbackServiceImpl(DbService dbService) {
        this.dbService = dbService;
    }

    @Override
    public APIResponseObject CreateFeedback(String email, String feedback) {
        if(email == null || email.isBlank() || email.isEmpty())
            throw new UserNotFoundException("User could not be found!");

        Feedback feedbackObj = new Feedback.Builder()
                .withUser(email)
                .withDescription(feedback)
                .build();

        dbService.save(FeedbackRepository.class, feedbackObj);

        return new APIResponseObject.Builder()
                .withMessage("Feedback successfully saved!")
                .build();
    }
}
