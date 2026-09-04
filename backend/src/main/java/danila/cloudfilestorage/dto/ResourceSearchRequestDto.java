package danila.cloudfilestorage.dto;

import danila.cloudfilestorage.util.RegexPattern;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ResourceSearchRequestDto(
        @Schema(description = "Запрос для поиска ресурсов",
                example = "f")
        @NotNull(message = "Запрос не передан")
        @Pattern(regexp = RegexPattern.REGEX_ALL_VALID_CHARACTERS_EXCEPT_SLASH,
                message = "Некорректный символы запроса")
        String query) {
}
