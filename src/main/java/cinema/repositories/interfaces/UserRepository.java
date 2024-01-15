package cinema.repositories.interfaces;

import cinema.entities.User;

import java.util.List;

public interface UserRepository {
    void addUser(User user);
    List<User> query(UserSpecification specification);
}
