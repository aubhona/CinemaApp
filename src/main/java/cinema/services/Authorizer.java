package cinema.services;

import cinema.entities.User;
import cinema.repositories.UserJsonRepository;
import cinema.repositories.interfaces.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;

public class Authorizer {
    private final UserRepository _repository = new UserJsonRepository();

    private String getHashCode(String input) {
        return DigestUtils.md5Hex(input);
    }
    public String authorize(String login, String password) {
        password = getHashCode(password);
        List<User> users = _repository.query((User user) -> user.getLogin().equals(login));
        if (users.isEmpty() || !users.get(0).getPasswordHash().equals(password)) {
            return null;
        }

        return users.get(0).getName() + " " + users.get(0).getSurname();
    }
}
