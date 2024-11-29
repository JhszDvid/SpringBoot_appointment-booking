package com.jddev.crmapp.exception;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.jddev.crmapp.enums.ResponseType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class APIResponseObject {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    private HttpStatus statusCode;

    private ResponseType responseType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object object;

    protected APIResponseObject() {}

    public ResponseEntity<Object> buildResponse() {
        return new ResponseEntity<>(this, this.statusCode);
    }

    public static class Builder{
        APIResponseObject obj;
        public Builder(){
            obj = new APIResponseObject();
            obj.setResponseType(ResponseType.SUCCESS);
            obj.setStatusCode(HttpStatus.OK);
        }

        public Builder withMessage(String message){
            obj.setMessage(message);
            return this;
        }

        public Builder withResponseType(ResponseType type) {
            obj.setResponseType(type);
            return this;
        }

        public Builder withObject(Object object){
            obj.setObject(object);
            return this;
        }

        public Builder withStatusCode(HttpStatus status) {
            obj.setStatusCode(status);
            return this;
        }

        public APIResponseObject build(){
            return obj;
        }

        public ResponseEntity<Object> buildResponse(){
            return obj.buildResponse();
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
