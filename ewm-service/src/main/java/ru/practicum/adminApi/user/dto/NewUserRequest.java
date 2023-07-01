package ru.practicum.adminApi.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class NewUserRequest {
    @Email(message = "Email isn't valid")
    @NotBlank(message = "Email can't be blank")
    @Length(min = 6, max = 254, message = "Email length must be min=6 and max=254")
    private String email;

    @NotBlank(message = "Name can't be blank")
    @Length(min = 2, max = 250, message = "Name length must be min=2 and max=250")
    private String name;
}
