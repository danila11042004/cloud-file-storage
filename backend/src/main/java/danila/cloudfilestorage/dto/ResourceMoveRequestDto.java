package danila.cloudfilestorage.dto;

import danila.cloudfilestorage.util.RegexPattern;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ResourceMoveRequestDto(
        @Schema(description = "Начальный путь до ресурса,который хотим переименовать/переместить",
                example = "first/file.txt")
        @NotNull(message = "Путь к начальному ресурсу не передан")
        @Pattern(regexp = RegexPattern.REGEX_PATH_TO_RESOURCE,
                message = "Некорректный начальный путь или название ресурса")
        String from,
        @Schema(description = "Конечный путь до ресурса,который хотим переименовать/переместить",
                example = "first/newFile.txt")
        @NotNull(message = "Путь к конечному ресурсу не передан")
        @Pattern(regexp = RegexPattern.REGEX_PATH_TO_RESOURCE,
                message = "Некорректный конечный путь или название ресурса")
        String to) {
}
