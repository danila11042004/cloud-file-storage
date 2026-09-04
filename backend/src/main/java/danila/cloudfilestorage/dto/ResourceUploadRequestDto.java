package danila.cloudfilestorage.dto;

import danila.cloudfilestorage.util.RegexPattern;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ResourceUploadRequestDto(
        @Schema(description = "Путь до родительской папки ресурса",
                example = "first/")
        @Pattern(regexp = RegexPattern.REGEX_ZERO_OR_MORE_DIRECTORIES,
                message = "Некорректный путь или название папки для загрузки")
        String path,
        @Schema(description = "Список загружаемых файлов")
        @NotNull(message = "Файлы не выбраны")
        @NotEmpty(message = "Файлы не выбраны")
        List<MultipartFile> object) {

    public ResourceUploadRequestDto {
        if (path == null || path.isBlank()) {
            path = "";
        }
    }
}
