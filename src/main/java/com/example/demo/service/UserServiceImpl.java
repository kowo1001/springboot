package com.example.demo.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import com.example.demo.dto.LoginDto;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.Manager;
import com.example.demo.entity.Member;
import com.example.demo.entity.User;
import com.example.demo.jwt.JwtFilter;
import com.example.demo.jwt.TokenProvider;
import com.example.demo.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider,
            AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @Override
    public ResponseEntity<UserDto> login(LoginDto loginDto, HttpServletResponse res) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUserId(), loginDto.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer" + jwt);

        Optional<User> userOpt = userRepository.findById(authentication.getName());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            UserDto userDto = UserDto.builder().userId(user.getUserId()).userName(user.getUserName())
                    .phoneNumber(user.getPhoneNumber()).userImage(user.getUserImage()).role(user.getRole()).build();

            logger.info("????????? ?????? ?????? ?????? ??????");
            return new ResponseEntity<>(userDto, httpHeaders, HttpStatus.OK);
        } else {
            logger.info("????????? ?????? ?????? ?????? ??????");
            return null;
        }
    }

    @Override
    public Boolean logout(HttpServletRequest req) {

        try {
            logger.info("???????????? ??????");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("???????????? ??????");
            return false;
        }
    }

    public Boolean insertUser(UserDto userDto, MultipartFile mfile) {
        Optional<User> User = userRepository.findById(userDto.getUserId());

        try {
            if (!User.isPresent()) {
                String imgname = null;

                try {
                    imgname = String.valueOf(System.currentTimeMillis()) + mfile.getOriginalFilename();
                    mfile.transferTo(
                            new File(System.getProperty("user.dir") + "\\src\\main\\webapp\\userimg" + imgname));
                    logger.info("{} ???????????? ????????? ??????", userDto.getUserName());
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                    logger.error("{} ???????????? ????????? ?????? ??????", userDto.getUserName());
                }

                if (userDto.getRole().equals("member")) {
                    Member member = new Member();
                    member.setUserId(userDto.getUserId());
                    member.setPassword(passwordEncoder.encode(userDto.getPassword()));
                    member.setUserName(userDto.getUserName());
                    member.setPhoneNumber(userDto.getPhoneNumber());
                    member.setUserImage(userDto.getUserImage());

                    userRepository.save(member);
                    logger.info("{} ?????? ???????????? ??????", userDto.getUserName());
                    return true;

                } else if (userDto.getRole().equals("admin")) {
                    Manager manager = new Manager();
                    manager.setUserId(userDto.getUserId());
                    manager.setPassword(passwordEncoder.encode(userDto.getPassword()));
                    manager.setUserName(userDto.getUserName());
                    manager.setPhoneNumber(userDto.getPhoneNumber());
                    manager.setUserImage(userDto.getUserImage());

                    userRepository.save(manager);
                    logger.info("{} ????????? ???????????? ??????", userDto.getUserName());
                    return true;
                } else {
                    logger.info("{} ?????????,????????? ????????????. ???????????? ??????", userDto.getUserName());
                    return false;
                }
            } else {
                logger.info("{} ?????? ???????????? ?????? ?????????. ???????????? ??????", userDto.getUserName());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("{} ???????????? ??????", userDto.getUserName());
            return false;
        }
    }

    @Override
    public Boolean insertUserNoimg(UserDto userDto) {

        Optional<User> findUser = userRepository.findById(userDto.getUserId());

        try {
            if (!findUser.isPresent()) {
                if (userDto.getRole().equals("member")) {
                    Member member = new Member();
                    member.setUserId(userDto.getUserId());
                    member.setPassword(passwordEncoder.encode(userDto.getPassword()));
                    member.setUserName(userDto.getUserName());
                    member.setPhoneNumber(userDto.getPhoneNumber());
                    member.setUserImage("default.png");

                    userRepository.save(member);
                    logger.info("{} ?????? ???????????? ??????", userDto.getUserName());
                    return true;
                } else if (userDto.getRole().equals("manager")) {
                    Manager manager = new Manager();
                    manager.setUserId(userDto.getUserId());
                    manager.setPassword(passwordEncoder.encode(userDto.getPassword()));
                    manager.setUserName(userDto.getUserName());
                    manager.setPhoneNumber(userDto.getPhoneNumber());
                    manager.setUserImage("default.png");

                    userRepository.save(manager);
                    logger.info("{} ????????? ???????????? ??????", userDto.getUserName());
                    return true;
                } else {
                    logger.info("{} ?????????,????????? ????????????. ???????????? ??????", userDto.getUserName());
                    return false;
                }
            } else {
                logger.info("{} ?????? ???????????? ?????? ?????????. ???????????? ??????", userDto.getUserName());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("{} ???????????? ??????", userDto.getUserName());
            return false;
        }
    }

    @Override
    public UserDto getUser(String userid) {
        Optional<User> findUser = userRepository.findById(userid);

        if (findUser.isPresent()) {
            User user = findUser.get();
            UserDto userDto = UserDto.builder().userId(user.getUserId()).password(user.getPassword())
                    .userName(user.getUserName()).phoneNumber(user.getPhoneNumber()).userImage(user.getUserImage())
                    .role(user.getRole()).build();

            logger.info("{} ?????? ?????? ?????? ??????", userid);
            return userDto;
        } else {
            logger.info("????????? ?????? {} ?????? ?????? ??????", userid);
            return null;
        }
    }

    @Override
    public List<UserDto> getUserList() {
        List<User> userList = userRepository.findAllUserByRole("ROLE_MANAGER");
        List<UserDto> uDtoList = userList.stream().map(u -> new UserDto(u.getUserId(), u.getPassword(), u.getUserName(),
                u.getPhoneNumber(), u.getUserImage(), u.getRole())).collect(Collectors.toList());

        logger.info("?????? ?????? ??????");
        return uDtoList;
    }

    @Override
    public Boolean updateUser(UserDto userDto, MultipartFile mfile) {
        Optional<User> findUser = userRepository.findById(userDto.getUserId());

        try {
            if (findUser.isPresent()) {
                String imgname = null;

                try {
                    imgname = String.valueOf(System.currentTimeMillis()) + mfile.getOriginalFilename();
                    mfile.transferTo(
                            new File(System.getProperty("user.dir") + "\\src\\main\\webapp\\userimg" + imgname));

                    String filename = findUser.get().getUserImage();
                    File file = new File(System.getProperty("user.dir") + "\\src\\main\\webapp\\userimg" + filename);

                    if (file.exists() && !filename.equals("default.png")) {
                        if (file.delete()) {
                            logger.info("{} ?????? ?????? ????????? ?????? ??????", userDto.getUserName());
                        } else {
                            logger.info("{} ?????? ?????? ????????? ?????? ??????", userDto.getUserName());
                        }
                    }
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                    logger.error("{} ?????? ????????? ?????? ?????? ??????", userDto.getUserName());
                    return false;
                }
                User user = findUser.get();
                user.setUserId(userDto.getUserName());
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
                user.setUserName(userDto.getUserName());
                user.setPhoneNumber(userDto.getPhoneNumber());
                user.setUserImage(userDto.getUserImage());

                userRepository.save(user);
                logger.info("{} ?????? ?????? ?????? ??????", userDto.getUserName());
                return true;
            } else {
                logger.info("????????? ?????? {} ?????? ?????? ??????", userDto.getUserName());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("????????? ?????? {} ?????? ?????? ??????", userDto.getUserName());
            return false;
        }
    }

    @Override
    public Boolean updateUserNoimg(UserDto userDto) {
        Optional<User> findUser = userRepository.findById(userDto.getUserId());

        try {
            if (findUser.isPresent()) {
                User user = findUser.get();
                user.setUserId(userDto.getUserId());
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
                user.setUserName(userDto.getUserName());
                user.setPhoneNumber(userDto.getPhoneNumber());

                userRepository.save(user);
                logger.info("{} ?????? ?????? ?????? ??????", userDto.getUserName());
                return true;
            } else {
                logger.info("????????? ?????? {} ?????? ?????? ??????", userDto.getUserName());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("?????? {} ?????? ?????? ??????", userDto.getUserName());
            return false;
        }
    }

    @Transactional
    public Boolean deleteUser(String userid) {
        Optional<User> findUser = userRepository.findById(userid);

        try {
            if (findUser.isPresent()) {
                String filename = findUser.get().getUserImage();
                File file = new File(System.getProperty("user.dir") + "\\src\\main\\webapp\\userimg" + filename);

                if (file.exists() && !filename.equals("default.png")) {
                    if (file.delete()) {
                        logger.info("{} ?????? ?????? ????????? ?????? ??????", findUser.get().getUserName());
                    } else {
                        logger.info("{} ?????? ?????? ????????? ?????? ??????", findUser.get().getUserName());
                    }
                }
                userRepository.delete(findUser.get());
                logger.info("{} ?????? ?????? ??????", userid);
                return true;
            } else {
                logger.info("????????? ?????? {} ?????? ??????", userid);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("?????? {} ?????? ??????", userid);
            return false;
        }
    }

}
