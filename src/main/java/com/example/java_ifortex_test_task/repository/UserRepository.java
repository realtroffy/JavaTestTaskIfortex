package com.example.java_ifortex_test_task.repository;

import com.example.java_ifortex_test_task.entity.DeviceType;
import com.example.java_ifortex_test_task.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = """
            SELECT u.*
            FROM users u
            JOIN (
                SELECT DISTINCT ON (s.user_id) s.user_id, s.started_at_utc
                FROM sessions s
                WHERE s.device_type = :#{#deviceType.code}
                ORDER BY s.user_id, s.started_at_utc DESC
            ) filtered_sessions ON u.id = filtered_sessions.user_id
            WHERE u.deleted = false
            ORDER BY filtered_sessions.started_at_utc DESC
            """, nativeQuery = true)
    List<User> getUsersWithAtLeastOneMobileSession(DeviceType deviceType);

    @Query(value = """
            SELECT u.*
            FROM users u
            JOIN sessions s ON u.id = s.user_id
            WHERE u.deleted = false
            GROUP BY u.id
            ORDER BY COUNT(s.id) DESC
            LIMIT 1
            """,
            nativeQuery = true)
    User getUserWithMostSessions();
}
