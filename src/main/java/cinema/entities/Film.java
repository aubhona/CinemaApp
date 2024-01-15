package cinema.entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Film {
    private String _name;
    private LocalDate _releaseDate;
    private String _briefDescription;
    private int _duration; // Duration of film in minutes.

    protected Film() {}

    public Film(String name, LocalDate releaseDate, String briefDescription, int duration) {
        _name = name;
        _releaseDate = releaseDate;
        _briefDescription = briefDescription;
        setDuration(duration);
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public LocalDate getReleaseDate() {
        return _releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        _releaseDate = releaseDate;
    }

    public String getBriefDescription() {
        return _briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        _briefDescription = briefDescription;
    }

    public int getDuration() {
        return _duration;
    }

    public void setDuration(int duration) {
        if (duration <= 0) {
            throw new IllegalArgumentException("Продолжительность не может быть меньше или равно нуля.");
        }
        _duration = duration;
    }

    @Override
    public String toString() {
        return "Фильм: " + _name;
    }

    public String getFilmInfo() {
        return this + "\nДата выхода: " + _releaseDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                + "\nПродолжительность: " + _duration + " мин " + "\nКраткое описание: " + _briefDescription;
    }

    public boolean equals(Film otherFilm) {
        return _name.equals(otherFilm._name) && _releaseDate.isEqual(otherFilm._releaseDate)
                && _briefDescription.equals(otherFilm._briefDescription) && _duration == otherFilm._duration;
    }
}
