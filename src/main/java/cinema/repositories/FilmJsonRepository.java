package cinema.repositories;

import cinema.repositories.adpters.LocalDateAdapter;
import cinema.repositories.interfaces.FilmRepository;
import cinema.repositories.interfaces.FilmSpecification;
import cinema.entities.Film;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FilmJsonRepository implements FilmRepository {
    static Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
    static String serializationFileName;

    static {
        Properties prop = new Properties();
        try (InputStream input = FilmJsonRepository.class.getClassLoader().getResourceAsStream("config.properties")) {
            prop.load(input);
            serializationFileName = prop.getProperty("FILM_SERIALIZATION_FILE_NAME");
        } catch (Exception e) {
            throw new IllegalArgumentException("There is a problem with config file.");
        }
    }

    @Override
    public void addFilm(Film film) {
        try (FileWriter writer = new FileWriter(serializationFileName, true)) {
            gson.toJson(film, writer);
            writer.write(System.lineSeparator());
        } catch (Exception e) {
            throw new RuntimeException("There is a problem to serialize film.");
        }
    }

    @Override
    public void addFilms(List<Film> films) {
        try (FileWriter writer = new FileWriter(serializationFileName, false)) {
            for (Film film: films) {
                gson.toJson(film, writer);
                writer.write(System.lineSeparator());
            }
        } catch (Exception e) {
            throw new RuntimeException("There is a problem to serialize film.");
        }
    }

    @Override
    public List<Film> query(FilmSpecification specification) {
        List<Film> films = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(serializationFileName))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Film film = gson.fromJson(line, Film.class);
                if (specification.check(film)) {
                    films.add(film);
                }
            }
        } catch (Exception e) {
            return films;
        }

        return films;
    }
}
