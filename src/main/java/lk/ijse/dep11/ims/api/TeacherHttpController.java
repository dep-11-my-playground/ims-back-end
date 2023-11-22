package lk.ijse.dep11.ims.api;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lk.ijse.dep11.ims.To.TeacherTo;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PreDestroy;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/teachers")
@CrossOrigin
public class TeacherHttpController {
    private final HikariDataSource pool;
    public TeacherHttpController(){
        HikariConfig config = new HikariConfig();
        config.setUsername("root");
        config.setPassword("mysql");
        config.setJdbcUrl("jdbc:mysql://localhost:3306/dep11_ims");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.addDataSourceProperty("maximumPoolSize", 10);
        pool = new HikariDataSource(config);
    }

    @PreDestroy
    public void destroy(){
        pool.close();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "application/json", produces = "application/json")
    public TeacherTo createTeacher(@RequestBody @Validated TeacherTo teacher){
        System.out.println("createTeacher()");

        try (Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection
                    .prepareStatement("INSERT INTO teacher(name, contact) VALUES (?, ?)"
                    , Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, teacher.getName());
            stm.setString(2, teacher.getContact());
            stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            generatedKeys.next();
            int id = generatedKeys.getInt(1);
            teacher.setId(id);
            return teacher;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(consumes = "application/json", value = "/{teacherId}")
    public void updateTeacher(@PathVariable int teacherId, @RequestBody @Validated TeacherTo teacher){
        try (Connection connection = pool.getConnection()) {

            PreparedStatement stmExists = connection.prepareStatement("SELECT * FROM teacher WHERE id =?");
            stmExists.setInt(1, teacherId);
            if (!stmExists.executeQuery().next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found!");
            }

            PreparedStatement stm = connection
                    .prepareStatement("UPDATE teacher SET name = ?, contact = ? WHERE id = ?");
            stm.setString(1, teacher.getName());
            stm.setString(2, teacher.getContact());
            stm.setInt(3, teacherId);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{teacherId}")
    public void deleteTeacher(@PathVariable int teacherId){
        try (Connection connection = pool.getConnection()) {

            PreparedStatement stmExists = connection.prepareStatement("SELECT * FROM teacher WHERE id =?");
            stmExists.setInt(1, teacherId);
            if (!stmExists.executeQuery().next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found!");
            }

            PreparedStatement stm = connection.prepareStatement("DELETE FROM teacher WHERE id = ?");
            stm.setInt(1, teacherId);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping(value = "/{teacherId}", produces = "application/json")
    public TeacherTo getTeacher(@PathVariable int teacherId){

        try (Connection connection = pool.getConnection()) {

            PreparedStatement stmExists = connection.prepareStatement("SELECT * FROM teacher WHERE id =?");
            stmExists.setInt(1, teacherId);
            if (!stmExists.executeQuery().next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found!");
            }

            PreparedStatement stm = connection.prepareStatement("SELECT * FROM teacher WHERE id = ?");
            stm.setInt(1, teacherId);
            ResultSet rst = stm.executeQuery();
            String name="";
            String contact = "";
            while (rst.next()){
                name = rst.getString("name");
                contact = rst.getString("contact");
            }
            return new TeacherTo(teacherId, name,contact);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(produces = "application/json")
    public List<TeacherTo> getAllTeachers(){
        try (Connection connection = pool.getConnection()) {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM teacher");
            List<TeacherTo> teacherList = new LinkedList<>();
            while (rst.next()){
                int id = rst.getInt("id");
                String name = rst.getString("name");
                String contact = rst.getString("contact");
                teacherList.add(new TeacherTo(id, name, contact));
            }
            return teacherList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
