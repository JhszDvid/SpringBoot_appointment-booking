package com.jddev.crmapp.statistics.repository;

import com.jddev.crmapp.statistics.model.Statistics;
import com.jddev.crmapp.statistics.model.StatisticsID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface StatisticsRepository extends JpaRepository<Statistics, StatisticsID> {
    List<Statistics> findById_YearValue(@NonNull Short yearValue);

    List<Statistics> findById_YearValueAndId_MonthIndex(Short yearValue, Short monthIndex);


    @Override
    Optional<Statistics> findById(StatisticsID statisticsID);
}
