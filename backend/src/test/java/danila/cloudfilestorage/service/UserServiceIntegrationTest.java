package danila.cloudfilestorage.service;

import danila.cloudfilestorage.dto.AuthResponseDto;
import danila.cloudfilestorage.entity.User;
import danila.cloudfilestorage.exception.InvalidCredentialsException;
import danila.cloudfilestorage.exception.UserAlreadyExistException;
import danila.cloudfilestorage.repository.UserRepository;
import danila.cloudfilestorage.service.authentication.SessionAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class UserServiceIntegrationTest extends IntegrationTest {
    private static final String USER_LOGIN = "test_user";
    private static final String USER_PASSWORD = "test_password";

    @Autowired
    private SessionAuthenticationService authService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDB() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("При регистрации в бд появляется данный пользователь")
    void mustCreateUser() {
        authService.signUp(USER_LOGIN, USER_PASSWORD);
        User foundUser = userRepository.findByName(USER_LOGIN).orElseThrow();
        assertThat(foundUser.getId()).isNotNull();
        assertThat(foundUser.getName()).isEqualTo(USER_LOGIN);
    }

    @Test
    @DisplayName("Регистрация с именем которое уже есть в бд,приводит к исключению об одинаковом имени")
    void mustThrowUserAlreadyExistException() {
        authService.signUp(USER_LOGIN, USER_PASSWORD);
        assertThatThrownBy(() -> authService.signUp(USER_LOGIN, USER_PASSWORD))
                .isInstanceOf(UserAlreadyExistException.class);
    }

    @Test
    @DisplayName("При регистрации в базе сохраняется хэшированный пароль")
    void mustHashedPassword() {
        authService.signUp(USER_LOGIN, USER_PASSWORD);
        User foundUser = userRepository.findByName(USER_LOGIN).orElseThrow();
        assertThat(foundUser.getPassword()).isNotEqualTo(USER_PASSWORD);
    }

    @Test
    @DisplayName("Попытка входа с неверными данными приводит к исключению неверных учетных данных ")
    void mustThrowInvalidCredentialsException() {
        authService.signUp(USER_LOGIN, USER_PASSWORD);
        assertThatThrownBy(() -> authService
                .signIn("invalid_user", "invalid_password"))
                .isInstanceOf(InvalidCredentialsException.class);
    }


    @Test
    @DisplayName("Попытка входа с верными данными приводит к успешной аутентификации")
    void mustSignInIfCredentialsTrue() {
        authService.signUp(USER_LOGIN, USER_PASSWORD);
        AuthResponseDto responseDto = authService.signIn(USER_LOGIN, USER_PASSWORD);
        assertThat(responseDto.username()).isEqualTo(USER_LOGIN);
    }
}
