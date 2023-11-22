package lk.ijse.dep11.ims.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherTo implements Serializable {
    @Null(message = "Id should be empty")
    private Integer id;

    @NotNull(message = "Name should not be empty")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Invalid name")
    private String name;

    @NotNull(message = "Contact should not be empty")
    @Pattern(regexp = "\\d{3}-\\d{7}$", message = "Invalid contact")
    private String contact;
}
