package danila.cloudfilestorage.dto;

import danila.cloudfilestorage.util.RegexPattern;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResourceSearchRequestDto(
        @Schema(description = "Запрос для поиска ресурсов",
                example = "f")
        @NotNull(message = "Запрос не передан")
        @Pattern(regexp = RegexPattern.REGEX_SEARCH,
                message = "Некорректные символы запроса")
        @Size(min = 1,max = 30, message = "Размер запроса должен быть от 1 до 30 символов")
        String query) {
}
