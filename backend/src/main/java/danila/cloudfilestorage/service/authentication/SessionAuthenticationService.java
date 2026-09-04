package danila.cloudfilestorage.service.authentication;

import danila.cloudfilestorage.api.service.AuthenticationService;
import danila.cloudfilestorage.dto.AuthResponseDto;
import danila.cloudfilestorage.entity.User;
import danila.cloudfilestorage.exception.InvalidCredentialsException;
import danila.cloudfilestorage.exception.UserAlreadyExistException;
import danila.cloudfilestorage.service.user.UserService;
import danila.cloudfilestorage.seсurity.AuthenticatedUser;
import danila.cloudfilestorage.seсurity.SecurityContextManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionAuthenticationService implements AuthenticationService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextManager securityContextManager;

    @Transactional
    public AuthResponseDto signUp(String username, String password) {
        if (userService.isExisting(username)) {
            throw new UserAlreadyExistException();
        }
        String encodedPassword = passwordEncoder.encode(password);
        User user = userService.create(username, encodedPassword);
        AuthenticatedUser authUser = new AuthenticatedUser(user.getName(), user.getId());
        securityContextManager.authenticate(authUser);
        return new AuthResponseDto(user.getName());
    }

    public AuthResponseDto signIn(String username, String password) {
        User user = userService.find(username)
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        AuthenticatedUser authUser = new AuthenticatedUser(user.getName(), user.getId());
        securityContextManager.authenticate(authUser);
        return new AuthResponseDto(user.getName());
    }
}
