package danila.cloudfilestorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CurrentUserResponseDto(
        @Schema(description = "Имя пользователя",
                example = "danila1")
        String username) {
}
