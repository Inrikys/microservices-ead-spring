package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserRecordDto;
import com.ead.authuser.exceptions.GlobalExceptionHandler;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.ead.authuser.specifications.SpecificationTemplate;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
//@CrossOrigin(origins = "*", maxAge = 3600) // config global em ResolverConfig
public class UserController {

    Logger logger = LogManager.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<UserModel>> getAllUsers(
            //@PageableDefault(page = 0, size = 3, sort = "userId", direction = Sort.Direction.ASC) // config global em ResolverConfig
            SpecificationTemplate.UserSpec spec,
            Pageable pageable,
            @RequestParam(required = false) UUID courseId) {

        logger.debug("GET getAllUsers");

        Page<UserModel> userModelPage = courseId != null
                ? userService.findAll(SpecificationTemplate.userCourseId(courseId).and(spec), pageable)
                : userService.findAll(spec, pageable);

        // HATEOAS
        if (!userModelPage.isEmpty()) {
            for (UserModel user : userModelPage.toList()) {
                user.add(linkTo(methodOn(UserController.class).getOneUser(user.getUserId())).withSelfRel());
            }
        }

        return ResponseEntity.ok(userModelPage);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getOneUser(@PathVariable(value = "userId") UUID userId) {
        return ResponseEntity.ok(userService.findById(userId).get());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "userId") UUID userId) {
        logger.debug("DELETE deleteUser userId received {}", userId);
        userService.delete(userService.findById(userId).get());
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable(value = "userId") UUID userId,
                                             @RequestBody @Validated(UserRecordDto.UserView.UserPut.class)
                                             @JsonView(UserRecordDto.UserView.UserPut.class)
                                             UserRecordDto userRecordDto) {
        logger.debug("PUT updateUser userRecordDto received {}", userRecordDto);
        return ResponseEntity.ok(userService.updateUser(userRecordDto, userService.findById(userId).get()));
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable(value = "userId") UUID userId,
                                                 @RequestBody @Validated(UserRecordDto.UserView.PasswordPut.class)
                                                 @JsonView(UserRecordDto.UserView.PasswordPut.class)
                                                 UserRecordDto userRecordDto) {

        logger.debug("PUT updatePassword userId received {}", userId);
        UserModel userModel = userService.findById(userId).get();

        if (!userModel.getPassword().equals(userRecordDto.oldPassword())) {
            logger.warn("Mismatch old password! userId {}", userId);
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
        logger.debug("PUT updateImage userRecordDto received {}", userRecordDto);
        return ResponseEntity.ok(userService.updateImage(userRecordDto, userService.findById(userId).get()));
    }

}
