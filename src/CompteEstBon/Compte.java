package CompteEstBon;

import java.util.*;

// A solution coded in Javascript
// [Le compte est bon](http://mamisab.chez-alice.fr/)

public class Compte {
    // Games is composed on 24 tiles, with following occurences:
    //   1 to 10 = twice
    //   25, 50, 75, 100 = once
    private final int nbPlaques = 24;
    private final int nbTirage = 6;
    private final int  minTirage= 101;
    private final int  maxTirage= 999;
    private int[] plaques = new int[nbPlaques];
    private Random rnd = new Random();

    // Constructor with initialization of the needed structures
    public Compte() {
        int idx = 0;
        // 1 to 10 is present twice
        for (int n = 1; n <= 10; n++) {
            plaques[idx] = n;
            plaques[idx+1] = n;
            idx += 2;
        }
        // Other numbers are present once
        plaques[idx++] = 25;
        plaques[idx++] = 50;
        plaques[idx++] = 75;
        plaques[idx++] = 100;
    }

    // Returns a random tirage
    // Each plaque can only be chosen once
    public int[] GetPlaques() {
        // Randomly select a list of indexes (can only be chosen once)
        List<Integer> chosenIdx = new ArrayList<Integer>();
        int nbChosen = 0;
        while (nbChosen < nbTirage) {
            int p = rnd.nextInt(nbPlaques);
            if (!chosenIdx.contains(p)) {
                chosenIdx.add(p);
                nbChosen++;
            }
        }

        // Compute the returned list with the values
        int[] tirage = new int[nbTirage];
        for (int i=0; i<nbTirage; i++) {
            tirage[i] = plaques[chosenIdx.get(i)];
        }
        return tirage;
    }

    // Returns a random number to calculate
    public int GetTirage() {
        return minTirage + rnd.nextInt(maxTirage - minTirage + 1);
    }

    // Solves a computation of a tirage
    // - recursive
    // - apply the 4 operations
    // - stop if found the result
    // - if not found, keep the closest result
    // - results must be chained: it can approach, then go farther, and closer after
    // - first keep the first correct result
    // - in a more sophisticated version, store all good results and propose shorest
    //
    // Principles of operations:
    // - "+" and "x" can always be applied
    // - "-" can only produce strictly positive values
    // - "/" can only produce entire values and divide by 1 is useless
    //
    // Structural principles:
    // - impossible to compute a step with itself
    //
    // Returns the solution
    public Solution SolveTirage(Solution solution) {
        // Terminal case: nothing left to calculate
        if (solution.depth == 1) {
            return solution;
        }

        // Loop through all the possible operations for this set of steps
        // Constructs a grid, with only the sub left half to be processed
        // - numbers are sorted ascending
        // - useless to do adition twice: l + r, and then r + l
        // - same with multiply
        // - substraction and division are only one way and left is always bigger
        // Example for a steps set with 6 items:
        //   | 1 | 2 | 3 | 4 | 5 | 6 |
        // 1 | W | U | U | U | U | U |
        // 2 | + | W | U | U | U | U |
        // 3 | + | + | W | U | U | U |
        // 4 | + | + | + | W | U | U |
        // 5 | + | + | + | + | W | U |
        // 6 | + | + | + | + | + | W |
        List<Result> newCur = new ArrayList<Result>();
        for (Result cur : solution.current) {
            for (int l=1; l<cur.steps.length; l++) {
                for (int r=0; r<l; r++) {
                    // Loop through the 4 possible operations
                    for (int o=0; o<4; o++) {
                        Result res = Calculate(cur.steps, l, r, Operation.values()[o]);
                        if (res != null) {
                            res.text = cur.text + res.text + String.format("%n");

                            if (Math.abs(res.value - solution.tirage) < Math.abs(solution.best.value - solution.tirage)) {
                                solution.best = res;
                            }

                            // Found the exact solution: terminal return
                            if (solution.best.value == solution.tirage) {
                                return solution;
                            }
                            else {
                                newCur.add(res);
                            }
                        }
                    }
                }
            }
        }

        // Update the list of possibilities with the new calculated ones
        solution.current = newCur;
        // Decrease depth of next level of steps
        solution.depth--;

        // Recursive call for next level
        return SolveTirage(solution);
    }

    private enum Operation {
        Add,
        Multiply,
        Substract,
        Divide
    }

    private Result Calculate(int[] steps, int l, int r, Operation op) {
        Result result = new Result();
        // Allocate a new set of steps that is 1 step smaller
        result.steps = new int[steps.length-1];
        // Copy the non modified steps (not in calculation)
        int n = 0;
        for (int i=0; i<steps.length; i++) {
            if (i != l && i != r) {
                result.steps[n] = steps[i];
                n++;
            }
        }
        // Perform the calculation, if it has a sense
        // Senseful returns are done inside of the switch, useless null return done at the end of the method
        switch(op) {
            case Add: {
                result.steps[n] = steps[l] + steps[r];
                result.value = result.steps[n];
                result.text = String.format("%d + %d = %d%n", steps[l], steps[r], result.value);
                Arrays.sort(result.steps);
                return result;
            }
            case Multiply: {
                if (steps[r] != 1) {
                    result.steps[n] = steps[l] * steps[r];
                    result.value = result.steps[n];
                    result.text = String.format("%d x %d = %d%n", steps[l], steps[r], result.value);
                    Arrays.sort(result.steps);
                    return result;
                }
                // A multiply by 1 is no help at all: skip this possibility
                break;
            }
            case Substract: {
                if (steps[l] > steps[r]) {
                    result.steps[n] = steps[l] - steps[r];
                    result.value = result.steps[n];
                    result.text = String.format("%d - %d = %d%n", steps[l], steps[r], result.value);
                    Arrays.sort(result.steps);
                    return result;
                }
                // A substract that leads to a value of 0 is no help at all: skip this possibility
                break;
            }
            case Divide: {
                if (steps[r] != 1 && steps[l] % steps[r] == 0) {
                    result.steps[n] = steps[l] / steps[r];
                    result.value = result.steps[n];
                    result.text = String.format("%d / %d = %d%n", steps[l], steps[r], result.value);
                    Arrays.sort(result.steps);
                    return result;
                }
                // A divide that is not entire is not possible
                // A divide by 1 is no help at all: skip these possibilities
                break;
            }
        }
        // When here, means that calculation is not possible or leads to nothing helpful
        return null;
    }
}
