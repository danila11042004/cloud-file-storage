package danila.cloudfilestorage.dto;

import danila.cloudfilestorage.util.RegexPattern;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthRequestDto(
        @Schema(description = "Имя пользователя",
                example = "danila1")
        @Pattern(regexp = RegexPattern.REGEX_USERNAME,
                message = "Логин не корректен,разрешены только буквы,цифры,тире и подчеркивания")
        @NotNull(message = "Логин не передан")
        @Size(min = 5, max = 15, message = "Длина логина должна быть от 5 до 15 символов")
        String username,
        @Schema(description = "Пароль пользователя",
                example = "asdfg123")
        @NotNull(message = "Пароль не передан")
        @NotEmpty(message = "Пароль не должен быть пустым")
        @Size(min = 5, max = 15, message = "Длина пароля должна быть от 5 до 15 символов")
        String password
) {
}
