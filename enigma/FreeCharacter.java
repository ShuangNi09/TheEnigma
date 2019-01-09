package enigma;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static enigma.EnigmaException.*;

/** An Alphabet consisting of the input
 *  order.
 *  @author nishuang
 */
class FreeCharacter extends Alphabet {

    /**
     * An alphabet consisting of all characters in the string
     * of input inclusive.
     * @param input This is the input passed in the main.
     */
    FreeCharacter(String input) {
        _input = input.toUpperCase();
        for (int i = 0; i < _input.length(); i += 1) {
            for (int j = 0; j < _input.length(); j += 1) {
                if (i != j && _input.charAt(i) == _input.charAt(j)) {
                    throw new EnigmaException("Wrong Alphabet input.");
                } else if (_input.charAt(i) == '(') {
                    throw new EnigmaException("Forbidden Special Char.");
                } else if (_input.charAt(i) == ')') {
                    throw new EnigmaException("Forbidden Special Char.");
                } else if (_input.charAt(i) == '*') {
                    throw new EnigmaException("Forbidden Special Char.");
                }
            }
        }
        for (int p = 0; p < _input.length(); p += 1) {
            _random.put(_input.charAt(p), p);
        }

    }
    @Override
    int size() {
        return _input.length();
    }

    @Override
    boolean contains(char ch) {
        final boolean check = _input.indexOf(ch) != -1;
        return check;
    }

    @Override
    char toChar(int index) {
        if (index < 0 || index >= size()) {
            throw error("character index out of range");
        } else {
            return getKeyByValue(_random, index);
        }
    }

    @Override
    int toInt(char ch) {
        if (!contains(ch)) {
            throw error("character out of range");
        }
        return _random.get(ch);
    }

    /** Helper function for using key to get value in hashmap.
     * @return the key given the value of the hashmap.
     * @param <T> this is the type parameter.
     * @param <E> this is another type parameter.
     * @param map this is the map given by T and E.
     * @param value this is the input value.
     *  */
    private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }


    /** Input of characters in this Alphabet. */
    private String _input;

    /** Use A as the anchor for the character. */
    private HashMap<Character, Integer> _random = new HashMap<>();
}
