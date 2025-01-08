package com.ead.course.repositories;

import com.ead.course.models.ModuleModel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ModuleRepository extends JpaRepository<ModuleModel, UUID> {

    // Mapeamento para mudar de FetchType.LAZY para EAGER
//    @EntityGraph(attributePaths = {"course"})
//    ModuleModel findByTitle(String title);

    //@Modifying -> Usada quando precisa customizar query usando @Query, mas
    // será feita alguma alteração no banco (DELETE, UPDATE)

    @Query(value = "select * from tb_modules where course_course_id = :courseId", nativeQuery = true)
    List<ModuleModel> findAllModulesIntoCourse(@Param("courseId") UUID courseId);
}