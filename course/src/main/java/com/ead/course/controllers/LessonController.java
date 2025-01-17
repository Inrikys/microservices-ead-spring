package com.ead.course.controllers;

import com.ead.course.dtos.LessonRecordDto;
import com.ead.course.models.LessonModel;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class LessonController {

    Logger logger = LogManager.getLogger(LessonController.class);

    private final ModuleService moduleService;
    private final LessonService lessonService;

    public LessonController(ModuleService moduleService, LessonService lessonService) {
        this.moduleService = moduleService;
        this.lessonService = lessonService;
    }

    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Object> saveLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                             @RequestBody @Valid LessonRecordDto lessonRecordDto) {

        logger.debug("POST saveLesson lessonRecordDto {}", lessonRecordDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.save(lessonRecordDto, moduleService.findById(moduleId).get()));
    }

    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Page<LessonModel>> getAllLessons(@PathVariable(value = "moduleId") UUID moduleId,
                                                           SpecificationTemplate.LessonSpec spec,
                                                           Pageable pageable) {
        return ResponseEntity.ok(lessonService.findAllLessonsIntoModule(SpecificationTemplate.lessonModuleId(moduleId).and(spec), pageable));
    }

    @GetMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> getOneLessons(
            @PathVariable(value = "moduleId") UUID moduleId,
            @PathVariable(value = "lessonId") UUID lessonId
    ) {
        return ResponseEntity.ok(lessonService.findLessonIntoModule(moduleId, lessonId).get());
    }

    @DeleteMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> deleteLesson(
            @PathVariable(value = "moduleId") UUID moduleId,
            @PathVariable(value = "lessonId") UUID lessonId) {

        logger.debug("DELETE deleteLesson moduleId {} lessonId {}", moduleId, lessonId);
        lessonService.delete(lessonService.findLessonIntoModule(moduleId, lessonId).get());
        return ResponseEntity.ok("Lesson deleted successfully.");
    }

    @PutMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> updateLesson(
            @PathVariable(value = "moduleId") UUID moduleId,
            @PathVariable(value = "lessonId") UUID lessonId,
            @RequestBody @Valid LessonRecordDto lessonRecordDto) {

        logger.debug("PUT updateLesson moduleId {} lessonId {}, lessonRecordDto", moduleId, lessonId, lessonRecordDto);
        return ResponseEntity.ok(lessonService.update(lessonRecordDto, lessonService.findLessonIntoModule(moduleId, lessonId).get()));
    }
}
