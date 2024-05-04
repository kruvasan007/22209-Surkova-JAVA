package Factory;

public enum CommandType {
    Push(0),
    Pop(1),
    Plus(2),
    Mult(3),
    Div(4),
    Sqrt(5),
    Print(6),
    Min(7),
    Define(8),

    Unknown(9);

    private final int value;

    CommandType(int value) {
        this.value= value;
    }
}
