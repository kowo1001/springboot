package com.example.demo.repository;

import java.util.List;

import com.example.demo.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, String> {

    @Query(value = "SELECT * FROM user WHERE role = ?", nativeQuery = true)
    List<User> findAllUserByRole(String user);

}
