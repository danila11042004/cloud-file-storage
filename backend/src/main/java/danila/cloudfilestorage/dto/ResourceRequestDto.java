package danila.cloudfilestorage.dto;

import danila.cloudfilestorage.util.RegexPattern;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ResourceRequestDto(
        @Schema(description = "Путь до ресурса,с которым хотим выполнить операцию",
                example = "first/second/")
        @NotNull(message = "Путь к ресурсу не передан")
        @Pattern(regexp = RegexPattern.REGEX_PATH_TO_RESOURCE,
                message = "Некорректный путь или название ресурса")
        String path) {
}
