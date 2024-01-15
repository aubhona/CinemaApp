package cinema.repositories.interfaces;

import cinema.entities.Session;

public interface SessionSpecification {
    boolean check(Session session);
}
