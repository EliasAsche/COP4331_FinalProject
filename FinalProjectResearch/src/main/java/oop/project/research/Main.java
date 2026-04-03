package oop.project.research;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import oop.project.research.scenarios.Scenarios;

import java.util.Scanner;

public class Main {

    static void main() {
        var scanner = new Scanner(System.in);
        while (true) {
            var input = scanner.nextLine();
            try {
                var args = Scenarios.parse(input);
                System.out.println(args);
            } catch (ArgumentParserException e) {
                e.getParser().handleError(e);
            } catch (Exception e) {
                System.out.println("Unexpected " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }

}
