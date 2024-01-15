package cinema.entities;

public class Ticket {
    private int _row;
    private int _seatNumber;
    private SeatStatus _status;

    protected Ticket() {}

    public Ticket(int row, int seatNumber, SeatStatus status) {
        if (row <= 0 || seatNumber <= 0) {
            throw new IllegalArgumentException("Argument less than zero!");
        }
        _row = row;
        _seatNumber = seatNumber;
        _status = status;
    }

    public int getRow() {
        return _row;
    }

    public int getSeatNumber() {
        return _seatNumber;
    }

    public SeatStatus getStatus() {
        return _status;
    }

    public void setStatus(SeatStatus status) {
        _status = status;
    }

    public Ticket copy() {
        return new Ticket(_row, _seatNumber, _status);
    }
}
