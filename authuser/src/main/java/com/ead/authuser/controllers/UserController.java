package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserRecordDto;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
//@CrossOrigin(origins = "*", maxAge = 3600) // config global em ResolverConfig
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<UserModel>> getAllUsers(
           //@PageableDefault(page = 0, size = 3, sort = "userId", direction = Sort.Direction.ASC) // config global em ResolverConfig
            Pageable pageable) {
        Page<UserModel> userModelPage = userService.findAll(pageable);
        return ResponseEntity.ok(userModelPage);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getOneUser(@PathVariable(value = "userId") UUID userId) {
        return ResponseEntity.ok(userService.findById(userId).get());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deteleUser(@PathVariable(value = "userId") UUID userId) {
        userService.delete(userService.findById(userId).get());
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable(value = "userId") UUID userId,
                                             @RequestBody @Validated(UserRecordDto.UserView.UserPut.class)
                                             @JsonView(UserRecordDto.UserView.UserPut.class)
                                             UserRecordDto userRecordDto) {
        return ResponseEntity.ok(userService.updateUser(userRecordDto, userService.findById(userId).get()));
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable(value = "userId") UUID userId,
                                                 @RequestBody @Validated(UserRecordDto.UserView.PasswordPut.class)
                                                 @JsonView(UserRecordDto.UserView.PasswordPut.class)
                                                 UserRecordDto userRecordDto) {

        UserModel userModel = userService.findById(userId).get();

        if (!userModel.getPassword().equals(userRecordDto.oldPassword())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Mismatch old password!");
        }

        userService.updatePassword(userRecordDto, userModel);

        return ResponseEntity.ok("Password updated successfully!");
    }

    @PutMapping("/{userId}/image")
    public ResponseEntity<Object> updateImage(@PathVariable(value = "userId") UUID userId,
                                              @RequestBody @Validated(UserRecordDto.UserView.ImagePut.class)
                                              @JsonView(UserRecordDto.UserView.ImagePut.class)
                                              UserRecordDto userRecordDto) {

        return ResponseEntity.ok(userService.updateImage(userRecordDto, userService.findById(userId).get()));
    }

}
