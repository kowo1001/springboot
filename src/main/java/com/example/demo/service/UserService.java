package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.UserDto;

import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    public Boolean insertUser(UserDto userdto, MultipartFile mfile);

    public Boolean insertUserNoimg(UserDto userDto);

    public UserDto getUser(String userid);

    public List<UserDto> getUserList();

    public Boolean updateUser(UserDto userDto, MultipartFile mfile);

    public Boolean updateUserNoimg(UserDto userDto);

    public Boolean deleteUser(String userid);

}
