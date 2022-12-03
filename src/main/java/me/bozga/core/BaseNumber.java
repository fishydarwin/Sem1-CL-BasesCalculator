package me.bozga.core;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a positive number with a base less than 10.
 */
public class BaseNumber {
    
    private byte base;
    private boolean negative;
    private List<Character> value;
    private Map<Character, Integer> additionalValueMapping;

    public static final Map<Character, Integer> NO_MAP = new HashMap<>();
    public static final Map<Character, Integer> BASE_16_MAP = 
        Stream.of(
        new AbstractMap.SimpleImmutableEntry<>('a', 10),    
        new AbstractMap.SimpleImmutableEntry<>('b', 11),    
        new AbstractMap.SimpleImmutableEntry<>('c', 12),    
        new AbstractMap.SimpleImmutableEntry<>('d', 13),    
        new AbstractMap.SimpleImmutableEntry<>('e', 14),    
        new AbstractMap.SimpleImmutableEntry<>('f', 15))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    public BaseNumber(byte base, boolean negative, String value, Map<Character, Integer> additionalValueMapping) 
                                                                                    throws IllegalArgumentException 
        {
        this.base = base;
        this.negative = negative;
        this.value = new ArrayList<>();
        this.additionalValueMapping = additionalValueMapping;
        value = value.toLowerCase();
        for (int i = value.length() - 1; i >= 0; i--) {
            char c = value.charAt(i);
            if (!"0123456789".contains("" + c)) { // hack to check if already a digit
                if (!additionalValueMapping.containsKey(c)) { // uh oh
                    throw new IllegalArgumentException("Provided invalid digit in base " + base); 
                }
            } else {
                if (Integer.parseInt("" + c) >= base) {
                    throw new IllegalArgumentException("Provided invalid digit in base " + base); 
                }
            }
            this.value.add(c);
        }
    }

    public byte getBase() { return base; }
    public boolean isNegative() { return negative; }
    public List<Character> getValue() { return value; }
    public Map<Character, Integer> getAdditionalValueMapping() { return additionalValueMapping; }

    /**
     * Adds a digit at the specified position - if the position is greater, will fill with 0.
     * @param index the index to add at
     * @param digitRepresentation the digit representation to add
     */
    public void addDigitAt(int index, char digitRepresentation) {
        while (index >= value.size()) { value.add('0'); }
        value.set(index, digitRepresentation);
    }

    /**
     * Removes the last digit - the last digit in this case means the first digit in base representation.
     */
    public void removeLastDigit() {
        value.remove(value.size() - 1);
    }

    /**
     * Gets the absolute value of a digit as base 10 number at index i.
     * @param index the index to get from
     * @return the value as a base 10 number
     */
    public final int digitValueAt(int index) {
        if ("0123456789".contains("" + value.get(index))) { return Integer.parseInt("" + value.get(index)); }
        else { return additionalValueMapping.get(value.get(index)); }
    }

    /**
     * Returns the associated character based on the provided digit
     * @param digit the provided digit
     * @return the associated character
     */
    public final char getAssociatedCharacter(int digit) {
        if (digit >= 0 && digit <= 9) { return ((Integer) digit).toString().charAt(0); }
        else {
            for (char associatedCharacter : additionalValueMapping.keySet()) {
                if (additionalValueMapping.get(associatedCharacter) == digit) {
                    return associatedCharacter;
                }
            }
        }
        return '\0';
    }

    /**
     * Compares this number with another BaseNumber n.
     * @param n the number to compare against
     * @return -1 if less than, 0 if equal, 1 if greater than
     */
    public byte compare(BaseNumber n) {
        if (this.value.size() > n.getValue().size()) { return 1; }
        else if (this.value.size() < n.getValue().size()) { return -1; }
        else {
            for (int i = this.value.size() - 1; i >= 0; i--) {
                if (this.digitValueAt(i) > n.digitValueAt(i)) { return 1; }
                else if (this.digitValueAt(i) < n.digitValueAt(i)) { return -1; }
            }
            return 0;
        }
    }

    /**
     * Outputs a readable version of this number.
     */
    @Override
    public String toString() {
        String result = "";
        for (int i = value.size() - 1; i >= 0; i--) { result += value.get(i); }
        return negative ? "-" + result : result;
    }

    /**
     * Converts this number from base p to base 10 using substitution.
     * @return the converted number
     */
    public final int convertToBaseTen() {
        int result = 0;
        int power = 1;

        for (char digitRepresentation : value) {
            int digit = 0;
            if ("0123456789".contains("" + digitRepresentation)) { digit = Integer.parseInt("" + digitRepresentation); }
            else { digit = additionalValueMapping.get(digitRepresentation); }
            result += digit * power;
            power *= base;
        }

        return negative ? -result : result;
    }

}
