package enigma;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/** The suite of all JUnit tests for the Machine class.
 *  @Shuang
 */
public class MachineTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */
    private Alphabet _upper = new CharacterRange('A', 'Z');
    private Collection<Rotor> allrotors = new ArrayList<>();
    private String _cycle1 = "(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)";
    private Permutation perm1 = new Permutation(_cycle1, _upper);
    private Rotor _i = new MovingRotor("I", perm1, "Q");
    private String _cycle2 = "(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)";
    private Permutation perm2 = new Permutation(_cycle2, _upper);
    private Rotor _iii = new MovingRotor("III", perm2, "V");
    private String _cycle3 = "(AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)";
    private Permutation perm3 = new Permutation(_cycle3, _upper);
    private Rotor _iv = new MovingRotor("IV", perm3, "J");
    private String _cycle4 = "(ALBEVFCYODJWUGNMQTZSKPR) (HIX)";
    private Permutation perm4 = new Permutation(_cycle4, _upper);
    private Rotor _beta = new FixedRotor("BETA", perm4);
    private String _cycle5 = "(AE) (BN) (CK) (DQ) (FU)";
    private String _cycle6 = "(GY) (HW) (IJ) (LO) (MP) (RX) (SZ) (TV)";
    private Permutation perm5 = new Permutation(_cycle5 + _cycle6, _upper);
    private Rotor _b = new Reflector("B", perm5);

    /** Check that the machine correctly convert a string of message. */

    @Test
    public void checkconvert() {
        String[] rotors = new String[5];
        rotors[0] = "B";
        rotors[1] = "BETA";
        rotors[2] = "III";
        rotors[3] = "IV";
        rotors[4] = "I";
        allrotors.add(_i);
        allrotors.add(_iii);
        allrotors.add(_iv);
        allrotors.add(_beta);
        allrotors.add(_b);
        Machine test = new Machine(_upper, 5, 3, allrotors);
        test.insertRotors(rotors);
        test.setRotors("AXLE");
        String plug = "(HQ) (EX) (IP) (TR) (BY)";
        test.setPlugboard(new Permutation(plug, _upper));
        String expected = "QVPQSOKOILPUBKJZPISFXDWBHCNSCXNUOAATZXSRCFYDGU";
        String tobeconvert = "FROMHI SSHOULDERHIAWATHATOOKTHECAMERAOFROSEWOOD";
        String converted = test.convert(tobeconvert);
        assertEquals(expected, converted);
    }

    @Test
    public void checkconvertback() {
        String[] rotors = new String[5];
        rotors[0] = "B";
        rotors[1] = "BETA";
        rotors[2] = "III";
        rotors[3] = "IV";
        rotors[4] = "I";
        allrotors.add(_i);
        allrotors.add(_iii);
        allrotors.add(_iv);
        allrotors.add(_beta);
        allrotors.add(_b);
        Machine test = new Machine(_upper, 5, 3, allrotors);
        test.insertRotors(rotors);
        test.setRotors("AXLE");
        String plug = "(HQ) (EX) (IP) (TR) (BY)";
        test.setPlugboard(new Permutation(plug, _upper));
        String expected = "FROMHISSHOULDERHIAWATHATOOKTHECAMERAOFROSEWOOD";
        String tobeconvert = "QVPQSOKOILPUBKJZPISFXDWBHCNSCXNU OAATZXSRCFYDGU";
        String converted = test.convert(tobeconvert);
        assertEquals(expected, converted);
    }


}
