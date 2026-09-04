package danila.cloudfilestorage.dto;

import danila.cloudfilestorage.util.RegexPattern;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DirectoryContentRequestDto(
        @Schema(description = "Путь до папки,ресурсы которой хотим получить",
                example = "first/second/")
        @NotNull(message = "Путь к папке не передан")
        @Pattern(regexp = RegexPattern.REGEX_ZERO_OR_MORE_DIRECTORIES,
                message = "Некорректный путь или название папки")
        String path) {
}
