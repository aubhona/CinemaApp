package cinema.entities;

import cinema.repositories.FilmJsonRepository;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class Session {
    private static final int _rowNum;
    private static final int _seatNumInRow;
    private LocalDateTime _dateTime;
    private Film _film;
    private Ticket[][] _tickets;

    static {
        Properties prop = new Properties();
        try (InputStream input = FilmJsonRepository.class.getClassLoader().getResourceAsStream("config.properties")) {
            prop.load(input);
            _rowNum = Integer.parseInt(prop.getProperty("CINEMA_HALL_ROW"));
            _seatNumInRow = Integer.parseInt(prop.getProperty("CINEMA_HALL_SEAT_COUNT_IN_ROW"));
        } catch (Exception e) {
            throw new IllegalArgumentException("There is a problem with config file.");
        }
    }

    protected Session() { }

    public Session(LocalDateTime dateTime, Film film) {
        if (dateTime.isBefore(film.getReleaseDate().atStartOfDay())) {
            throw new IllegalArgumentException("Сессия не может быть до выхода кино!");
        }

        _dateTime = dateTime;
        _film = film;
        _tickets = new Ticket[_rowNum][_seatNumInRow];
        for (int i = 0; i < _rowNum; ++i) {
            for (int j = 0; j < _seatNumInRow; ++j) {
                _tickets[i][j] = new Ticket(i + 1, j + 1, SeatStatus.FREE);
            }
        }
    }

    public LocalDateTime getDateTime() {
        return _dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        if (dateTime.isBefore(_film.getReleaseDate().atStartOfDay())) {
            throw new IllegalArgumentException("Сессия не может быть до выхода кино!");
        }
        _dateTime = dateTime;
    }

    public Film getFilm() {
        return _film;
    }

    public void setFilm(Film film) {
        _film = film;
        if (_dateTime.isBefore(film.getReleaseDate().atStartOfDay())) {
            throw new IllegalArgumentException("Сессия не может быть до выхода кино!");
        }
    }

    public Ticket[][] getTickets() {
        Ticket[][] tickets = new Ticket[_rowNum][_seatNumInRow];
        for (int i = 0; i < _rowNum; ++i) {
            for (int j = 0; j < _seatNumInRow; ++j) {
                tickets[i][j] = _tickets[i][j].copy();
            }
        }
        return tickets;
    }

    public void buyTicket(int rowNum, int seatNum) {
        if (rowNum <= 0 || rowNum > _rowNum || seatNum <= 0 || seatNum > _seatNumInRow) {
            throw new IllegalArgumentException("Такого места нет!");
        }
        --rowNum;
        --seatNum;
        if ( _tickets[rowNum][seatNum].getStatus() == SeatStatus.PURCHASED || _tickets[rowNum][seatNum].getStatus() == SeatStatus.BUSY) {
            throw new IllegalArgumentException("Место уже куплено!");
        }
        _tickets[rowNum][seatNum].setStatus(SeatStatus.PURCHASED);
    }

    public void takeSeat(int rowNum, int seatNum) {
        checkSeatNumbers(rowNum, seatNum);
        --rowNum;
        --seatNum;
        _tickets[rowNum][seatNum].setStatus(SeatStatus.BUSY);
    }

    public void returnTicket(int rowNum, int seatNum) {
        checkSeatNumbers(rowNum, seatNum);
        --rowNum;
        --seatNum;
        if (_dateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Возврат оформлять уже поздно!");
        }
        _tickets[rowNum][seatNum].setStatus(SeatStatus.FREE);
    }

    private void checkSeatNumbers(int rowNum, int seatNum) {
        if (rowNum <= 0 || rowNum > _rowNum || seatNum <= 0 || seatNum > _seatNumInRow){
            throw new IllegalArgumentException("Такого места нет!");
        }
        --rowNum;
        --seatNum;
        if ( _tickets[rowNum][seatNum].getStatus() == SeatStatus.FREE) {
            throw new IllegalArgumentException("Место не куплено!");
        }
        if (_tickets[rowNum][seatNum].getStatus() == SeatStatus.BUSY) {
            throw new IllegalArgumentException("Место уже занято!");
        }
    }

    @Override
    public String toString() {
        return "Сеанс: " + _dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
                + " " + _dateTime.getDayOfWeek().toString();
    }
}
