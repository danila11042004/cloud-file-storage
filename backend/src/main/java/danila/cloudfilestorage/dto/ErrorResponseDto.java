package danila.cloudfilestorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponseDto(
        @Schema(description = "Сообщение ошибки",
                example = "Ресурс не найден")
        String message) {
}
