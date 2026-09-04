package danila.cloudfilestorage.controller;

import danila.cloudfilestorage.api.service.AuthenticationService;
import danila.cloudfilestorage.dto.AuthRequestDto;
import danila.cloudfilestorage.dto.AuthResponseDto;
import danila.cloudfilestorage.seсurity.SecurityContextManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Аутентификация", description = "Работа с аутентификацией пользователя")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;
    private final SecurityContextManager securityContextManager;

    @Operation(summary = "Регистрация",
            description = "Создает нового пользователя и проводит аутентификацию," +
                    "что дает возможность пользоватся защищенными эндпоинтами")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка валидации учетных данных"),
            @ApiResponse(responseCode = "409",
                    description = "Пользователь с таким именем уже существует"),
            @ApiResponse(responseCode = "500",
                    description = "Неизвестная ошибка")
    })
    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDto signUp(@Valid @RequestBody AuthRequestDto requestDto) {
        return authenticationService.signUp(requestDto.username(), requestDto.password());

    }

    @Operation(summary = "Вход в учетную запись",
            description = "Выполняет вход по учетным данным и проводит аутентификацию," +
                    "что дает возможность пользоватся защищенными эндпоинтами")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Пользователь успешно вошел"),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка валидации учетных данных"),
            @ApiResponse(responseCode = "401",
                    description = "Неверные учетные данные"),
            @ApiResponse(responseCode = "500",
                    description = "Неизвестная ошибка")
    })
    @PostMapping("/sign-in")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponseDto signIn(@Valid @RequestBody AuthRequestDto requestDto) {
        return authenticationService.signIn(requestDto.username(), requestDto.password());
    }

    @Operation(summary = "Выход из учетной записи",
            description = "Выполняет выход из учетной записи" +
                    "что запрещает пользоватся защищенными эндпоинтами")
    @ApiResponses({
            @ApiResponse(responseCode = "204",
                    description = "Пользователь успешно вышел"),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "500",
                    description = "Неизвестная ошибка")
    })
    @PostMapping("/sign-out")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void signOut(HttpServletRequest req, HttpServletResponse resp) {
        securityContextManager.logout(req, resp);
    }

}
