package cinema.services;

import cinema.entities.User;
import cinema.repositories.UserJsonRepository;
import cinema.repositories.interfaces.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;

public class Registrar {
    private final UserRepository _repository = new UserJsonRepository();

    private String getHashCode(String input) {
        return DigestUtils.md5Hex(input);
    }
    public boolean register(String name, String surname, String login, String password) {
        password = getHashCode(password);
        if (!_repository.query((User user) -> user.getLogin().equals(login)).isEmpty()) {
            return false;
        }
        _repository.addUser(new User(name, surname, login, password));

        return true;
    }
}
