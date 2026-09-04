package danila.cloudfilestorage.service.resource;

import danila.cloudfilestorage.dto.ResourceResponseDto;
import danila.cloudfilestorage.model.DownloadResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ResourceFacadeService {
    private final ResourceUploadService resourceUploadService;
    private final ResourceService resourceService;
    private final ResourceDownloadService resourceDownloadService;
    private final ResourceMoveService resourceMoveService;
    private final DirectoryService directoryService;
    private final UserDirectoryCreator userDirectoryCreator;

    public ResourceResponseDto createDirectory(long userId, String resourcePath) {
        String userRootPath = userDirectoryCreator.getOrCreateUserDirectory(userId);
        return directoryService.createDirectory(userRootPath, resourcePath);
    }

    public List<ResourceResponseDto> getDirectoryContent(long userId, String resourceParentPath) {
        String userRootPath = userDirectoryCreator.getOrCreateUserDirectory(userId);
        return directoryService.getDirectoryContent(userRootPath, resourceParentPath);
    }

    public DownloadResource download(long userId, String resourcePath) {
        String userRootPath = userDirectoryCreator.getOrCreateUserDirectory(userId);
        return resourceDownloadService.download(userRootPath, resourcePath);
    }

    public ResourceResponseDto renameOrMove(long userId, String oldResourcePath, String newResourcePath) {
        String userRootPath = userDirectoryCreator.getOrCreateUserDirectory(userId);
        return resourceMoveService.renameOrMove(userRootPath, oldResourcePath, newResourcePath);
    }

    public ResourceResponseDto getInfo(long userId, String resourcePath) {
        String userRootPath = userDirectoryCreator.getOrCreateUserDirectory(userId);
        return resourceService.getInfo(userRootPath, resourcePath);
    }

    public void delete(long userId, String resourcePath) {
        String userRootPath = userDirectoryCreator.getOrCreateUserDirectory(userId);
        resourceService.delete(userRootPath, resourcePath);
    }

    public List<ResourceResponseDto> search(long userId, String query) {
        String userRootPath = userDirectoryCreator.getOrCreateUserDirectory(userId);
        return resourceService.search(userRootPath, query.toLowerCase(Locale.ROOT));
    }

    public List<ResourceResponseDto> upload(long userId, List<MultipartFile> resourceList, String resourceParentPath) {
        String userRootPath = userDirectoryCreator.getOrCreateUserDirectory(userId);
        return resourceUploadService.upload(userRootPath, resourceList, resourceParentPath);
    }
}
