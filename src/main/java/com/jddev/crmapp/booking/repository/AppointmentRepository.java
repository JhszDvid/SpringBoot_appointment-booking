package com.jddev.crmapp.booking.repository;

import com.jddev.crmapp.booking.enums.AppointmentStatus;
import com.jddev.crmapp.booking.model.Appointment;
import com.jddev.crmapp.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDateBetween(@NonNull LocalDate dateStart, @NonNull LocalDate dateEnd);

    List<Appointment> findByDateAndStatus(@NonNull LocalDate date, AppointmentStatus status);

    List<Appointment> findByStatus(AppointmentStatus status);



    @Query("select a from Appointment a where a.booked_by = ?1")
    List<Appointment> findByBooked_by(@NonNull Customer booked_by);

    @Query("select a from Appointment a where a.booked_by = ?1 and a.status = ?2")
    List<Appointment> findByBooked_byAndStatus(@NonNull Customer booked_by, @NonNull AppointmentStatus status);













    @Query("SELECT SUM(CASE WHEN a.status = 'CANCELED' THEN 1 ELSE 0 END) AS canceledCount, " +
            "SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) AS completedCount, " +
            "SUM(CASE WHEN a.status IN ('CANCELED', 'COMPLETED') THEN a.price ELSE 0 END) AS totalPrice " +
            "FROM Appointment a " +
            "WHERE a.date BETWEEN :startDate AND :endDate")
    List<Object[]> getStatistics(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
