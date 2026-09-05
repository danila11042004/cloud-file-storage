package danila.cloudfilestorage.dto;

import danila.cloudfilestorage.util.RegexPattern;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DirectoryCreateRequestDto(
        @Schema(description = "Путь до папки,которую хотим создать",
                example = "first/second/")
        @NotNull(message = "Путь к новой папке не передан")
        @Pattern(regexp = RegexPattern.REGEX_ONE_OR_MORE_DIRECTORIES,
                message = "Некорректный путь или название папки")
        @Size(max = 250, message = "Длина пути до директории должна быть до 250 символов")
        String path) {
}
