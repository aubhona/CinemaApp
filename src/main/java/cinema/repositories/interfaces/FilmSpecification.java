package cinema.repositories.interfaces;

import cinema.entities.Film;

public interface FilmSpecification {
    boolean check(Film film);
}
