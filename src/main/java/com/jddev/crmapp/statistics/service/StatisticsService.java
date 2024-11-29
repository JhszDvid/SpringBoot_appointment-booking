package com.jddev.crmapp.statistics.service;

import com.jddev.crmapp.booking.enums.AppointmentStatus;
import com.jddev.crmapp.booking.repository.AppointmentRepository;
import com.jddev.crmapp.customer.repository.CustomerRepository;
import com.jddev.crmapp.exception.InternalErrorException;
import com.jddev.crmapp.statistics.dto.AggregatedStats;
import com.jddev.crmapp.statistics.dto.FetchStatResponse;
import com.jddev.crmapp.statistics.dto.YearlyStats;
import com.jddev.crmapp.statistics.model.Statistics;
import com.jddev.crmapp.statistics.model.StatisticsID;
import com.jddev.crmapp.statistics.repository.StatisticsRepository;
import com.jddev.crmapp.utility.db.DbService;
import com.jddev.crmapp.utility.event.StatUpdateEvent;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
@Service
public class StatisticsService {

    Logger logger = LoggerFactory.getLogger(StatisticsService.class);
    private final DbService dbService;
    private final CustomerRepository customerRepository;

    //Temporary, as the original language used was Hungarian, so I have returned the months in Hungarian instead
    private static final Map<Short, String> monthMap = Map.ofEntries(
            Map.entry((short)1, "Január"),
            Map.entry((short)2, "Február"),
            Map.entry((short)3, "Március"),
            Map.entry((short)4, "Április"),
            Map.entry((short)5, "Május"),
            Map.entry((short)6, "Június"),
            Map.entry((short)7, "Július"),
            Map.entry((short)8, "Augusztus"),
            Map.entry((short)9, "Szeptember"),
            Map.entry((short)10, "Október"),
            Map.entry((short)11, "November"),
            Map.entry((short)12, "December")
    );

    public StatisticsService(DbService dbService, CustomerRepository customerRepository) {
        this.dbService = dbService;
        this.customerRepository = customerRepository;
    }

    private AppointmentStatInfo RetrieveAppointmentStats(LocalDate firstDayOfMonth, LocalDate lastDayOfMonth) {
        // Retrieve stat info from appointment repo
        logger.info("Retrieving statistics with firstDayOfMonth: " + firstDayOfMonth.toString() + " lastDayOfMonth: " + lastDayOfMonth.toString());
        List<Object[]> appointmentStats = dbService.executeCustomMethod(AppointmentRepository.class,
                repo -> ((AppointmentRepository) repo).getStatistics(firstDayOfMonth, lastDayOfMonth)
        );

        if(appointmentStats.size() != 1)
            throw new InternalErrorException("Stat Service Error, Unexpected result");

        Object[] stat = appointmentStats.get(0);

        Integer canceledCount = stat[0] == null ? 0 : ((Number) stat[0]).intValue();
        Integer completedCount = stat[1] == null ? 0 : ((Number) stat[1]).intValue();
        Float totalPrice = stat[2] == null ? 0 : ((Number) stat[2]).floatValue();

        logger.info("cancelledCount: " + canceledCount + " completedCount: " + completedCount + " totalPrice: " + totalPrice);
        return new AppointmentStatInfo(canceledCount, completedCount, totalPrice);
    }

    private Integer RetrieveCustomerCount(LocalDate start){
        LocalDateTime startOfMonth = start.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate().atTime(23, 59, 59, 999999999);

        Long countVal = dbService.executeCustomMethod(CustomerRepository.class,
            repo -> ((CustomerRepository) repo).countByDateBetween(startOfMonth, endOfMonth)
        );

        System.out.println("StartOfMonth: " + startOfMonth);
        System.out.println("EndOfMonth: " + endOfMonth);
        System.out.println("Count: " + countVal.intValue());
        return countVal.intValue();
    }

    @Async
    @EventListener
    public void UpdateStats(StatUpdateEvent event){
        logger.info("UpdateStats Started");
        // Get current date data
        LocalDate currDate = LocalDate.now();
        Short currYear = (short) currDate.getYear();
        Short currMonth = (short) currDate.getMonthValue();
        LocalDate firstDayOfMonth = currDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = currDate.with(TemporalAdjusters.lastDayOfMonth());


        // Find Stat row
        StatisticsID idToLookUp = new StatisticsID(currYear, currMonth);
        Optional<Statistics> LookupStat = dbService.executeCustomMethod(StatisticsRepository.class,
                repo -> ((StatisticsRepository) repo).findById(idToLookUp)
        );

        // If it does not exist, create one
        Statistics statToModify = LookupStat.orElseGet(() -> new Statistics.Builder(idToLookUp).build());

        // Retrieve stats
        AppointmentStatInfo appStats = RetrieveAppointmentStats(firstDayOfMonth, lastDayOfMonth);
        Integer amountOfCustomersThisMonth = RetrieveCustomerCount(firstDayOfMonth);

        // Apply stat values into the row
        statToModify.setTotalPrice(appStats.totalPrice);
        statToModify.setNumberOfCanceled(appStats.canceledCount);
        statToModify.setNumberOfCompleted(appStats.completedCount);
        statToModify.setNumberOfClients(amountOfCustomersThisMonth);

        dbService.save(StatisticsRepository.class, statToModify);
        logger.info("UpdateStats Finished");
    }

    public FetchStatResponse FetchStats(Short year)
    {
        // Find all rows and aggregate the stats
        List<Statistics> stats = dbService.findAll(StatisticsRepository.class);

        // Calculate the sums
        Integer sumOfCustomers = stats.stream()
                .mapToInt(stat -> stat.getNumberOfClients() != null ? stat.getNumberOfClients() : 0)
                .sum();

        Float sumOfTotalPrices = stats.stream()
                .map(stat -> stat.getTotalPrice() != null ? stat.getTotalPrice() : 0.0f)
                .reduce(0.0f, Float::sum);

        Integer sumOfCanceledAppointments = stats.stream()
                .mapToInt(stat -> stat.getNumberOfCanceled() != null ? stat.getNumberOfCanceled() : 0)
                .sum();

        Integer sumOfCompletedAppointments = stats.stream()
                .mapToInt(stat -> stat.getNumberOfCompleted() != null ? stat.getNumberOfCompleted() : 0)
                .sum();

        // Build up the aggregated results
        AggregatedStats totals = new AggregatedStats(
                sumOfCompletedAppointments,
                sumOfCanceledAppointments,
                sumOfTotalPrices,
                sumOfCustomers
        );

        // Fetch the yearly results
        List<Statistics> yearlyStatsList = dbService.executeCustomMethod(StatisticsRepository.class,
            repo -> ((StatisticsRepository) repo).findById_YearValue(year)
        );

        // Build the yearly results
        List<YearlyStats> yearlyStats = new ArrayList<>();
        for (Statistics stat: yearlyStatsList) {
            yearlyStats.add(
                new YearlyStats(monthMap.get(stat.getId().getMonthIndex()), stat.getNumberOfCompleted(), stat.getNumberOfCanceled(), stat.getTotalPrice(), stat.getNumberOfClients())
            );
        }

        return new FetchStatResponse(totals, yearlyStats);
    }

    private class AppointmentStatInfo{
        private Integer canceledCount;
        private Integer completedCount;
        private Float totalPrice;

        public AppointmentStatInfo(Integer canceledCount, Integer completedCount, Float totalPrice) {
            this.canceledCount = canceledCount;
            this.completedCount = completedCount;
            this.totalPrice = totalPrice;
        }
    }

                                                           
}                                                          
