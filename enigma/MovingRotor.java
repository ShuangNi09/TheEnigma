package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author nishuang
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    private String _notches;

    /** Initiate a moving rotor with notches.
     * @param name the input initiation name.
     * @param perm the input permuation.
     * @param notches the set notches. */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    @Override
    boolean atNotch() {
        return _notches.indexOf(alphabet().toChar(setting())) != -1;
    }

    @Override
    boolean rotates() {
        return true;
    }

}
