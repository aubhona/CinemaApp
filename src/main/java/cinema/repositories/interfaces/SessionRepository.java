package cinema.repositories.interfaces;

import cinema.entities.Session;

import java.util.List;

public interface SessionRepository {
    void addSession(Session session);
    void addSessions(List<Session> sessions);
    List<Session> query(SessionSpecification specification);
}
