package ch.epfl.cs107.play.window;

/**
 * Represents the keyboard.
 */
public interface Keyboard {

    int TAB = 9;
    int ENTER = 10;
    int ESC = 27;
    int SPACE = 32;
    int SHIFT = 16;
    int UP = 38;
    int DOWN = 40;
    int LEFT = 37;
    int RIGHT = 39;
    int ZERO = 48;
    int ONE = 49;
    int TWO = 50;
    int THREE = 51;
    int FOUR = 52;
    int FIVE = 53;
    int SIX = 54;
    int SEVEN = 55;
    int EIGHT = 56;
    int NINE = 57;
    int A = 65;
    int B = 66;
    int C = 67;
    int D = 68;
    int E = 69;
    int F = 70;
    int G = 71;
    int H = 72;
    int I = 73;
    int J = 74;
    int K = 75;
    int L = 76;
    int M = 77;
    int N = 78;
    int O = 79;
    int P = 80;
    int Q = 81;
    int R = 82;
    int S = 83;
    int T = 84;
    int U = 85;
    int V = 86;
    int W = 87;
    int X = 88;
    int Y = 89;
    int Z = 90;

    /**
     * Getter for the button corresponding to the given code
     *
     * @param code (int): the given code
     * @return (Button): state of the button for the given code
     */
    Button get(int code);

}
