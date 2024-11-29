package com.jddev.crmapp.statistics.dto;

import java.time.Year;
import java.util.List;

public record FetchStatResponse(
        AggregatedStats totals,
        List<YearlyStats> yearly
) {
}
