package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;


import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author nishuang
 */
public final class Main {

    /**
     * Process a sequence of encryptions and decryptions, as
     * specified by ARGS, where 1 <= ARGS.length <= 3.
     * ARGS[0] is the name of a configuration file.
     * ARGS[1] is optional; when present, it names an input file
     * containing messages.  Otherwise, input comes from the standard
     * input.  ARGS[2] is optional; when present, it names an output
     * file for processed messages.  Otherwise, output goes to the
     * standard output. Exits normally if there are no errors in the input;
     * otherwise with code 1.
     */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /**
     * Check ARGS and open the necessary files (see comment on main).
     */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /**
     * Return a Scanner reading from the file named NAME.
     */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /**
     * Return a PrintStream writing to the file named NAME.
     */
    private PrintStream getOutput(String name) {
        try {
            File out = new File(name);
            return new PrintStream(out);
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /**
     * Configure an Enigma machine from the contents of configuration
     * file _config and apply it to the messages in _input, sending the
     * results to _output.
     */
    private void process() {
        Machine made = readConfig();
        while (_input.hasNextLine()) {
            if (!_input.hasNext("[*]")) {
                throw new EnigmaException("Setting line invalid.");
            } else {
                String put = _input.nextLine();
                if (put.contains("*")) {
                    setUp(made, put);
                } else if (put.length() == 0) {
                    _output.println();
                }
                while (!_input.hasNext("[*]") && _input.hasNextLine()) {
                    String s = _input.nextLine();
                    if (!s.contains("*")) {
                        printMessageLine(made.convert(s));
                    }
                }

            }
        }

    }



    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alpha = _config.nextLine();
            if (alpha.contains("-")) {
                int dash = alpha.indexOf("-");
                char a = alpha.charAt(dash - 1);
                char b = alpha.charAt(dash + 1);
                _alphabet = new CharacterRange(a, b);
            } else {
                _alphabet = new FreeCharacter(alpha);
            }
            String numset = _config.nextLine();
            String numrotors1 = numset.replaceAll("\\s+", "");
            int numtotalrotors = 0;
            int numpawls = 0;
            try {
                String inp = String.valueOf(numrotors1.charAt(0));
                numtotalrotors = Integer.parseInt(inp);
                String inp2 = String.valueOf(numrotors1.charAt(1));
                numpawls = Integer.parseInt(inp2);
            } catch (NumberFormatException excp) {
                throw error("missing number of rotors");
            }
            ArrayList<Rotor> allrotors = new ArrayList<>();
            while (_config.hasNextLine()) {
                String rotoline = _config.nextLine();
                if (!rotoline.isEmpty()) {
                    allrotors.add(readRotor(rotoline));
                }
            }
            return new Machine(_alphabet, numtotalrotors, numpawls, allrotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config.
     * @param roto The input is checked above.*/
    private Rotor readRotor(String roto) {
        try {
            String front = roto.substring(0, roto.indexOf("("));
            String back = roto.substring(roto.indexOf("("));
            if (_alphabet.getClass() == CharacterRange.class) {
                String che = back.replaceAll("\\s+", "");
                if (!che.matches("([\\\\(][a-zA-Z]+[\\\\)])*")) {
                    throw new EnigmaException("Wrong permutation cycles.");
                }
            }
            Permutation perm = new Permutation(back, _alphabet);
            String[] properties = front.split("\\s+");
            int fuuz = 0;
            while (fuuz < properties.length) {
                if (!properties[fuuz].isEmpty()) {
                    break;
                } else {
                    fuuz += 1;
                }
            }
            String name = properties[fuuz].toUpperCase();
            String type = properties[fuuz + 1].toUpperCase();
            Rotor one = new Rotor(name, perm);
            if (type.contains("M")) {
                one = new MovingRotor(name, perm, type.substring(1));
            } else if (type.contains("N")) {
                one = new FixedRotor(name, perm);
            } else if (type.contains("R")) {
                while (!perm.derangement()) {
                    back = back.concat(_config.nextLine());
                    perm = new Permutation(back, _alphabet);
                }
                one = new Reflector(name, perm);
            }
            return one;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Return the rotors given the parsed.
     * @param parsed This is the input given in setUp.
     * Helper function for setUp. */
    private String[] getrotors(String[] parsed) {
        String[] onlyrotors = new String[parsed.length - 2];
        for (int i = 0; i < parsed.length - 2; i += 1) {
            onlyrotors[i] = parsed[i + 1];
        }
        for (int w = 0; w < onlyrotors.length - 1; w += 1) {
            for (int t = 0; t < onlyrotors.length - 1; t += 1) {
                if (t != w) {
                    if (onlyrotors[t].equals(onlyrotors[w])) {
                        throw new EnigmaException("Duplicate rotors.");
                    }
                }
            }
        }
        return onlyrotors;
    }

    /** Set M according to the specification given on SETTINGS,
     * which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        if (settings.contains("(")) {
            int ind = settings.indexOf("(");
            String left = settings.substring(0, ind);
            String right = settings.substring(ind);
            String[] parsed = left.split("\\s+");
            if (parsed.length - 1 < M.numRotors()) {
                throw new EnigmaException("Not enough input rotor.");
            } else {
                M.insertRotors(getrotors(parsed));
                if (M.checkRotorslots()) {
                    String initial = parsed[parsed.length - 1];
                    for (int i = 0; i < initial.length(); i += 1) {
                        if (!_alphabet.contains(initial.charAt(i))) {
                            throw new EnigmaException("Initial Pos invalid.");
                        }
                    }
                    M.setRotors(initial);
                    Permutation permboard = new Permutation(right, _alphabet);
                    M.setPlugboard(permboard);
                }
            }
        } else {
            String[] parsed = settings.split("\\s+");
            String[] onlyrotors = new String[parsed.length - 1];
            for (int i = 0; i < parsed.length - 2; i += 1) {
                onlyrotors[i] = parsed[i + 1];
            }
            M.insertRotors(getrotors(parsed));
            if (M.checkRotorslots()) {
                String initial = parsed[parsed.length - 1];
                for (int i = 0; i < initial.length(); i += 1) {
                    if (!_alphabet.contains(initial.charAt(i))) {
                        throw new EnigmaException("Initial Position invalid.");
                    }
                }
                M.setRotors(initial);
            }
        }
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String output = "";
        int breakpoint = 0;
        for (int i = 1; i <= msg.length(); i += 1) {
            if (i % 5 == 0) {
                if (output.length() == 0) {
                    output += msg.substring(breakpoint, i);
                } else {
                    output += " " + msg.substring(breakpoint, i);
                }
                breakpoint = i;
            } else if (msg.length() - breakpoint <= 4) {
                if (output.length() == 0) {
                    output += msg.substring(breakpoint);
                } else {
                    output += " " + msg.substring(breakpoint);
                }
                break;
            }
        }
        _output.println(output);
    }


    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

}
