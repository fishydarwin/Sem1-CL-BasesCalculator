package me.bozga.core;

/**
 * Operates on numbers represented by BaseNumber.
 */
public class BaseNumberOperators {

    public static BaseNumber add(BaseNumber a, BaseNumber b) throws IllegalArgumentException {
        if (a.getBase() != b.getBase()) { throw new IllegalArgumentException("Cannot add two different bases."); }

        // initializers
        BaseNumber result = new BaseNumber(a.getBase(), "0", a.getAdditionalValueMapping());
        byte base = a.getBase();
        int carry = 0;

        // figure out order
        int additions;
        BaseNumber largerNumber;
        if (a.getValue().size() > b.getValue().size()) {
            additions = b.getValue().size();
            largerNumber = a;
        } else {
            additions = a.getValue().size();
            largerNumber = b;
        }

        // main addition
        int i;
        for (i = 0; i < additions; i++) {
            int firstStep = a.digitValueAt(i) + b.digitValueAt(i) + carry;
            char resultDigit = result.getAssociatedCharacter(firstStep % base);
            result.addDigitAt(i, resultDigit);
            carry = firstStep / base;
        }

        // fill rest
        if (carry != 0) {
            result.addDigitAt(i, result.getAssociatedCharacter(carry));
            i++;
        }
        while (i < largerNumber.getValue().size()) {
            result.addDigitAt(i, largerNumber.getValue().get(i));
            i++;
        }

        return result;
    }
    
}
