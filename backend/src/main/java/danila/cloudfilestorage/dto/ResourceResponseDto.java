package danila.cloudfilestorage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import danila.cloudfilestorage.enums.ResourceType;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResourceResponseDto(
        @Schema(description = "Путь до родительской папки ресурса",
                example = "first/")
        String path,
        @Schema(description = "Имя ресурса",
                example = "file.txt")
        String name,
        @Schema(description = "Размер ресурса,если это папка,поле отсутсвует",
                example = "9 MB")
        Long size,
        @Schema(description = "Тип ресурса",
                example = "FILE")
        ResourceType type) {
}
