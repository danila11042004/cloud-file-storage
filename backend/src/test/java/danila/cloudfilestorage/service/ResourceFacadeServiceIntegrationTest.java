package danila.cloudfilestorage.service;

import danila.cloudfilestorage.dto.ResourceResponseDto;
import danila.cloudfilestorage.exception.InvalidRequestParametersException;
import danila.cloudfilestorage.exception.ResourceAlreadyExistException;
import danila.cloudfilestorage.exception.ResourceNotFoundException;
import danila.cloudfilestorage.model.DownloadResource;
import danila.cloudfilestorage.model.StorageResource;
import danila.cloudfilestorage.repository.MinioResourceStorageRepository;
import danila.cloudfilestorage.service.resource.ResourceFacadeService;
import danila.cloudfilestorage.service.resource.UserDirectoryCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ResourceFacadeServiceIntegrationTest extends IntegrationTest {
    private static final long USER_ID = 1L;
    private static final String FIRST_DIRECTORY_IN_ROOT = "first/";
    private static final String SECOND_DIRECTORY_IN_ROOT = "second/";
    private static final String FILENAME = "test.txt";
    private static final String DIGEST_ALGORITHM = "SHA-256";
    private static final String TEXT_IN_FILE = "Test text";

    @Autowired
    private ResourceFacadeService resourceFacadeService;

    @Autowired
    private UserDirectoryCreator userDirectoryCreator;

    @Autowired
    private MinioResourceStorageRepository minioResourceStorageRepository;

    @BeforeEach
    void cleanRootDirectory() {
        resourceFacadeService.delete(USER_ID, "");
    }


    @Test
    @DisplayName("При получении информации о ресурсе,если путь корректен,мы получим соответствующую информацию")
    void mustGetInfoResource() {
        ResourceResponseDto createDto = resourceFacadeService.createDirectory(USER_ID, FIRST_DIRECTORY_IN_ROOT);
        ResourceResponseDto getResourceDto = resourceFacadeService.getInfo(USER_ID, FIRST_DIRECTORY_IN_ROOT);
        assertThat(createDto).isEqualTo(getResourceDto);
    }

    @Test
    @DisplayName("При получении информации о несуществующем ресурсе, мы получим исключение о ненайденном ресурсе")
    void mustThrowNotFoundResourceWhenGetInfoResource() {
        assertThatThrownBy(() -> resourceFacadeService.getInfo(USER_ID, FIRST_DIRECTORY_IN_ROOT))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("При корректном пути должна создастся директория")
    void mustCreateDirectory() {
        ResourceResponseDto createDto = resourceFacadeService.createDirectory(USER_ID, FIRST_DIRECTORY_IN_ROOT);
        ResourceResponseDto getResourceDto = resourceFacadeService.getInfo(USER_ID, FIRST_DIRECTORY_IN_ROOT);
        assertThat(createDto).isEqualTo(getResourceDto);
    }

    @Test
    @DisplayName("При создании директории с именем,которое уже принадлежит другой директории в " +
            "этой же родительской директории,мы получим исключение о существовании такого же ресурса")
    void mustThrowDirectoryAlreadyExistWhenCreateDirectory() {
        resourceFacadeService.createDirectory(USER_ID, FIRST_DIRECTORY_IN_ROOT);
        assertThatThrownBy(() -> resourceFacadeService.createDirectory(USER_ID, FIRST_DIRECTORY_IN_ROOT))
                .isInstanceOf(ResourceAlreadyExistException.class);
    }

    @Test
    @DisplayName("При создании директории с родителским путем ,которого не существует,мы получим " +
            "исключение о не найденном ресурсе")
    void mustThrowParentNotFoundWhenCreateDirectory() {
        assertThatThrownBy(() -> resourceFacadeService.createDirectory(USER_ID, "first/second/"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("При получении ресурсов директории,если путь корректен,мы получим ожидаемые ресурсы ")
    void mustGetContentDirectory() {
        resourceFacadeService.createDirectory(USER_ID, FIRST_DIRECTORY_IN_ROOT);
        ResourceResponseDto createSecondDirectoryDto = resourceFacadeService
                .createDirectory(USER_ID, "first/second/");
        resourceFacadeService.createDirectory(USER_ID, "first/second/three");
        List<MultipartFile> multipartFileList = getMultipartFileList(FILENAME);
        List<ResourceResponseDto> uploadedResourceList = resourceFacadeService
                .upload(USER_ID, multipartFileList, FIRST_DIRECTORY_IN_ROOT);
        List<ResourceResponseDto> expectedResource = new ArrayList<>(uploadedResourceList);
        expectedResource.add(createSecondDirectoryDto);
        List<ResourceResponseDto> getContentDirectoryDto = resourceFacadeService
                .getDirectoryContent(USER_ID, FIRST_DIRECTORY_IN_ROOT);
        assertThat(getContentDirectoryDto).containsExactlyInAnyOrderElementsOf(expectedResource);
    }

    @Test
    @DisplayName("При получении ресурсов директории, которой не существует,мы получим исключение,о не найденном ресурсе")
    void mustThrowCurrentDirectoryNotFoundWhenGetContentDirectory() {
        assertThatThrownBy(() -> resourceFacadeService.getDirectoryContent(USER_ID, FIRST_DIRECTORY_IN_ROOT))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("При скачивании файла,если путь корректен,мы примем ожидаемый файл по частям.Без сохранения в RAM")
    void mustDownloadFile() throws NoSuchAlgorithmException, IOException {
        List<MultipartFile> multipartFileList = getMultipartFileList(FILENAME);
        resourceFacadeService.upload(USER_ID, multipartFileList, "");
        DownloadResource downloadResource = resourceFacadeService.download(USER_ID, FILENAME);
        assertThat(downloadResource.resourceName()).isEqualTo(FILENAME);
        MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);
        try (DigestOutputStream outputStream = new DigestOutputStream(OutputStream.nullOutputStream(), digest)) {
            downloadResource.body().writeTo(outputStream);
        }
        byte[] actualHash = digest.digest();
        byte[] expectedHash = MessageDigest.getInstance(DIGEST_ALGORITHM)
                .digest(TEXT_IN_FILE.getBytes(StandardCharsets.UTF_8));
        assertThat(actualHash).isEqualTo(expectedHash);
    }

    @Test
    @DisplayName("При скачивании папки,если путь корректен,мы получим ожидаемый ZIP с теми же ресурсами." +
            "С сохранением в RAM для упрощения")
    void mustDownloadZip() throws IOException {
        resourceFacadeService.createDirectory(USER_ID, FIRST_DIRECTORY_IN_ROOT);
        resourceFacadeService.createDirectory(USER_ID, "first/second/");
        resourceFacadeService.createDirectory(USER_ID, "first/three/");
        DownloadResource downloadResource = resourceFacadeService.download(USER_ID, FIRST_DIRECTORY_IN_ROOT);
        assertThat(downloadResource.resourceName()).isEqualTo("first.zip");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        downloadResource.body().writeTo(outputStream);
        Set<String> directorySet = new HashSet<>();
        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(outputStream.toByteArray()))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                directorySet.add(zipEntry.getName());
            }
        }
        assertThat(directorySet).containsExactlyInAnyOrder(FIRST_DIRECTORY_IN_ROOT, "first/second/",
                "first/three/");
    }

    @Test
    @DisplayName("При скачивании ресурса, которого не существует,мы получим исключение,о не найденном ресурсе")
    void mustThrowResourceNotFoundWhenDownloadResource() {
        assertThatThrownBy(() -> resourceFacadeService.download(USER_ID, FIRST_DIRECTORY_IN_ROOT))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("При перемещении ресурса если пути корректные,то ресурс переместится")
    void mustMoveResource() {
        resourceFacadeService.createDirectory(USER_ID, FIRST_DIRECTORY_IN_ROOT);
        resourceFacadeService.createDirectory(USER_ID, "first/second/");
        resourceFacadeService.createDirectory(USER_ID, "first/three/");
        resourceFacadeService.createDirectory(USER_ID, "newDirectory/");
        ResourceResponseDto moveResponseDto = resourceFacadeService
                .renameOrMove(USER_ID, FIRST_DIRECTORY_IN_ROOT, "newDirectory/first/");
        ResourceResponseDto movedResourceInfoResponseDto = resourceFacadeService
                .getInfo(USER_ID, "newDirectory/first/");
        assertThat(movedResourceInfoResponseDto.path()).isEqualTo(moveResponseDto.path());
        assertThat(movedResourceInfoResponseDto.name()).isEqualTo(moveResponseDto.name());
        assertThatThrownBy(() -> resourceFacadeService
                .getInfo(USER_ID, FIRST_DIRECTORY_IN_ROOT))
                .isInstanceOf(ResourceNotFoundException.class);
        ResourceResponseDto secondDirectoryResponseDto = resourceFacadeService
                .getInfo(USER_ID, "newDirectory/first/second/");
        assertThat(secondDirectoryResponseDto.path()).isEqualTo("newDirectory/first/");
        assertThat(secondDirectoryResponseDto.name()).isEqualTo("second");
        ResourceResponseDto threeDirectoryResponseDto = resourceFacadeService
                .getInfo(USER_ID, "newDirectory/first/three/");
        assertThat(threeDirectoryResponseDto.path()).isEqualTo("newDirectory/first/");
        assertThat(threeDirectoryResponseDto.name()).isEqualTo("three");
    }

    @Test
    @DisplayName("При переименовании ресурса если пути корректные,то ресурс переименуется")
    void mustRenameResource() {
        resourceFacadeService.createDirectory(USER_ID, FIRST_DIRECTORY_IN_ROOT);
        ResourceResponseDto renameResponseDto = resourceFacadeService
                .renameOrMove(USER_ID, FIRST_DIRECTORY_IN_ROOT, SECOND_DIRECTORY_IN_ROOT);
        ResourceResponseDto renamedResourceResponseDto = resourceFacadeService
                .getInfo(USER_ID, SECOND_DIRECTORY_IN_ROOT);
        assertThat(renameResponseDto.path()).isEqualTo(renamedResourceResponseDto.path());
        assertThat(renameResponseDto.name()).isEqualTo(renamedResourceResponseDto.name());
        assertThatThrownBy(() -> resourceFacadeService
                .getInfo(USER_ID, FIRST_DIRECTORY_IN_ROOT))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("При попытке одновременно переместить и переименовать ресурс," +
            "мы получим исключение о некорректных параметрах")
    void mustThrowInvalidParameterWhenMoveAndRenameResource() {
        resourceFacadeService.createDirectory(USER_ID, FIRST_DIRECTORY_IN_ROOT);
        resourceFacadeService.createDirectory(USER_ID, SECOND_DIRECTORY_IN_ROOT);
        assertThatThrownBy(() -> resourceFacadeService
                .renameOrMove(USER_ID, FIRST_DIRECTORY_IN_ROOT, "second/newname/"))
                .isInstanceOf(InvalidRequestParametersException.class);
    }

    @Test
    @DisplayName("При перемещении ресурса в несуществующую директорию,мы получим исключение о ненайденном ресурсе")
    void mustThrowNotFoundNewDirectoryWhenMoveResource() {
        resourceFacadeService.createDirectory(USER_ID, FIRST_DIRECTORY_IN_ROOT);
        assertThatThrownBy(() -> resourceFacadeService
                .renameOrMove(USER_ID, FIRST_DIRECTORY_IN_ROOT, "second/first/"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("При перемещении/переименовании несуществующего ресурса,мы получим исключение о ненайденном ресурсе")
    void mustThrowNotFoundOldResourceWhenMoveOrRenameResource() {
        resourceFacadeService.createDirectory(USER_ID, SECOND_DIRECTORY_IN_ROOT);
        assertThatThrownBy(() -> resourceFacadeService
                .renameOrMove(USER_ID, FIRST_DIRECTORY_IN_ROOT, "second/first/"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("При перемещении/переименовании ресурса в директорию где есть ресурс с таким же названием," +
            "мы получим исключение об уже существующем ресурсе")
    void mustThrowAlreadyExistResourceWhenMoveOrRenameResource() {
        resourceFacadeService.createDirectory(USER_ID, FIRST_DIRECTORY_IN_ROOT);
        resourceFacadeService.createDirectory(USER_ID, SECOND_DIRECTORY_IN_ROOT);
        resourceFacadeService.createDirectory(USER_ID, "second/first/");
        assertThatThrownBy(() -> resourceFacadeService
                .renameOrMove(USER_ID, FIRST_DIRECTORY_IN_ROOT, "second/first/"))
                .isInstanceOf(ResourceAlreadyExistException.class);
    }

    @Test
    @DisplayName("При удалении ресурса,если путь корректен,то ресурс удалится")
    void mustDeleteResource() {
        resourceFacadeService.createDirectory(USER_ID, FIRST_DIRECTORY_IN_ROOT);
        resourceFacadeService.delete(USER_ID, FIRST_DIRECTORY_IN_ROOT);
        assertThatThrownBy(() -> resourceFacadeService
                .getInfo(USER_ID, FIRST_DIRECTORY_IN_ROOT))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("При удалении ресурса,если путь не корректен,мы получим исключение о не найденном ресурсе")
    void mustThrowNotFoundResourceWhenDeleteResource() {
        assertThatThrownBy(() -> resourceFacadeService.delete(USER_ID, FIRST_DIRECTORY_IN_ROOT))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("При поиске найдем все ресурсы в которых встречается запрос")
    void mustSearchResource() {
        ResourceResponseDto firstDto = resourceFacadeService.createDirectory(USER_ID, FIRST_DIRECTORY_IN_ROOT);
        ResourceResponseDto secondDto = resourceFacadeService.createDirectory(USER_ID, SECOND_DIRECTORY_IN_ROOT);
        resourceFacadeService.createDirectory(USER_ID, "three/");
        List<ResourceResponseDto> responseDtoList = resourceFacadeService.search(USER_ID, "s");
        List<ResourceResponseDto> expectedResourceList = new ArrayList<>();
        expectedResourceList.add(firstDto);
        expectedResourceList.add(secondDto);
        assertThat(responseDtoList).containsExactlyInAnyOrderElementsOf(expectedResourceList);
    }

    @Test
    @DisplayName("При аплоаде файла если путь корректен,то файл загрузится вместе со всеми поддиректориями")
    void mustUploadFileAndNestedDirectory() {
        List<MultipartFile> multipartFileList = getMultipartFileList("first/second/test.txt");
        List<ResourceResponseDto> uploadDtoList = resourceFacadeService.upload(USER_ID, multipartFileList, "");
        ResourceResponseDto firstDto = resourceFacadeService.getInfo(USER_ID, FIRST_DIRECTORY_IN_ROOT);
        ResourceResponseDto secondDto = resourceFacadeService.getInfo(USER_ID, "first/second/");
        ResourceResponseDto fileDto = resourceFacadeService.getInfo(USER_ID, "first/second/test.txt");
        List<ResourceResponseDto> expectedDtoList = new ArrayList<>();
        expectedDtoList.add(firstDto);
        expectedDtoList.add(secondDto);
        expectedDtoList.add(fileDto);
        assertThat(uploadDtoList).containsExactlyInAnyOrderElementsOf(expectedDtoList);
    }

    @Test
    @DisplayName("При аплоаде если директория куда загружается файл не существует ,мы получим исключение " +
            "о не найденном ресурсе")
    void mustThrowResourceNotFoundWhenUploadFile() {
        List<MultipartFile> multipartFileList = getMultipartFileList(FILENAME);
        assertThatThrownBy(() -> resourceFacadeService.upload(USER_ID, multipartFileList, FIRST_DIRECTORY_IN_ROOT))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("При любом обращении к сервису, все операции производятся только в директории выбранного пользователя")
    void mustBeCurrentUser() {
        resourceFacadeService.createDirectory(USER_ID, FIRST_DIRECTORY_IN_ROOT);
        String currentUser = userDirectoryCreator.getOrCreateUserDirectory(USER_ID);
        StorageResource storageResource = minioResourceStorageRepository
                .getResourceInfo(currentUser + FIRST_DIRECTORY_IN_ROOT);
        assertThat(storageResource.fullResourcePath()).isEqualTo(currentUser + FIRST_DIRECTORY_IN_ROOT);
    }

    private List<MultipartFile> getMultipartFileList(String originalFilename) {
        MultipartFile multipartFile = new MockMultipartFile("parameterTest", originalFilename,
                "text/plain", TEXT_IN_FILE.getBytes(StandardCharsets.UTF_8));
        return List.of(multipartFile);
    }
}
