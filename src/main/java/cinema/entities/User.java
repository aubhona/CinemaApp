package cinema.entities;

public class User {
    private String _name;
    private String _surname;
    private String _login;
    private String _passwordHash;

    protected User() {}

    public User(String name, String surname, String login, String passwordHash) {
        if (name.isEmpty() || surname.isEmpty() || login.isEmpty() || passwordHash.isEmpty()) {
            throw new IllegalArgumentException("Нельзя вводить пустые строки!");
        }
        _name = name;
        _surname = surname;
        _login = login;
        _passwordHash = passwordHash;
    }

    public String getLogin() {
        return _login;
    }

    public String getPasswordHash() {
        return _passwordHash;
    }

    public String getName() {
        return _name;
    }

    public String getSurname() {
        return _surname;
    }
}
