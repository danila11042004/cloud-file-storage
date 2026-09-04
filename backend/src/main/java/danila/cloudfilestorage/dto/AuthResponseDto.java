package danila.cloudfilestorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponseDto(
        @Schema(description = "Имя пользователя",
                example = "danila1")
        String username) {
}
