package cinema.repositories;

import cinema.entities.Session;
import cinema.repositories.adpters.LocalDateAdapter;
import cinema.repositories.adpters.LocalDateTimeAdapter;
import cinema.repositories.interfaces.SessionRepository;
import cinema.repositories.interfaces.SessionSpecification;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SessionJsonRepository implements SessionRepository {
    static Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    static String serializationFileName;

    static {
        Properties prop = new Properties();
        try (InputStream input = FilmJsonRepository.class.getClassLoader().getResourceAsStream("config.properties")) {
            prop.load(input);
            serializationFileName = prop.getProperty("SESSION_SERIALIZATION_FILE_NAME");
        } catch (Exception e) {
            throw new IllegalArgumentException("There is a problem with config file.");
        }
    }

    @Override
    public void addSession(Session session) {
        try (FileWriter writer = new FileWriter(serializationFileName, true)) {
            gson.toJson(session, writer);
            writer.write(System.lineSeparator());
        } catch (Exception e) {
            throw new RuntimeException("There is a problem to serialize session.");
        }
    }

    @Override
    public void addSessions(List<Session> sessions) {
        try (FileWriter writer = new FileWriter(serializationFileName, false)) {
            for (Session session : sessions) {
                gson.toJson(session, writer);
                writer.write(System.lineSeparator());
            }
        } catch (Exception e) {
            throw new RuntimeException("There is a problem to serialize session.");
        }
    }

    @Override
    public List<Session> query(SessionSpecification specification) {
        List<Session> sessions = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(serializationFileName))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Session session = gson.fromJson(line, Session.class);
                if (specification.check(session)) {
                    sessions.add(session);
                }
            }
        } catch (Exception e) {
            return sessions;
        }

        return sessions;
    }
}
