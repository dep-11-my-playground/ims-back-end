package lk.ijse.dep11.ims.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.groups.Default;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CourseTO implements Serializable {

    @Null(message = "Course Id should be empty")
    private Integer courseId;

    @NotNull(message = "Course Name Should not be empty")
    private String courseName;

    @NotNull(message = "Course duration should not be empty")
    private Integer durationInMonths;



}
