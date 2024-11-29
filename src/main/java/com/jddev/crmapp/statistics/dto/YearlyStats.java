package com.jddev.crmapp.statistics.dto;

public class YearlyStats {
    private String month;
    private Integer numberOfCompleted;
    private Integer numberOfCanceled;

    private Float price;
    private Integer numberOfClients;
    public YearlyStats(String month){
        setMonth(month);
        setNumberOfCanceled(0);
        setNumberOfCompleted(0);
        setPrice(0f);
        setNumberOfClients(0);
    }

    public YearlyStats(String month, Integer numberOfCompleted, Integer numberOfCanceled, Float price, Integer numberOfClients) {
        this.month = month;
        this.numberOfCompleted = numberOfCompleted;
        this.numberOfCanceled = numberOfCanceled;
        this.price = price;
        this.numberOfClients = numberOfClients;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Integer getNumberOfCompleted() {
        return numberOfCompleted;
    }

    public void setNumberOfCompleted(Integer numberOfCompleted) {
        this.numberOfCompleted = numberOfCompleted;
    }

    public Integer getNumberOfCanceled() {
        return numberOfCanceled;
    }

    public void setNumberOfCanceled(Integer numberOfCanceled) {
        this.numberOfCanceled = numberOfCanceled;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getNumberOfClients() {
        return numberOfClients;
    }

    public void setNumberOfClients(Integer numberOfClients) {
        this.numberOfClients = numberOfClients;
    }
}
