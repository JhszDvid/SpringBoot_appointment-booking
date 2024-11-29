package com.jddev.crmapp.statistics.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;
@Embeddable
public class StatisticsID implements Serializable {
    private Short yearValue;
    private Short monthIndex;

    public StatisticsID() {
    }

    public StatisticsID(Short yearValue, Short monthIndex) {
        this.yearValue = yearValue;
        this.monthIndex = monthIndex;
    }

    public Short getYearValue() {
        return yearValue;
    }

    public void setYearValue(Short yearValue) {
        this.yearValue = yearValue;
    }

    public Short getMonthIndex() {
        return monthIndex;
    }

    public void setMonthIndex(Short monthIndex) {
        this.monthIndex = monthIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatisticsID that = (StatisticsID) o;
        return Objects.equals(yearValue, that.yearValue) && Objects.equals(monthIndex, that.monthIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(yearValue, monthIndex);
    }
}
