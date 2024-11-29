package com.jddev.crmapp.feedback.model;

import com.jddev.crmapp.authentication.model.AppUser;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;



    @Column(name = "description", length = 50000)
    private String description;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "user_email")
    private String userEmail;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    protected Feedback(){}

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Long getId() {
        return id;
    }

    public static class Builder {
        Feedback feedback;
        public Builder(){
            feedback = new Feedback();
        }

        public Builder withUser(String email) {
            feedback.setUserEmail(email);
            return this;
        }

        public Builder withDescription(String desc) {
            feedback.setDescription(desc);
            return this;
        }

        public Feedback build(){
            feedback.setDate(new Date());
            return feedback;
        }
    }


}