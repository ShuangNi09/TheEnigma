package enigma;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;



/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author nishuang
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        int size = _alphabet.size();
        for (int i = 0; i < size; i += 1) {
            _permutation.put(alphabet().toChar(i), alphabet().toChar(i));
        }
        int j = 0;
        char init = 'a';
        while (j < cycles.length() - 1) {
            if (cycles.charAt(j) == '(') {
                init = cycles.charAt(j + 1);
                j += 1;
            } else if (cycles.charAt(j + 1) == ')') {
                _permutation.put(cycles.charAt(j), init);
                j += 2;
            } else {
                _permutation.put(cycles.charAt(j), cycles.charAt(j + 1));
                j += 1;
            }
        }

    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        for (int y = 0; y < cycle.length(); y += 1) {
            char c = cycle.toCharArray()[y];
            if (y == cycle.length() - 1) {
                char c1 = cycle.toCharArray()[0];
                _permutation.put(c, c1);
            } else {
                char c2 = cycle.toCharArray()[y + 1];
                _permutation.put(c, c2);
            }
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return alphabet().size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int m = wrap(p);
        char beforeper = alphabet().toChar(m);
        char afterper = _permutation.get(beforeper);
        return alphabet().toInt(afterper);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char beforepr = alphabet().toChar(c);
        char afterpr = getKeyByValue(_permutation, beforepr);
        return alphabet().toInt(afterpr);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return _permutation.get(p);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return getKeyByValue(_permutation, c);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }


    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (Character key : _permutation.keySet()) {
            if (key == _permutation.get(key)) {
                return false;
            }
        } return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Storage of the permutation mappings. */
    private HashMap<Character, Character> _permutation = new HashMap<>();

    /** Helper function for using key to get value in hashmap.
     * @return the key given the value of the hashmap.
     * @param <T> this is the type parameter.
     * @param <E> this is another type parameter.
     * @param map this is the map given by T and E.
     * @param value this is the input value.
     *  */
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

}
