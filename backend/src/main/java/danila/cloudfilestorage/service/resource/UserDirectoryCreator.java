package danila.cloudfilestorage.service.resource;

import danila.cloudfilestorage.api.repository.ResourceStorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDirectoryCreator {
    private static final String USER_PATH = "user-%d-files/";
    private final ResourceStorageRepository resourceStorageRepository;

    public String getOrCreateUserDirectory(long idUser) {
        String userRootPath = String.format(USER_PATH, idUser);
        if (!resourceStorageRepository.isExist(userRootPath)) {
            resourceStorageRepository.createDirectory(userRootPath);
        }
        return userRootPath;
    }
}
