package danila.cloudfilestorage.controller;

import danila.cloudfilestorage.dto.*;
import danila.cloudfilestorage.model.DownloadResource;
import danila.cloudfilestorage.service.resource.ResourceFacadeService;
import danila.cloudfilestorage.seсurity.AuthenticatedUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@Tag(name = "Ресурсы", description = "Работа с ресурсами текущего пользователя")
@RestController
@RequestMapping("api/resource")
@RequiredArgsConstructor
public class ResourceController {
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ResourceFacadeService resourceFacadeService;

    @Operation(summary = "Аплоад ресурса",
            description = "Аплоадит ресурсы со всеми лежащими в них ресурсами")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Успешный аплоад ресурсов"),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка валидации пути к родительской папке загружаемых ресурсов," +
                            "Ошибка валидации тела запроса с ресурсами"),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404",
                    description = "Родительская папка загружаемых ресурсов не существует"),
            @ApiResponse(responseCode = "409",
                    description = "Ресурс с таким же именем существует в родительской папке"),
            @ApiResponse(responseCode = "500",
                    description = "Ошибка при чтении потока,Ошибка при обращении к БД,Неизвестная ошибка")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public List<ResourceResponseDto> uploadResource(
            @ParameterObject @Valid @ModelAttribute
            ResourceUploadRequestDto requestDto) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();
        return resourceFacadeService.upload(userId, requestDto.object(), requestDto.path());
    }

    @Operation(summary = "Получение информации о ресурсе")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Успешное получение информации о ресурсе"),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка валидации пути к ресурсу"),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404",
                    description = "Выбранный ресурс не существует"),
            @ApiResponse(responseCode = "500",
                    description = "Ошибка при обращении к БД,Неизвестная ошибка")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResourceResponseDto getResource(
            @ParameterObject @Valid @ModelAttribute
            ResourceRequestDto requestDto) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();
        return resourceFacadeService.getInfo(userId, requestDto.path());
    }

    @Operation(summary = "Удаление ресурса",
            description = "Удаление ресурса со всеми лежащими в нем ресурсами")
    @ApiResponses({
            @ApiResponse(responseCode = "204",
                    description = "Успешное удаление ресурса"),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка валидации пути к ресурсу"),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404",
                    description = "Выбранный ресурс не существует"),
            @ApiResponse(responseCode = "500",
                    description = "Ошибка при обращении к БД,Неизвестная ошибка")
    })
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResource(
            @ParameterObject @Valid @ModelAttribute
            ResourceRequestDto requestDto) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();
        resourceFacadeService.delete(userId, requestDto.path());
    }

    @Operation(summary = "Скачивание ресурса",
            description = "Скачивание ресурса.Если это папка,скачивается zip,со всеми вложенными ресурсами")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Успешное скачивание ресурса"),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка валидации пути к ресурсу"),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404",
                    description = "Выбранный ресурс не существует"),
            @ApiResponse(responseCode = "500",
                    description = "Ошибка при обработке потока с данными,Ошибка при обращении к БД,Неизвестная ошибка")
    })
    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> downloadResource(
            @ParameterObject @Valid @ModelAttribute
            ResourceRequestDto requestDto) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();
        DownloadResource downloadResource = resourceFacadeService.download(userId, requestDto.path());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=\"" + downloadResource.resourceName() + "\"")
                .body(downloadResource.body());
    }

    @Operation(summary = "Перемещение/Переименование ресурса",
            description = "Переименование ресурса или перемещение со всеми вложенными ресурсами")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Успешное Перемещение/Переименование ресурса"),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка валидации пути к ресурсу или к его новому месту," +
                            "Ошибка при попытке одновременно преименовать и переместить пользователя," +
                            "Ошибка при попытке переместить папку в себя же," +
                            "Ошибка валидации пути к ресурсу или к его новому месту"),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404",
                    description = "Выбранный ресурс не существует," +
                            "Конечная родительская папка перемещаемого ресурса не существует"),
            @ApiResponse(responseCode = "409",
                    description = "В конечной родительской папке уже существует ресурс с таким именем"),
            @ApiResponse(responseCode = "500",
                    description = "Ошибка при обращении к БД,Неизвестная ошибка")
    })
    @PostMapping("/move")
    @ResponseStatus(HttpStatus.OK)
    public ResourceResponseDto moveResource(
            @ParameterObject @Valid @ModelAttribute
            ResourceMoveRequestDto requestDto) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();
        return resourceFacadeService.renameOrMove(userId, requestDto.from(), requestDto.to());
    }

    @Operation(summary = "Поиск ресурса",
            description = "Поиск совпадений запроса среди всех ресурсов текущего пользователя.Регистр не важен")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Успешный поиск ресурсов"),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка валидации запроса"),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "500",
                    description = "Ошибка при обращении к БД,Неизвестная ошибка")
    })
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ResourceResponseDto> search(
            @ParameterObject @Valid @ModelAttribute
            ResourceSearchRequestDto requestDto) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();
        return resourceFacadeService.search(userId, requestDto.query());
    }

}
