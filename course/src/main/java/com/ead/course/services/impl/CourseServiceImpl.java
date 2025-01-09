package com.ead.course.services.impl;

import com.ead.course.dtos.CourseRecordDto;
import com.ead.course.exceptions.NotFoundException;
import com.ead.course.models.CourseModel;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.CourseService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;

    public CourseServiceImpl(CourseRepository courseRepository, ModuleRepository moduleRepository, LessonRepository lessonRepository) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    @Transactional // Garantir a atomicidade (ACID)
    public void delete(CourseModel courseModel) {

        List<ModuleModel> moduleModelList = moduleRepository.findAllModulesIntoCourse(courseModel.getCourseId());

        if (!moduleModelList.isEmpty()) {
            deleteModules(moduleModelList);
        }

        courseRepository.delete(courseModel);
    }

    @Override
    public CourseModel save(CourseRecordDto courseRecordDto) {
        var courseModel = new CourseModel();
        BeanUtils.copyProperties(courseRecordDto, courseModel);

        return courseRepository.save(courseModel);
    }

    @Override
    public boolean existsByName(String name) {
        return courseRepository.existsByName(name);
    }

    @Override
    public List<CourseModel> findAll() {
        return courseRepository.findAll();
    }

    @Override
    public Optional<CourseModel> findById(UUID courseId) {

        Optional<CourseModel> optionalCourseModel = courseRepository.findById(courseId);

        if (optionalCourseModel.isEmpty()) {
            throw new NotFoundException("Error: Course not found.");
        }

        return optionalCourseModel;
    }

    @Override
    public CourseModel update(CourseRecordDto courseRecordDto, CourseModel courseModel) {
        BeanUtils.copyProperties(courseRecordDto, courseModel);
        return courseRepository.save(courseModel);
    }

    private void deleteModules(List<ModuleModel> moduleModelList) {
        for (ModuleModel module : moduleModelList) {
            List<LessonModel> lessonModelList = lessonRepository.findAllLessonsIntoModule(module.getModuleId());

            if (!lessonModelList.isEmpty()) {
                deleteLessons(lessonModelList);
            }
        }

        moduleRepository.deleteAll(moduleModelList);
    }

    private void deleteLessons(List<LessonModel> lessonModelList) {
        lessonRepository.deleteAll(lessonModelList);
    }
}
