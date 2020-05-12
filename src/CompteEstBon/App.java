package CompteEstBon;

import java.util.*;

// [Le compte est bon](http://mamisab.chez-alice.fr/)

// [Le compte est bon](http://j.mochel.free.fr/comptebon.php)

public class App {
    public static void main(final String[] args) throws Exception {
        Scanner in = new Scanner(System.in);

        while (true) {
            displayTitle();
            // Launch a game with 40 seconds of think time
            newGame(in, 30, 40);
            prompt(in, "Want another game ?");
            clearScreen();
        }
    }

    private static void newGame(Scanner in, int chars, int seconds) {
        Compte cpt = new Compte();
        int[] plaques = cpt.GetPlaques();
        String s = new String();
        for (int t : plaques) {
            if (s != "") {
                s += " ";
            }
            s += String.format("|%3d|", t);
        }
        System.out.println(" +---+ +---+ +---+ +---+ +---+ +---+");
        System.out.println(s);
        System.out.println(" +---+ +---+ +---+ +---+ +---+ +---+");

        String t = new String();
        int tirage = cpt.GetTirage();
        t = String.format(" |%3d|", tirage);
        System.out.println(" +---+");
        System.out.println(t);
        System.out.println(" +---+");

        // Wait for some seconds of think time
        Chrono.Countup(chars, seconds);

        prompt(in, "Want the solution ?");

        // Initialize the recursive calculation root structure
        Solution solution = new Solution();
        solution.tirage = tirage;
        solution.depth = plaques.length;
        Result res = new Result();
        res.steps = plaques;
        res.text = "";
        res.value = 0;
        Arrays.sort(res.steps);
        solution.current.add(res);

        // Initialize the best approaching structure
        solution.best = new Result();
        solution.best.steps = res.steps;
        solution.best.value = solution.best.steps[solution.best.steps.length-1];
        solution.best.text = String.format("%d", solution.best.value);

        // Start the recursive resolution
        solution = cpt.SolveTirage(solution);

        // Output final result
        System.out.println(String.format("Solution [%s]", (solution.best.value == solution.tirage ? "Exact" : "Approché")));
        System.out.println(solution.best.text);
    }

    private static void clearScreen() {  
        try {
            new ProcessBuilder("cmd", "/c", "cls")
            .inheritIO()
            .start()
            .waitFor();
        }
        catch (Exception ex) {
        }
    }

    private static void prompt(Scanner in,String msg) {
        System.out.println(msg + " (press Enter)");
        in.nextLine();
    }

    private static void displayTitle() {
        System.out.println("  +-------------------------+");
        System.out.println("  |  Le Compte est bon !    |");
        System.out.println("  +-------------------------+");
    }
}
