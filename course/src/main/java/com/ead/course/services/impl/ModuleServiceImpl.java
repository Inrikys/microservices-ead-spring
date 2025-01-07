package com.ead.course.services.impl;

import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.ModuleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;

    public ModuleServiceImpl(ModuleRepository moduleRepository, LessonRepository lessonRepository) {
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    public void delete(ModuleModel moduleModel) {

        List<LessonModel> lessonModelList = lessonRepository.findAllLessonsIntoModule(module.getModuleId());

        if (!lessonModelList.isEmpty()) {
            lessonRepository.deleteAll(lessonModelList);
        }

        moduleRepository.delete(moduleModel);
    }
}
