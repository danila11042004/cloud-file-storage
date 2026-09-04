package danila.cloudfilestorage.controller;

import danila.cloudfilestorage.dto.CurrentUserResponseDto;
import danila.cloudfilestorage.seсurity.AuthenticatedUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Аутентификация", description = "Работа с аутентификацией пользователя")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class CurrentUserController {
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Operation(summary = "Получение имени текущего пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Текущий пользователь получен"),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "500",
                    description = "Неизвестная ошибка")
    })
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public CurrentUserResponseDto getMe() {
        return new CurrentUserResponseDto(authenticatedUserProvider.getAuthenticatedUserName());
    }
}
