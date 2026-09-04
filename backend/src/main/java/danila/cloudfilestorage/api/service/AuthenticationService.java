package danila.cloudfilestorage.api.service;

import danila.cloudfilestorage.dto.AuthResponseDto;

public interface AuthenticationService {
    AuthResponseDto signUp(String username, String password);

    AuthResponseDto signIn(String username, String password);

}
