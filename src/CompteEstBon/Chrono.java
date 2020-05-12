package CompteEstBon;

public class Chrono {
    public static void Countup(int chars, int seconds) {
        int msecPerChar = seconds * 1000 / 30;
        int up = 0;
        int top = seconds * 1000;
        int nb = 0;
        while (up < top ) {
            try {
                Thread.sleep(msecPerChar);
                String pb = String.format("|%s%s|\r", "=".repeat(nb), " ".repeat(chars - nb));
                System.out.write(pb.getBytes());
            }
            catch(Exception ex) {
            }
            up += msecPerChar;
            nb++;
        }
    }
}
