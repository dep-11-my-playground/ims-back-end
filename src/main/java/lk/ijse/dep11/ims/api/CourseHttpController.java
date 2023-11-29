package lk.ijse.dep11.ims.api;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lk.ijse.dep11.ims.to.CourseTO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PreDestroy;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/courses")
@CrossOrigin
public class CourseHttpController {
    private final HikariDataSource pool;


    public CourseHttpController() {
        HikariConfig config = new HikariConfig();
        config.setUsername("root");
        config.setPassword("mysql");
        config.setJdbcUrl("jdbc:mysql://localhost:3306/dep11_ims");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.addDataSourceProperty("maximumPoolSize", 10);
        pool = new HikariDataSource(config);


    }

    @PreDestroy
    public void destroy(){pool.close();}

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "application/json", produces = "application/json")
    public CourseTO createCourse(@RequestBody @Validated CourseTO course){
        try ( Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("INSERT INTO course(name, duration_in_months) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, course.getCourseName());
            stm.setInt(2, course.getDurationInMonths());
            stm.executeUpdate();
            ResultSet rst = stm.getGeneratedKeys();
            rst.next();
            int courseId = rst.getInt(1);
            course.setCourseId(courseId);
            return course;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(value = "/{courseId}", consumes = "application/json")
    public void updateCourse(@PathVariable int courseId,
                             @RequestBody @Validated CourseTO course){
        try(Connection connection = pool.getConnection()){
            PreparedStatement stmExist = connection.prepareStatement("SELECT * FROM course WHERE id=?");
            stmExist.setInt(1, courseId);
            if (!stmExist.executeQuery().next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found!");
            }
            PreparedStatement stm = connection.prepareStatement("UPDATE course SET name=?, duration_in_months=? WHERE id=?");
            stm.setString(1, course.getCourseName());
            stm.setInt(2, course.getDurationInMonths());
            stm.setInt(3, courseId);
            stm.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{courseId}")
    public void deleteCourse(@PathVariable int courseId){
        try (Connection connection = pool.getConnection()){
            PreparedStatement stmExist = connection.prepareStatement("SELECT * FROM course WHERE id=?");
            stmExist.setInt(1, courseId);
            if (!stmExist.executeQuery().next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found!");
            }

            PreparedStatement stmDelete = connection.prepareStatement("DELETE FROM course WHERE id=?");
            stmDelete.setInt(1,courseId);
            stmDelete.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @GetMapping(value = "/{courseId}", produces = "application/json")
    public CourseTO getCourse(@PathVariable int courseId){
        try (Connection connection = pool.getConnection()){
            PreparedStatement stmExist = connection.prepareStatement("SELECT * FROM course WHERE id=?");
            stmExist.setInt(1, courseId);
            ;
            if (!stmExist.executeQuery().next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found!");
            }

            PreparedStatement stm = connection.prepareStatement("SELECT * FROM course WHERE id=?");
            stm.setInt(1, courseId);
            ResultSet rst = stm.executeQuery();

            String courseName = "";
            int courseDuration = 0;
            while (rst.next()){
                courseName = rst.getString("name");
                courseDuration = rst.getInt("duration_in_months");

            }
            return new CourseTO(courseId, courseName, courseDuration);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @GetMapping(produces = "application/json")
    public List<CourseTO> getAllCourses(){
        try (Connection connection = pool.getConnection()){
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM course");
            List<CourseTO> courseList = new LinkedList<>();
            while (rst.next()){
                int courseId = rst.getInt("id");
                String courseName = rst.getString("name");
                int courseDuration = rst.getInt("duration_in_months");
                courseList.add(new CourseTO(courseId,courseName,courseDuration));

            }
            return courseList;


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

}
