package com.jddev.crmapp.statistics.controller;

import com.jddev.crmapp.exception.APIResponseObject;
import com.jddev.crmapp.statistics.dto.FetchStatResponse;
import com.jddev.crmapp.statistics.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("api/v1/statistics")
public class StatsController {

    private final StatisticsService statisticsService;

    public StatsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public ResponseEntity<?> FetchStatistics( @RequestParam(value = "year", defaultValue = "2020", required = false) Short yearValue){
        FetchStatResponse responseObj = statisticsService.FetchStats(yearValue);

        return new APIResponseObject.Builder()
                .withObject(responseObj)
                .buildResponse();
    }
}
