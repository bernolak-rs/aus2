import tester.TreeTester;

import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 * AI DOKUMENTACIA 3.1
 * 
 * Used to test tree structures easily with CLI
 * Allows to change parameters of tests without the need to change source code
 */
public final class TesterCli {
    private long seed = 42L;
    private int seedCount = 1;
    private int pointCount = 10_100;
    private int areaCount = 1_000;
    private int coordinateLimit = 1_000_000;
    private int checkEvery = 100000;
    private int maxDepth = 20;
    private String tree = "both";
    private boolean useData = false;

    /**
     * AI DOKUMENTACIA 3.1
     * 
     * Starts CLI application and displays options
     */
    public void start() {
        System.out.println("Tester stromov");
        TesterCli.printHelp();
        this.printSettings();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                System.out.flush();
                if (!scanner.hasNextLine()) {
                    return;
                }

                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                try {
                    if (!this.handleCommand(line)) {
                        return;
                    }
                } catch (NumberFormatException error) {
                    System.out.println("Error: Zadaj cele cislo");
                } catch (IllegalArgumentException error) {
                    System.out.println("Error: " + error.getMessage());
                }
            }
        }
    }

    /**
     * AI DOKUMENTACIA 3.1, 3.3
     * 
     * Handles possible commands received from user input
     * @param line user input
     * @return return true to continue testing and false to exit
     */
    private boolean handleCommand(String line) {
        String[] parts = line.split(" ");
        String command = parts[0].toLowerCase();

        switch (command) {
            case "seed": {
                String value = TesterCli.argument(parts).toLowerCase();
                this.seed = value.equals("random")
                        ? new Random().nextLong() : Long.parseLong(value);
                break;
            }
            case "seeds":
                this.seedCount = TesterCli.integerArgument(parts, 1);
                break;
            case "size":
                this.pointCount = TesterCli.integerArgument(parts, 0);
                break;
            case "rectangles":
                this.areaCount = TesterCli.integerArgument(parts, 0);
                break;
            case "check":
                this.checkEvery = TesterCli.integerArgument(parts, 1);
                break;
            case "range":
                this.coordinateLimit = TesterCli.integerArgument(parts, 2);
                break;
            case "depth":
                this.maxDepth = TesterCli.integerArgument(parts, 0);
                break;
            case "tree": {
                String value = TesterCli.argument(parts).toLowerCase();
                if (!Set.of("kd", "quad", "quadkd", "danysk", "jts", "mimicat13",
                        "morkev", "plamenabrezova", "mgruben", "both", "all").contains(value)) {
                    throw new IllegalArgumentException("Vyber strom: kd, quad, quadkd, danysk, jts, "
                            + "mimicat13, morkev, plamenabrezova, mgruben, both alebo all");
                }
                this.tree = value;
                break;
            }
            case "data": {
                String value = TesterCli.argument(parts).toLowerCase();
                if (!Set.of("on", "off").contains(value)) {
                    throw new IllegalArgumentException("Pouzitie: data on|off");
                }
                this.useData = value.equals("on");
                break;
            }
            case "run":
                this.runTests();
                return true;
            case "show":
                this.printSettings();
                return true;
            case "help":
            case "h":
            case "?":
                TesterCli.printHelp();
                return true;
            case "exit":
            case "q":
            case "quit":
                return false;
            default:
                throw new IllegalArgumentException("Neznamy prikaz - Pouzi help pre vypisanie prikazov");
        }

        this.printSettings();
        return true;
    }

    /**
     * AI DOKUMENTACIA 3.1
     * 
     * Handles string argument of a command
     * @param parts
     * @return argument passed with command
     */
    private static String argument(String[] parts) {
        if (parts.length != 2) {
            throw new IllegalArgumentException("Pouzitie: " + parts[0] + " <hodnota>");
        }
        return parts[1];
    }

    /**
     * AI DOKUMENTACIA 3.1
     * 
     * Parses integer arguments
     * @param parts
     * @param minimum
     * @return validated integer argument
     */
    private static int integerArgument(String[] parts, int minimum) {
        int value = Integer.parseInt(TesterCli.argument(parts));
        if (value < minimum) {
            throw new IllegalArgumentException("Hodnota pre " + parts[0] + " musi byt aspon " + minimum);
        }
        return value;
    }

    /**
     * AI DOKUMENTACIA 3.1
     * 
     * Prints current config used for test
     */
    private void printSettings() {
        System.out.println("seed=" + this.seed
                + ", seeds=" + this.seedCount
                + ", size=" + this.pointCount
                + ", rectangles=" + this.areaCount
                + ", check=" + this.checkEvery
                + ", range=" + this.coordinateLimit
                + ", depth=" + this.maxDepth
                + ", tree=" + this.tree
                + ", data=" + (this.useData ? "on" : "off"));
    }

    /**
     * AI DOKUMENTACIA 3.1, 3.3
     * 
     * Prints command options
     */
    private static void printHelp() {
        System.out.println("""
            run                 Spusti test s aktuálnymi nastaveniami
            data on|off         CSV body a intervalové find alebo generované body
            seed N|random       Nastav počiatočný seed alebo vyber náhodný
            seeds N             Počet po sebe idúcich seedov (N >= 1, predvolene 1)
            size N              Počet bodov (N >= 0)
            rectangles N         Počet obdĺžnikov (N >= 0)
            check N             Kontrola každých N vložení (N >= 1)
            range N             Súradnice od 0 do N-1 (N >= 2)
            depth N             Maximálna hĺbka QuadTree a QuadKDTree (N >= 0)
            tree NAZOV          kd, quad, quadkd, danysk, jts, mimicat13,
                                morkev, plamenabrezova, mgruben
            tree both|all       Pôvodné dva stromy alebo všetkých deväť
            show                Zobraz nastavenia
            help|h|?            Zobraz príkazy
            exit|q|quit         Ukonči tester
        """);
    }

    /**
     * AI DOKUMENTACIA 3.1
     * 
     * Runs test with current configuration
     * Creates Tree Tester and runs selected tests. Prints results of test and structure size or error message
     */
    private void runTests() {
        this.printSettings();

        TreeTester tester = new TreeTester(
                this.seed, this.seedCount, this.pointCount, this.areaCount, this.coordinateLimit,
                this.checkEvery, this.maxDepth, this.tree, this.useData);

        for (TreeTester.Result result : tester.run()) {
            if (result.error() == null) {
                String source = this.useData ? "CSV" : "seed= " + result.seed();
                System.out.println(result.name() + " OK: " + source + ", size= " + result.size());
            } else {
                String source = this.useData ? "CSV" : "seed= " + result.seed();
                System.out.println(result.name() + " ZLYHAL: " + source + ", " + result.error().getMessage());
            }
        }
    }
}
