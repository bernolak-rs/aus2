import tester.TreeTester;

import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public final class TesterCli {
    private long seed = 42L;
    private int seedCount = 1;
    private int pointCount = 10_100;
    private int coordinateLimit = 1_000_000;
    private int checkEvery = 100;
    private int maxDepth = 20;
    private String tree = "both";

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

    private boolean handleCommand(String line) {
        String[] parts = line.split(" ");
        String command = parts[0].toLowerCase();
        if (Set.of("run", "show", "help", "exit").contains(command) && parts.length != 1) {
            throw new IllegalArgumentException("Prikaz " + command + " nema argumenty");
        }

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
                if (!Set.of("kd", "quad", "both").contains(value)) {
                    throw new IllegalArgumentException("Vyber strom: kd, quad alebo both");
                }
                this.tree = value;
                break;
            }
            case "run":
                this.runTests();
                return true;
            case "show":
                this.printSettings();
                return true;
            case "help":
                TesterCli.printHelp();
                return true;
            case "exit":
                return false;
            default:
                throw new IllegalArgumentException("Neznamy prikaz. Pouzi help");
        }

        this.printSettings();
        return true;
    }

    private static String argument(String[] parts) {
        if (parts.length != 2) {
            throw new IllegalArgumentException("Pouzitie: " + parts[0] + " <hodnota>");
        }
        return parts[1];
    }

    private static int integerArgument(String[] parts, int minimum) {
        int value = Integer.parseInt(TesterCli.argument(parts));
        if (value < minimum) {
            throw new IllegalArgumentException("Hodnota pre " + parts[0] + " musi byt aspon " + minimum);
        }
        return value;
    }

    private void printSettings() {
        System.out.println("seed=" + this.seed
                + ", seeds=" + this.seedCount
                + ", size=" + this.pointCount
                + ", check=" + this.checkEvery
                + ", range=" + this.coordinateLimit
                + ", depth=" + this.maxDepth
                + ", tree=" + this.tree);
    }

    private static void printHelp() {
        System.out.println("""
            run                 Spusti test s aktuálnymi nastaveniami
            seed N|random       Nastav počiatočný seed alebo vyber náhodný
            seeds N             Počet po sebe idúcich seedov (N >= 1, predvolene 1)
            size N              Počet unikátnych bodov (N >= 0)
            check N             Kontrola každých N vložení (N >= 1)
            range N             Súradnice od 0 do N-1 (N >= 2)
            depth N             Maximálna hĺbka QuadTree (N >= 0)
            tree kd|quad|both   Vyber testované stromy
            show                Zobraz nastavenia
            help                Zobraz príkazy
            exit                Ukonči tester
        """);
    }

    private void runTests() {
        this.printSettings();
        for (TreeTester.Result result : new TreeTester().run(
                this.seed, this.seedCount, this.pointCount, this.coordinateLimit,
                this.checkEvery, this.maxDepth, this.tree)) {
            if (result.error() == null) {
                System.out.println(result.name() + " OK: seed=" + result.seed() + ", size=" + result.size());
            } else {
                System.out.println(result.name() + " ZLYHAL: seed=" + result.seed() + ", " + result.error().getMessage());
                for (Throwable cause = result.error().getCause(); cause != null; cause = cause.getCause()) {
                    System.out.println("  " + cause);
                }
            }
        }
    }
}
