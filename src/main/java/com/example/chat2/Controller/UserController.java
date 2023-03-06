package com.example.chat2.Controller;

import com.example.chat2.domain.dto.UserDto;
import com.example.chat2.domain.model.User;
import com.example.chat2.security.JwtTokenProvider;
import com.example.chat2.domain.repository.UserRepository;
import com.example.chat2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserService userService;
    // 회원가입
    @PostMapping("/api/signup")
    public Long join(@RequestBody Map<String, String> user) {
        return userRepository.save(User.builder()
                .email(user.get("email"))
                .password(passwordEncoder.encode(user.get("password")))
                .userName(user.get("userName"))
                .roles(Collections.singletonList("ROLE_USER")) // 최초 가입시 USER 로 설정
                .build()).getId();
    }
    @PostMapping("/api/login")
    public UserDto login(@RequestBody Map<String, String> user) {
        User member = userRepository.findByEmail(user.get("email"))
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
        if (!passwordEncoder.matches(user.get("password"), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        String token = jwtTokenProvider.createToken(member.getEmail());
        UserDto User = new UserDto(token, member);
        return User;
    }
    //Request의 Header로 넘어온 token을 쪼개어 유저정보 확인해주는 과정 _ return value: Optional<User>
    @RequestMapping("/api/logincheck")
    public UserDto userInfo(HttpServletRequest httpServletRequest) {
    /*
    HTTP Request의 Header로 넘어온 token을 쪼개어 누구인지 나타내주는 과정
     */
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        String email = jwtTokenProvider.getUserPk(token);
        User member = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 E-MAIL이 없습니다"));
        UserDto User = new UserDto(token,member);
        return User;
    }
    //관심사 설정
    @RequestMapping("/api/interest")
    public UserDto selectInterest(@RequestBody Map<String, List> userInterested, HttpServletRequest httpServletRequest) {
        //토근에서 사용자 정보 추출
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        String email = jwtTokenProvider.getUserPk(token);
        User member = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 E-MAIL이 없습니다"));
        //해당 사용자의 관심사 업데이트
        List<String> interested = userInterested.get("userInterested");
        UserDto User = new UserDto(member,interested);
        userService.update(User);
        return User;
    }

    //프로필 수정
    @RequestMapping("/api/profile")
    public UserDto profileChange(@RequestBody String userJson, HttpServletRequest httpServletRequest) {
        //토근에서 사용자 정보 추출
        JSONObject ujson = new org.json.JSONObject(userJson);
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        String email = jwtTokenProvider.getUserPk(token);
        User member = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 E-MAIL이 없습니다"));
        //해당 사용자의 프로필 업데이트
        UserDto User = new UserDto(member,ujson);
        userService.update(User);
        return User;
    }

    @RequestMapping("/api/userprofile")
    public UserDto getProfile(HttpServletRequest httpServletRequest){
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        String email = jwtTokenProvider.getUserPk(token);
        User member = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 E-MAIL이 없습니다"));
        UserDto User = new UserDto(token,member);
        return User;
    }
}