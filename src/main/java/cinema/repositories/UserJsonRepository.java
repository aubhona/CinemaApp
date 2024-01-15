package cinema.repositories;

import cinema.repositories.interfaces.UserRepository;
import cinema.repositories.interfaces.UserSpecification;
import cinema.entities.User;
import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UserJsonRepository implements UserRepository {
    static Gson gson = new Gson();
    static String serializationFileName;

    static {
        Properties prop = new Properties();
        try (InputStream input = UserJsonRepository.class.getClassLoader().getResourceAsStream("config.properties")) {
            prop.load(input);
            serializationFileName = prop.getProperty("USER_SERIALIZATION_FILE_NAME");
        } catch (Exception e) {
            throw new IllegalArgumentException("There is a problem with config file.");
        }
    }

    @Override
    public void addUser(User user) {
        try (FileWriter writer = new FileWriter(serializationFileName, true)) {
            gson.toJson(user, writer);
            writer.write(System.lineSeparator());
        } catch (Exception e) {
            throw new RuntimeException("There is a problem to serialize user.");
        }
    }

    @Override
    public List<User> query(UserSpecification specification) {
        List<User> users = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(serializationFileName))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                User user = gson.fromJson(line, User.class);
                if (specification.check(user)) {
                    users.add(user);
                }
            }
        } catch (Exception e) {
            return users;
        }

        return users;
    }
}
