package ru.aston.intensive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.aston.intensive.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query(value = "SELECT * FROM users as u WHERE u.email = :email", nativeQuery = true)
    Optional<UserEntity> findByEmail(@Param("email") String email);
}
