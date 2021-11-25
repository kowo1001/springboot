package com.example.demo.controller;

import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.dto.LoginDto;
import com.example.demo.dto.UserDto;
import com.example.demo.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> insertUser(UserDto userDto,
            @RequestParam(value = "file", required = false) MultipartFile mfile) {
        Boolean check = null;
        if (mfile != null) {
            check = userService.insertUser(userDto, mfile);
        } else {
            check = userService.insertUserNoimg(userDto);
        }

        if (check) {
            return new ResponseEntity<String>("회원 가입 성공", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<String>("회원 가입 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<UserDto> login(@RequestBody LoginDto loginDto, HttpServletResponse res) {
        return userService.login(loginDto, res);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest req) {

        if (userService.logout(req)) {
            return new ResponseEntity<String>("회원 로그아웃 성공", HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<String>("회원 로그아웃 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUserList() {
        List<UserDto> iDtoList = userService.getUserList();
        return new ResponseEntity<List<UserDto>>(iDtoList, HttpStatus.OK);
    }

    @GetMapping("/userinfo")
    public ResponseEntity<UserDto> getUser(@RequestParam String userid) {
        return new ResponseEntity<UserDto>(userService.getUser(userid), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateUser(UserDto userDto,
            @RequestParam(value = "file", required = false) MultipartFile mfile) {
        Boolean check = null;
        if (mfile != null) {
            check = userService.updateUser(userDto, mfile);
        } else {
            check = userService.updateUserNoimg(userDto);
        }

        if (check) {
            return new ResponseEntity<String>("회원 정보 수정 성공", HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("회원 정보 수정 실패", HttpStatus.NOT_MODIFIED);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestParam String userid) {

        if (userService.deleteUser(userid)) {
            return new ResponseEntity<String>("회원 탈퇴 성공", HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<String>("회원 탈퇴 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}