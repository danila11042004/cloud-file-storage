package danila.cloudfilestorage.controller;

import danila.cloudfilestorage.dto.DirectoryContentRequestDto;
import danila.cloudfilestorage.dto.DirectoryCreateRequestDto;
import danila.cloudfilestorage.dto.ResourceResponseDto;
import danila.cloudfilestorage.service.resource.ResourceFacadeService;
import danila.cloudfilestorage.seсurity.AuthenticatedUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Ресурсы", description = "Работа с ресурсами текущего пользователя")
@RestController
@RequestMapping("api/directory")
@RequiredArgsConstructor
public class DirectoryController {
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ResourceFacadeService resourceFacadeService;

    @Operation(summary = "Создание папки")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Папка успешно создана"),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка валидации пути к новой папке или его отсутствие"),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404",
                    description = "Родительская папка новой папки не существует"),
            @ApiResponse(responseCode = "409",
                    description = "Папка с таким же именем существует в родителской папке"),
            @ApiResponse(responseCode = "500",
                    description = "Ошибка при обращении к БД,Неизвестная ошибка")
    })
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceResponseDto createDirectory(
            @ParameterObject @Valid @ModelAttribute DirectoryCreateRequestDto requestDto) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();
        return resourceFacadeService.createDirectory(userId, requestDto.path());
    }

    @Operation(summary = "Получение содержимого папки",
            description = "Получает все содержимое папки,но не рекурсивно")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Содержимое папки успешно получено"),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка валидации пути к выбранной папке или его отсутствие"),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404",
                    description = "Выбранная папка не существует"),
            @ApiResponse(responseCode = "500",
                    description = "Ошибка при обращении к БД,Неизвестная ошибка")
    })
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<ResourceResponseDto> getDirectoryContent(
            @ParameterObject @Valid @ModelAttribute
            DirectoryContentRequestDto requestDto) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();
        return resourceFacadeService.getDirectoryContent(userId, requestDto.path());
    }

}
