package danila.cloudfilestorage.service.user;

import danila.cloudfilestorage.entity.User;
import danila.cloudfilestorage.exception.UserAlreadyExistException;
import danila.cloudfilestorage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String STATE_UNIQUE = "23505";
    private final UserRepository userRepository;

    public User create(String username, String password) {
        try {
            return userRepository.saveAndFlush(new User(username, password));
        } catch (DataAccessException e) {
            if (e.getRootCause() instanceof SQLException ex) {
                String sqlState = ex.getSQLState();
                if (sqlState.equals(STATE_UNIQUE)) {
                    throw new UserAlreadyExistException();
                }
            }
            throw e;
        }
    }

    public Optional<User> find(String name) {
        return userRepository.findByName(name);
    }

    public boolean isExisting(String name) {
        return userRepository.existsByName(name);
    }
}
