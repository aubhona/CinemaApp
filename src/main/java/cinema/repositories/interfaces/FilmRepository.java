package cinema.repositories.interfaces;

import cinema.entities.Film;

import java.util.List;

public interface FilmRepository {
    void addFilm(Film film);
    void addFilms(List<Film> films);
    List<Film> query(FilmSpecification specification);
}
