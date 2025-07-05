package com.example.nexus.mypage.controller;

import com.example.nexus.mypage.domain.User;
import com.example.nexus.mypage.dto.UserDto;
import com.example.nexus.mypage.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "User API", description = "사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다.")
    @PostMapping
    public ResponseEntity<UserDto.Response> createUser(@RequestBody UserDto.Create request) {
        User user = new User(request.getUsername(), request.getEmail());
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(new UserDto.Response(savedUser));
    }

    @Operation(summary = "모든 사용자 조회", description = "모든 사용자 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<UserDto.Response>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto.Response> responseList = users.stream()
                .map(UserDto.Response::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }
}