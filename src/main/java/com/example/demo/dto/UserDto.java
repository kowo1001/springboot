package com.example.demo.dto;

import org.hibernate.annotations.NotFound;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NonNull
    private String userId;

    @NonNull
    private String password;

    @NonNull
    private String userName;

    @NonNull
    private String phoneNumber;

    private String userImage;

    @NonNull
    private String role;
}
