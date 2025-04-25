package com.example.employeeAtt.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.employeeAtt.models.Attendance;
import com.example.employeeAtt.models.Employee;

public interface AttendanceRepository extends JpaRepository<Attendance,Long>{

    // Fetch attendance entries for an employee for a specific date
    @Query("SELECT a FROM Attendance a WHERE a.employee.employeeId = :employeeId AND DATE(a.loginTime) = CURRENT_DATE")
    List<Attendance> findTodayAttendanceByEmployee(@Param("employeeId") String employeeId);
    
    @Query("SELECT a FROM Attendance a WHERE DATE(a.loginTime) = :date")
    List<Attendance> findByLoginDate(@Param("date") LocalDate date);

    // Find all employees who logged in on a specific date
    @Query("SELECT DISTINCT a.employee.employeeId FROM Attendance a WHERE FUNCTION('DATE', a.loginTime) = :date AND a.attendanceType = 'Login'")
    List<String> findPresentEmployeeIdsByDate(@Param("date") LocalDate date);


    @Query("SELECT e FROM Employee e WHERE e.employeeId NOT IN ("
     + "SELECT a.employee.employeeId FROM Attendance a "
     + "WHERE FUNCTION('DATE', a.loginTime) = :date AND a.attendanceType = 'Login')")
    List<Employee> findAbsenteesByDate(@Param("date") LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.employee.employeeId = :employeeId AND a.loginTime BETWEEN :startOfDay AND :endOfDay")
    List<Attendance> findByUserIdAndDate(
        @Param("employeeId") String employeeId,
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("endOfDay") LocalDateTime endOfDay
    );

    @Query("SELECT a FROM Attendance a WHERE FUNCTION('MONTH', a.loginTime) = :month AND FUNCTION('YEAR', a.loginTime) = :year")
        List<Attendance> findByMonthAndYear(String month, String year);

        @Query(value = """
        SELECT 
            DATE(a.attendance_time) AS date,
            COUNT(DISTINCT CASE WHEN a.attendance_type = 'Login' THEN a.employee_id END) AS present
        FROM attendance a
        WHERE a.attendance_type = 'Login'
          AND a.attendance_time >= :startDate
        GROUP BY DATE(a.attendance_time)
        ORDER BY DATE(a.attendance_time)
    """, nativeQuery = true)
    List<Object[]> getWeeklyPresentCounts(@Param("startDate") Date startDate);
}
