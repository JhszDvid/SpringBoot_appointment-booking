package com.jddev.crmapp.statistics.model;

import jakarta.persistence.*;

@Entity
@Table(name = "statistics")
public class Statistics {
    @EmbeddedId
    private StatisticsID id;

    @Column(name = "total_price", nullable = false)
    private Float totalPrice;

    @Column(name = "number_of_completed")
    private Integer numberOfCompleted;

    @Column(name = "number_of_canceled", nullable = false)
    private Integer numberOfCanceled;

    @Column(name = "number_of_clients")
    private Integer numberOfClients;

    public static class Builder {
        Statistics stat;
        public Builder(StatisticsID id)
        {
            stat = new Statistics();
            stat.setNumberOfCompleted(0);
            stat.setId(id);
            stat.setNumberOfCanceled(0);
            stat.setTotalPrice(0.0f);
            stat.setNumberOfClients(0);
        }

        public Builder withNumberOfCompleted(Integer number)
        {
            stat.setNumberOfCompleted(number);
            return this;
        }
        public Builder withNumberOfCanceled(Integer number)
        {
            stat.setNumberOfCanceled(number);
            return this;
        }

        public Builder withTotalPrice(Float number)
        {
            stat.setTotalPrice(number);
            return this;
        }

        public Builder withNumberOfClients(Integer number)
        {
            stat.setNumberOfClients(number);
            return this;
        }
        public Statistics build(){
            return stat;
        }
    }

    public Integer getNumberOfClients() {
        return numberOfClients;
    }

    public void setNumberOfClients(Integer numberOfClients) {
        this.numberOfClients = numberOfClients;
    }

    public Integer getNumberOfCanceled() {
        return numberOfCanceled;
    }

    public void setNumberOfCanceled(Integer numberOfCanceled) {
        this.numberOfCanceled = numberOfCanceled;
    }

    public Integer getNumberOfCompleted() {
        return numberOfCompleted;
    }

    public void setNumberOfCompleted(Integer numberOfCompleted) {
        this.numberOfCompleted = numberOfCompleted;
    }

    public Float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public StatisticsID getId() {
        return id;
    }

    public void setId(StatisticsID id) {
        this.id = id;
    }



}