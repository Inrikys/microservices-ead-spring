package com.ead.authuser.controllers;

import com.ead.authuser.clients.CourseClient;
import com.ead.authuser.dtos.CourseRecordDto;
import com.ead.authuser.dtos.UserCourseRecordDto;
import com.ead.authuser.models.UserCourseModel;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserCourseService;
import com.ead.authuser.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
public class UserCourseController {

    private final CourseClient courseClient;
    private final UserService userService;
    private final UserCourseService userCourseService;

    public UserCourseController(CourseClient courseClient, UserService userService, UserCourseService userCourseService) {
        this.courseClient = courseClient;
        this.userService = userService;
        this.userCourseService = userCourseService;
    }

    @GetMapping("/users/{userId}/courses")
    public ResponseEntity<Page<CourseRecordDto>> getAllCoursesByUser(
            @PageableDefault(sort = "courseId", direction = Sort.Direction.ASC) Pageable pageable,
            @PathVariable(value = "userId") UUID userId) {

        return ResponseEntity.ok(courseClient.getAllCoursesByUser(userId, pageable));
    }

    @PostMapping("/users/{userId}/courses/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(
            @PathVariable(value = "userId") UUID userId,
            @RequestBody @Valid UserCourseRecordDto userCourseRecordDto) {

        Optional<UserModel> userModelOptional = userService.findById(userId);

        if (userCourseService.existsByUserAndCourseId(userModelOptional.get(), userCourseRecordDto.courseId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Subscription already exists!");
        }

        UserCourseModel userCourseModel = userCourseService
                .save(userModelOptional.get().convertToUserCourseModel(userCourseRecordDto.courseId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(userCourseModel);
    }

    @DeleteMapping("/users/courses/{courseId}")
    public ResponseEntity<Object> deleteUserCourseByCourse(
            @PathVariable(value = "courseId") UUID courseId) {

        if (userCourseService.existsByCourseId(courseId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UserCourse not found.");
        }

        userCourseService.deleteAllByCourseId(courseId);
        return ResponseEntity.ok("UserCourse deleted successfully.");
    }

}
