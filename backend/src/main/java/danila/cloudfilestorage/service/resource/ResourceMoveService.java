package danila.cloudfilestorage.service.resource;

import danila.cloudfilestorage.api.repository.ResourceStorageRepository;
import danila.cloudfilestorage.dto.ResourceResponseDto;
import danila.cloudfilestorage.enums.ResourceType;
import danila.cloudfilestorage.exception.InvalidRequestParametersException;
import danila.cloudfilestorage.exception.ResourceAlreadyExistException;
import danila.cloudfilestorage.exception.ResourceNotFoundException;
import danila.cloudfilestorage.model.StorageResource;
import danila.cloudfilestorage.util.PathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceMoveService {
    private static final String START_PATH_RESOURCE_NOT_FOUND = "Ресурс по начальному пути не найден";
    private static final String END_PATH_RESOURCE_ALREADY_EXIST = "По конечному пути ресурс с таким именем уже существует";
    private static final String DIFFERENT_OPERATION_ERROR_MESSAGE = "Нельзя одновременно переместить и переименовать ресурс";
    private static final String MOVE_DIRECTORY_IN_DIRECTORY = "Нельзя перемещать директорию в себя же";
    private static final String END_PATH_PARENT_DIRECTORY_NOT_FOUND = "По конечному пути родительской папки не существует";
    private final ResourceStorageRepository resourceStorageRepository;

    public ResourceResponseDto renameOrMove(String userRootPath, String oldResourcePath, String newResourcePath) {
        String oldFullResourcePath = userRootPath + oldResourcePath;
        String newFullResourcePath = userRootPath + newResourcePath;
        String newFullResourceParentPath = PathUtil.getPathWithRemoteLastPart(newResourcePath);
        String newResourceName = PathUtil.getResourceNameWithoutSlash(newResourcePath);
        validation(oldFullResourcePath, newFullResourcePath);
        if (oldFullResourcePath.endsWith("/")) {
            List<String> oldFullResourcePathList = resourceStorageRepository.getResourceNameList(oldFullResourcePath);
            for (String currentOldFullResourcePath : oldFullResourcePathList) {
                String currentNewFullResourcePath = newFullResourcePath +
                        currentOldFullResourcePath.substring(oldFullResourcePath.length());
                resourceStorageRepository.renameResource(currentOldFullResourcePath, currentNewFullResourcePath);
            }
            return new ResourceResponseDto(newFullResourceParentPath, newResourceName, null, ResourceType.DIRECTORY);
        } else {
            resourceStorageRepository.renameResource(oldFullResourcePath, newFullResourcePath);
            StorageResource storageResource = resourceStorageRepository.getResourceInfo(newFullResourcePath);
            return new ResourceResponseDto(newFullResourceParentPath, newResourceName, storageResource.size(),
                    ResourceType.FILE);
        }
    }

    private void validation(String oldFullResourcePath, String newFullResourcePath) {
        String oldFullResourceParentPath = PathUtil.getPathWithRemoteLastPart(oldFullResourcePath);
        String oldResourceName = PathUtil.getResourceNameWithSlashIfHas(oldFullResourcePath);
        String newFullResourceParentPath = PathUtil.getPathWithRemoteLastPart(newFullResourcePath);
        String newResourceName = PathUtil.getResourceNameWithSlashIfHas(newFullResourcePath);
        if (!oldFullResourceParentPath.equals(newFullResourceParentPath) && !oldResourceName.equals(newResourceName)) {
            throw new InvalidRequestParametersException(DIFFERENT_OPERATION_ERROR_MESSAGE);
        }
        if (newFullResourceParentPath.equals(oldFullResourcePath)) {
            throw new InvalidRequestParametersException(MOVE_DIRECTORY_IN_DIRECTORY);
        }
        if (!resourceStorageRepository.isExist(newFullResourceParentPath)) {
            throw new ResourceNotFoundException(END_PATH_PARENT_DIRECTORY_NOT_FOUND);
        }
        if (!resourceStorageRepository.isExist(oldFullResourcePath)) {
            throw new ResourceNotFoundException(START_PATH_RESOURCE_NOT_FOUND);
        }
        if (resourceStorageRepository.isExist(newFullResourcePath)) {
            throw new ResourceAlreadyExistException(END_PATH_RESOURCE_ALREADY_EXIST);
        }
    }
}
