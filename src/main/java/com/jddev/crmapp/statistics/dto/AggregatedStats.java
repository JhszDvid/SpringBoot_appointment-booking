package com.jddev.crmapp.statistics.dto;

public record AggregatedStats(
        Integer numberOfCompleted,
        Integer numberOfCanceled,
        Float price,
        Integer numberOfClients
) {
}
