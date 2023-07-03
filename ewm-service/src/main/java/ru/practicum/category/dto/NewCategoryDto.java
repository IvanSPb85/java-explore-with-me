package ru.practicum.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NewCategoryDto {
    @NotBlank(message = "Name must not be blank")
    @Length(min = 1, max = 50, message = "Name length must be min=1 and max=50")
    private String name;
}
