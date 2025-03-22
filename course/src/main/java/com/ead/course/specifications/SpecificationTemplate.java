package com.ead.course.specifications;

import com.ead.course.models.CourseModel;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.models.UserModel;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.UUID;

public class SpecificationTemplate {

    @And({
            @Spec(path = "courseStatus", spec = Equal.class),
            @Spec(path = "courseLevel", spec = Equal.class),
            @Spec(path = "name", spec = LikeIgnoreCase.class),
            @Spec(path = "userInstructor", spec = Equal.class),
    })
    public interface CourseSpec extends Specification<CourseModel> {
    }

    @Spec(path = "title", spec = LikeIgnoreCase.class)
    public interface ModuleSpec extends Specification<ModuleModel> {
    }

    @Spec(path = "title", spec = LikeIgnoreCase.class)
    public interface LessonSpec extends Specification<LessonModel> {
    }

    @And({
            @Spec(path = "email", spec = Like.class),
            @Spec(path = "fullName", spec = LikeIgnoreCase.class),
            @Spec(path = "userStatus", spec = Equal.class),
            @Spec(path = "userType", spec = Equal.class)
    })
    public interface UserSpec extends Specification<UserModel> {
    }

    public static Specification<ModuleModel> moduleCourseId(final UUID courseId) {
        return (root, query, cb) -> {
            query.distinct(true);
            Root<ModuleModel> module = root; // ENTITY A
            Root<CourseModel> course = query.from(CourseModel.class); // ENTITY B
            Expression<Collection<ModuleModel>> courseModules = course.get("modules"); // COLETANDO MODULOS DE DENTRO DO CURSO (ENTITY A em ENTITY B)
            return cb.and(cb.equal(course.get("courseId"), courseId), cb.isMember(module, courseModules)); // CriteriaBuilder usando AND -> courseId igual ao recebido E module membro de courseModules
        };
    }

    public static Specification<LessonModel> lessonModuleId(final UUID moduleId) {
        return (root, query, cb) -> {
            query.distinct(true);
            Root<LessonModel> lesson = root; // ENTITY A
            Root<ModuleModel> module = query.from(ModuleModel.class); // ENTITY B
            Expression<Collection<LessonModel>> moduleLessons = module.get("lessons"); // COLETANDO Lessons DE DENTRO DO Module (ENTITY A em ENTITY B)
            return cb.and(cb.equal(module.get("moduleId"), moduleId), cb.isMember(lesson, moduleLessons)); // CriteriaBuilder usando AND -> moduleId igual ao recebido E lesson membro de moduleLessons
        };
    }

    public static Specification<CourseModel> courseUserId(final UUID userId) {
        return (root, query, cb) -> {
            query.distinct(true);
            Root<CourseModel> course = root;
            Root<UserModel> user = query.from(UserModel.class);
            Expression<Collection<CourseModel>> userCourses = user.get("courses");
            return cb.and(cb.equal(course.get("userId"), userId), cb.isMember(course, userCourses));
        };
    }

    public static Specification<UserModel> userCourseId(final UUID courseId) {
        return (root, query, cb) -> {
            query.distinct(true);
            Root<UserModel> user = root;
            Root<CourseModel> course = query.from(CourseModel.class);
            Expression<Collection<UserModel>> coursesUsers = course.get("users");
            return cb.and(cb.equal(course.get("courseId"), courseId), cb.isMember(user, coursesUsers));
        };
    }

}
