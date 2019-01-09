package enigma;

import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;


/** Class that represents a complete enigma machine.
 *  @author nishuang
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allr = allRotors;
        for (Rotor object : _allr) {
            String name = object.name();
            _allrotors.put(name, object);
        }
        _myrotorslots = new ArrayList<Rotor>(numRotors());
        _plugboard = new Permutation("", new CharacterRange('A', 'Z'));
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        if (rotors.length != numRotors()) {
            throw new EnigmaException("Wrong num of rotors.");
        }
        _myrotorslots = new ArrayList<Rotor>(numRotors());
        for (int i = 0; i < numRotors(); i += 1) {
            if (!_allrotors.keySet().contains(rotors[i])) {
                throw new EnigmaException("Invalid rotor name.");
            }
            Rotor x = _allrotors.get(rotors[i]);
            _myrotorslots.add(i, x);
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 upper-case letters. The first letter refers to the
     *  leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 0; i < setting.length(); i += 1) {
            _myrotorslots.get(i + 1).set(setting.charAt(i));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Advance the whole machine after one click.
     * Update the setting of the rotors. */

    void advanceMachine() {
        int q = numRotors() - 1;
        HashSet<Rotor> advanced = new HashSet<>();
        for (Rotor object : _myrotorslots.subList(1, q + 1)) {
            if (object.atNotch()) {
                int ind = _myrotorslots.indexOf(object);
                if (_myrotorslots.get(ind - 1).rotates()) {
                    if (!advanced.contains(_myrotorslots.get(ind - 1))) {
                        _myrotorslots.get(ind - 1).advance();
                        advanced.add(_myrotorslots.get(ind - 1));
                    }
                    if (_myrotorslots.indexOf(object) != q) {
                        if (!advanced.contains(object)) {
                            object.advance();
                            advanced.add(object);
                        }
                    }
                }

            }
        }
        _myrotorslots.get(q).advance();
    }

    /** Returns the private arraylist of rotorslots. */
    Boolean checkRotorslots() {
        int cat = 0;
        if (!_myrotorslots.get(0).reflecting()) {
            throw new EnigmaException("The first shld be a reflector.");
        }
        for (Rotor puppy : _myrotorslots) {
            if (puppy.rotates()) {
                cat += 1;
            }
        }
        if (cat != numPawls()) {
            throw new EnigmaException("Wrong num of pawls.");
        }
        return true;
    }



    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceMachine();
        int a = _plugboard.permute(c);
        int i = 1;
        int intermediate = a;
        while (i <= numRotors()) {
            int use = numRotors() - i;
            int b = _myrotorslots.get(use).convertForward(intermediate);
            intermediate = b;
            i += 1;
        }
        int revertintermediate = intermediate;
        int j = 1;
        while (j < numRotors()) {
            int d = _myrotorslots.get(j).convertBackward(revertintermediate);
            revertintermediate = d;
            j += 1;
        }
        return _plugboard.permute(revertintermediate);
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        msg = msg.toUpperCase();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < msg.length(); i += 1) {
            if (Character.isWhitespace(msg.charAt(i))) {
                continue;
            } else {
                int c = _alphabet.toInt(msg.charAt(i));
                int r = convert(c);
                result.append(_alphabet.toChar(r));
            }
        }
        return result.toString();
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** Number of rotors the machine has. */
    private int _numRotors;
    /** Number of pawls/moving rotors the machine has. */
    private int _pawls;
    /** A mapping of rotor's name to rotor itself. */
    private HashMap<String, Rotor> _allrotors = new HashMap<>();
    /** A collection of rotorslots. */
    private ArrayList<Rotor> _myrotorslots;
    /** The permutation of plugboard.
     * if not set, the default is every char
     * mapping to itself. */
    private Permutation _plugboard;
    /** A collection of rotors. */
    private Collection<Rotor> _allr;
}
