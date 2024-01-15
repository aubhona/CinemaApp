package cinema.repositories.interfaces;

import cinema.entities.User;

public interface UserSpecification {
    boolean check(User user);
}
