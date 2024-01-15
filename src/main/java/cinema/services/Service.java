package cinema.services;

import cinema.entities.Film;
import cinema.entities.Session;
import cinema.entities.Ticket;
import cinema.repositories.FilmJsonRepository;
import cinema.repositories.SessionJsonRepository;
import cinema.repositories.interfaces.FilmRepository;
import cinema.repositories.interfaces.SessionRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.NoSuchFileException;
import java.security.KeyException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Service {
    private final BufferedReader _console =  new BufferedReader(new InputStreamReader(System.in));
    private final Registrar _registrar = new Registrar();
    private final Authorizer _authorizer = new Authorizer();
    private final FilmRepository _filmRepository = new FilmJsonRepository();
    private final SessionRepository _sessionRepository = new SessionJsonRepository();
    private List<Film> _films = null;
    private List<Session> _allSessions = null;
    private List<Session> _sessions = null;
    private int _selectedFilm = 0;
    private int _selectedSession = 0;

    public Service() {
    }

    private void colorPrintln(String message, Color color) {
        System.out.println(color.getValue() + message + color.getValue());
    }

    private void colorPrint(String message, Color color) {
        System.out.print(color.getValue() + message + color.getValue());
    }

    private String readLine(String message) {
        colorPrint(message, Color.GRAY);
        try {
            return _console.readLine();
        } catch (Exception e) {
            throw new InputMismatchException("Ошибка ввода!");
        }
    }
    
    private int readInt(String message) {
        try {
            return Integer.parseInt(readLine(message));
        } catch (Exception e) {
            throw new InputMismatchException("Введено не число!");
        }
    }

    private LocalDate readDate(String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        try {
            return LocalDate.parse(readLine(message), formatter);
        } catch (Exception e) {
            throw new InputMismatchException();
        }
    }

    private LocalDateTime readDateTime(String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        try {
            return LocalDateTime.parse(readLine(message), formatter);
        } catch (Exception e) {
            throw new InputMismatchException();
        }
    }

    private int readInt() {
        return readInt("");
    }

    private void showCinemaHall(Session session) {
        Ticket[][] tickets = session.getTickets();
        int cellWidth = Integer.toString(tickets[0].length + tickets.length).length();
        int consoleWidth = (tickets[0].length + 2) * cellWidth + 3;
        String seat = "█";
        colorPrintln(session.getFilm().toString(), Color.GRAY);
        colorPrintln(session.toString(), Color.GRAY);
        colorPrintln(String.join("", Collections.nCopies((consoleWidth - 5) / 2, " ")) + "Экран"
                        + String.join("", Collections.nCopies((consoleWidth - 5) / 2, " ")), Color.GRAY);
        colorPrintln(String.join("", Collections.nCopies(consoleWidth, "-")), Color.GRAY);
        for (int i = 0; i < tickets.length; ++i) {
            colorPrint(String.format("%" + (cellWidth + 2) + "s", (i + 1) + "| "), Color.GRAY);
            for (int j = 0; j < tickets[i].length; ++j) {
                Color color = switch(tickets[i][j].getStatus()) {
                    case BUSY -> Color.YELLOW;
                    case FREE -> Color.GREEN;
                    default -> Color.GRAY;
                };
                colorPrint(String.format("%-" + cellWidth + "s", seat), color);
            }
            colorPrintln(String.format("%-" + (cellWidth + 1) + "s", "|" + (i + 1)), Color.GRAY);
        }
        System.out.print(String.join("", Collections.nCopies(cellWidth + 2, " ")));
        for (int i = 0; i < tickets[0].length; ++i) {
            System.out.printf("%-" + cellWidth + "s", i + 1);
        }
        System.out.println();
        colorPrint(seat, Color.GRAY);
        colorPrintln(" - купленные места", Color.GRAY);
        colorPrint(seat, Color.GREEN);
        colorPrintln(" - свободные места", Color.GRAY);
        colorPrint(seat, Color.YELLOW);
        colorPrintln(" - занятые места", Color.GRAY);
    }

    private void buyTickets() {
        int row = readInt("Введите номер ряда >> ");
        int seat = readInt("Введите номер места >> ");
        _sessions.get(_selectedSession).buyTicket(row, seat);
        _sessionRepository.addSessions(_allSessions);
    }

    private void takeSeat() {
        int row = readInt("Введите номер ряда >> ");
        int seat = readInt("Введите номер места >> ");
        _sessions.get(_selectedSession).takeSeat(row, seat);
        _sessionRepository.addSessions(_allSessions);
    }

    private void returnTicket() {
        int row = readInt("Введите номер ряда >> ");
        int seat = readInt("Введите номер места >> ");
        _sessions.get(_selectedSession).returnTicket(row, seat);
        _sessionRepository.addSessions(_allSessions);
    }

    private void editSession() throws Exception {
        colorPrintln("Введите данные о новом сеансе: ", Color.GRAY);
        Session newSession = inputSession();
        _sessions.get(_selectedSession).setDateTime(newSession.getDateTime());
        _sessionRepository.addSessions(_allSessions);
        colorPrintln("Данные о сеансе успешно поменялись.", Color.GREEN);
    }

    private void selectedSessionAction() throws Exception {
        while (true) {
        colorPrintln("Выбранный фильм: " + _films.get(_selectedFilm).getFilmInfo(), Color.GREEN);
        colorPrintln("Выбранный сеанс: " + _sessions.get(_selectedSession), Color.GREEN);
        showCinemaHall(_sessions.get(_selectedSession));
        colorPrintln("""
                Введите номер действия:
                1) Отметить купленные билеты на выбранный сеанс
                2) Оформить возврат билетов
                3) Отметить занятые места
                4) Редактировать данные о выбранном сеансе
                5) Завершить""", Color.GRAY);
            try {
                int action = readInt();
                switch (action) {
                    case 1:
                        buyTickets();
                        continue;
                    case 2:
                        returnTicket();
                        continue;
                    case 3:
                        takeSeat();
                        continue;
                    case 4:
                        editSession();
                        continue;
                    case 5:
                        return;
                    default:
                        throw new IllegalArgumentException("Некорректная операция!");
                }
            } catch (IllegalArgumentException | InputMismatchException e) {
                colorPrintln(e.getMessage(), Color.RED);
                colorPrintln("Попробуйте снова!", Color.RED);
            }
        }
    }

    private Session inputSession() throws Exception {
        LocalDateTime dateTime;
        try {
            dateTime = readDateTime("Введите дату и время сеанса (в формате dd.MM.yyyy HH:mm) >> ");
        } catch (Exception e) {
            throw new Exception("Некорректная дата или время");
        }

        return new Session(dateTime, _films.get(_selectedFilm));
    }

    private void addNewSession() throws Exception {
        colorPrintln("Введите данные о новом сеансе:", Color.GRAY);
        _sessionRepository.addSession(inputSession());
        colorPrintln("Новый сеанс успешно добавлен.", Color.GREEN);
    }

    private void chooseSession() {
        int sessionIndex = readInt("Введите номер сеанса >> ");
        if (sessionIndex <= 0 || sessionIndex > _sessions.size()) {
            throw new IllegalArgumentException("Сеанса с таким номером в списке нет!");
        }

        _selectedSession = sessionIndex - 1;
    }

    private void showSessions() throws NoSuchFileException {
        List<Session> sessions = _sessionRepository.query((Session session) -> true);
        List<Session> selectedFilmSessions = sessions.stream().filter((Session session) ->
                session.getFilm().equals(_films.get(_selectedFilm))).toList();
        if (sessions.isEmpty() || selectedFilmSessions.isEmpty()) {
            throw new NoSuchFileException("Никаких cессий по выбранному фильму нет. Попробуйте позже.");
        }
        _sessions = selectedFilmSessions;
        _allSessions = sessions;
        for (int i = 0; i < _sessions.size(); ++i) {
            colorPrintln((i + 1) + ". " + _sessions.get(i), Color.GRAY);
        }
    }

    private Film inputFilm() throws Exception {
        String name = readLine("Введите название >> ");
        int duration = readInt("Введите продолжительность в минутах >> ");
        String briefDescription = readLine("Введите краткое описание >> ");
        LocalDate date;
        try {
            date = readDate("Введите дату выхода (в формате dd.MM.yyyy) >> ");
        } catch (Exception e) {
            throw new Exception("Некорректная дата");
        }

        return new Film(name, date, briefDescription, duration);
    }

    private void editFilm() throws Exception {
        List<Session> sessions = _sessionRepository.query((Session session) -> true);
        List<Session> selectedFilmSessions = sessions.stream().filter((Session session) ->
                session.getFilm().equals(_films.get(_selectedFilm))).toList();
        colorPrintln("Введите новые данные о фильме:", Color.GRAY);
        Film film = inputFilm();
        _films.set(_selectedFilm, film);
        _filmRepository.addFilms(_films);
        if (sessions.isEmpty() || selectedFilmSessions.isEmpty()) {
            colorPrintln("Данные о фильме успешно поменялись.", Color.GRAY);
            return;
        }
        for (Session session : selectedFilmSessions) {
            session.setFilm(film);
        }
        _sessionRepository.addSessions(sessions);
        colorPrintln("Данные о фильме успешно поменялись.", Color.GRAY);
    }

    private void addNewFilm() throws Exception {
        colorPrintln("Введите данные о новом фильме:", Color.GRAY);
        _filmRepository.addFilm(inputFilm());
        colorPrintln("Новый фильм успешно добавлен.", Color.GREEN);
    }

    private void selectedFilmAction() throws Exception {
        colorPrintln("Выбранный фильм: " + _films.get(_selectedFilm).getFilmInfo(), Color.GREEN);
        colorPrintln("""
                Введите номер действия:
                1) Выбрать сеанс, для выбранного фильма
                2) Добавить сеанс для выбранного фильма
                3) Редактировать данные о выбранном фильме""", Color.GRAY);
        while (true) {
            int action = readInt();
            switch (action) {
                case 1:
                    showSessions();
                    chooseSession();
                    selectedSessionAction();
                    return;
                case 2:
                    addNewSession();
                    return;
                case 3:
                    editFilm();
                    return;
                default:
                    colorPrintln("Некорректное действие! Попробуйте снова!", Color.RED);
            }
        }
    }

    private void chooseFilm() {
        int filmIndex = readInt("Введите номер фильма, у которого хотите посмотреть сеансы >> ");
        if (filmIndex <= 0 || filmIndex > _films.size()) {
            throw new IllegalArgumentException("Фильма с таким номером в списке нет!");
        }

        _selectedFilm = filmIndex - 1;
    }

    private void showFilms() throws NoSuchFileException {
        List<Film> films = _filmRepository.query((Film film) -> true);
        if (films.isEmpty()) {
            throw new NoSuchFileException("Никаких фильмов в прокате нет. Попробуйте позже.");
        }
        for (int i = 0; i < films.size(); ++i) {
            colorPrintln((i + 1) + ". " + films.get(i).getFilmInfo(), Color.GRAY);
        }

        _films = films;
    }

    private boolean chooseAction() {
        colorPrintln("""
                Введите номер действия:
                1) Выбрать фильм, находящийся в прокате
                2) Добавить фильм в прокат
                3) Завершить""", Color.GRAY);
        try {
            int action = readInt();
            switch (action) {
                case 1:
                    showFilms();
                    chooseFilm();
                    selectedFilmAction();
                    return true;
                case 2:
                    addNewFilm();
                    return true;
                case 3:
                    return false;
                default:
                    throw new KeyException("Некорректное действие!");
            }
        } catch (InputMismatchException e) {
            colorPrintln("Некорректное дейтсвие!\nПопробуйте снова!", Color.RED);
            return true;
        } catch (NoSuchFileException e) {
            colorPrintln(e.getMessage(), Color.YELLOW);
            return true;
        } catch (Exception e) {
            colorPrintln(e.getMessage(), Color.RED);
            colorPrintln("Попробуйте снова!", Color.RED);
            return true;
        }
    }

    private void registerUser() {
        String name = readLine("Введите имя >> ");
        String surname = readLine("Введите фамилию >> ");
        String login = readLine("Введите логин >> ");
        String password = readLine("Введите пароль >> ");
        boolean isSuccess = _registrar.register(name, surname, login, password);
        if (!isSuccess) {
            throw new  IllegalArgumentException("Пользователь с таким логином существует!");
        }
        colorPrintln("Добро пожаловать, " + name + " " + surname + "!",  Color.GREEN);
    }

    private void authorizeUser() {
        String login = readLine("Введите логин >> ");
        String password = readLine("Введите пароль >> ");
        String name = _authorizer.authorize(login, password);
        if (name == null) {
            throw new IllegalArgumentException("Ошибка в логине или пароле!");
        }
        colorPrintln("Добро пожаловать, " + name + "!",  Color.GREEN);
    }

    private boolean logInOrRegister() {
        while (true) {
            colorPrintln("""
                    Добро пожаловать в CinemaAPP!
                    Введите номер действия:
                    1) Зарегистрироваться
                    2) Войти
                    3) Завершить""", Color.GRAY);
            try {
                int action = readInt();
                return switch (action) {
                    case 1 -> {
                        registerUser();
                        yield  true;
                    }
                    case 2 -> {
                        authorizeUser();
                        yield true;
                    }
                    case 3 -> false;
                    default -> throw new KeyException("Некорректное дейтсвие!");
                };
            } catch (InputMismatchException e) {
                colorPrintln("Некорректное дейтсвие!\nПопробуйте снова!", Color.RED);
            } catch (Exception e) {
                colorPrintln(e.getMessage(), Color.RED);
                colorPrintln("Попробуйте снова!", Color.RED);
            }
        }
    }

    public void start() {
        boolean isContinue = logInOrRegister();
        if (!isContinue) {
            return;
        }
        while (chooseAction()) {}
    }
}
