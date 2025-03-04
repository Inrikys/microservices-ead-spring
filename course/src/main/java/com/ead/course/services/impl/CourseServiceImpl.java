package com.ead.course.services.impl;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.dtos.CourseRecordDto;
import com.ead.course.exceptions.NotFoundException;
import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.CourseUserRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.CourseService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final CourseUserRepository courseUserRepository;
    private final AuthUserClient authUserClient;

    public CourseServiceImpl(CourseRepository courseRepository, ModuleRepository moduleRepository, LessonRepository lessonRepository, CourseUserRepository courseUserRepository, AuthUserClient authUserClient) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.courseUserRepository = courseUserRepository;
        this.authUserClient = authUserClient;
    }

    @Override
    @Transactional // Garantir a atomicidade (ACID)
    public void delete(CourseModel courseModel) {
        boolean deleteCourseUserInAuthUser = false;
        List<ModuleModel> moduleModelList = moduleRepository.findAllModulesIntoCourse(courseModel.getCourseId());

        if (!moduleModelList.isEmpty()) {
            deleteModules(moduleModelList);
        }

        List<CourseUserModel> courseUserModelList = courseUserRepository.findAllCourseUserIntoCourse(courseModel.getCourseId());

        if (courseUserModelList.isEmpty()) {
            courseUserRepository.deleteAll(courseUserModelList);
            deleteCourseUserInAuthUser = true;
        }

        courseRepository.delete(courseModel);

        if (deleteCourseUserInAuthUser) {
            authUserClient.deleteCourseUserInAuthUser(courseModel.getCourseId());
        }
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
    public Page<CourseModel> findAll(Specification<CourseModel> spec, Pageable pageable) {
        return courseRepository.findAll(spec, pageable);
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
