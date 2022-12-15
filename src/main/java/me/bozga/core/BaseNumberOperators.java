package me.bozga.core;

/**
 * Operates on numbers represented by BaseNumber.
 */
public class BaseNumberOperators {

    /**
     * Adds two BaseNumbers together.
     * @param a first number
     * @param b second number
     * @return The sum of a + b in their base
     * @throws IllegalArgumentException If base of a is different from base of b
     */
    public static BaseNumber add(BaseNumber a, BaseNumber b) throws IllegalArgumentException {
        if (a.getBase() != b.getBase()) { throw new IllegalArgumentException("Cannot add two different bases."); }

        // initializers
        BaseNumber result = new BaseNumber(a.getBase(), false, "0", a.getAdditionalValueMapping());
        byte base = a.getBase();
        int carry = 0;

        // figure out order
        int additions;
        BaseNumber largerNumber;
        if (a.compare(b) > 0) {
            additions = a.getValue().size();
            largerNumber = a;
            while (b.getValue().size() != a.getValue().size()) {
                b.addDigitAt(b.getValue().size(), '0');
            }
        } else {
            additions = b.getValue().size();
            largerNumber = b;
            while (a.getValue().size() != b.getValue().size()) {
                a.addDigitAt(a.getValue().size(), '0');
            }
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
            int pushDigit = 0;
            if (i < largerNumber.getValue().size()) { pushDigit = largerNumber.digitValueAt(i); }
            result.addDigitAt(i, result.getAssociatedCharacter(carry + pushDigit));
            i++;
        }
        // while (i < largerNumber.getValue().size()) {
        //     result.addDigitAt(i, largerNumber.getValue().get(i));
        //     i++;
        // }

        System.out.println(a.toString() + " + " + b.toString() + " == " + result.toString());

        return result;
    }
    
    /**
     * Subtracts two BaseNumbers.
     * @param a first number
     * @param b second number
     * @return The subtraction of a - b in their base
     * @throws IllegalArgumentException If base of a is different from base of b
     */
    public static BaseNumber sub(BaseNumber a, BaseNumber b) throws IllegalArgumentException {
        if (a.getBase() != b.getBase()) { throw new IllegalArgumentException("Cannot subtract two different bases."); }

        // initializers
        BaseNumber result = new BaseNumber(a.getBase(), a.compare(b) == -1, "0", a.getAdditionalValueMapping());
        byte base = a.getBase();
        int carry = 0;

        // figure out order and fill small one with zeros
        int subtractions;
        BaseNumber largerNumber;
        BaseNumber smallerNumber;
        if (a.compare(b) > 0) {
            if (b.getValue().size() != a.getValue().size()) { b.addDigitAt(a.getValue().size() - 1, '0'); }
            subtractions = b.getValue().size();
            largerNumber = a; smallerNumber = b;
        } else {
            // fill a with 0s
            if (a.getValue().size() != b.getValue().size()) { a.addDigitAt(b.getValue().size() - 1, '0'); }
            subtractions = a.getValue().size();
            largerNumber = b; smallerNumber = a;
        }

        // main subtraction
        int i;
        for (i = 0; i < subtractions; i++) {
            int firstStep = largerNumber.digitValueAt(i) + carry;
            char resultDigit;
            if (firstStep >= smallerNumber.digitValueAt(i)) { 
                resultDigit = result.getAssociatedCharacter(firstStep - smallerNumber.digitValueAt(i));
                carry = 0;
            } else { 
                resultDigit = result.getAssociatedCharacter(firstStep + base - smallerNumber.digitValueAt(i));
                carry = -1;
            }
            result.addDigitAt(i, resultDigit);
        }

        // fill rest
        while (i < largerNumber.getValue().size()) {
            result.addDigitAt(i, result.getAssociatedCharacter(largerNumber.getValue().get(i) + carry));
            i++;
        }

        // clean up trailing zeros
        for (int j = result.getValue().size() - 1; j > 0; j--) {
            if (result.getValue().get(j) != '0') { break; }
            result.removeLastDigit();
        }

        System.out.println(a.toString() + " - " + b.toString() + " == " + result.toString());

        return result;
    }

    /**
     * Multiplies a BaseNumber a with the digit BaseNumber b
     * @param a first number
     * @param b digit
     * @return The multiplication of a * b in their base
     * @throws IllegalArgumentException If base of a is different from base of b, or if b is not 1 digit only
     */
    public static BaseNumber mulDigit(BaseNumber a, BaseNumber b) throws IllegalArgumentException {
        if (a.getBase() != b.getBase()) { throw new IllegalArgumentException("Cannot multiply two different bases."); }
        if (b.getValue().size() > 1) { throw new IllegalArgumentException("Second number must be 1 digit only."); }

        // initializers
        BaseNumber result = new BaseNumber(a.getBase(), false, "0", a.getAdditionalValueMapping());
        byte base = a.getBase();
        int carry = 0;

        // main addition
        int i;
        for (i = 0; i < a.getValue().size(); i++) {
            int firstStep = a.digitValueAt(i) * b.digitValueAt(0) + carry;
            carry = firstStep / base;
            char resultDigit = result.getAssociatedCharacter(firstStep - carry * base);
            result.addDigitAt(i, resultDigit);
        }

        // fill with carry if needed
        if (carry != 0) {
            result.addDigitAt(i, result.getAssociatedCharacter(carry));
        }

        // clean up trailing zeros
        for (int j = result.getValue().size() - 1; j > 0; j--) {
            if (result.getValue().get(j) != '0') { break; }
            result.removeLastDigit();
        }

        System.out.println(a.toString() + " * " + b.toString() + " == " + result.toString());

        return result;
    }

    /**
     * Divides a BaseNumber a by the digit BaseNumber b
     * @param a first number
     * @param b digit
     * @return The division of a / b in their base
     * @throws IllegalArgumentException If base of a is different from base of b, or if b is not 1 digit only
     * @throws ArithmeticException If division with 0
     */
    public static BaseNumber[] divDigit(BaseNumber a, BaseNumber b) throws IllegalArgumentException, ArithmeticException {
        if (a.getBase() != b.getBase()) { throw new IllegalArgumentException("Cannot divide two different bases."); }
        if (b.getValue().size() > 1) { throw new IllegalArgumentException("Second number must be 1 digit only."); }
        if (b.getValue().get(0) == '0') { throw new ArithmeticException("Error - division by zero."); }

        // initializers
        BaseNumber result = new BaseNumber(a.getBase(), false, "0", a.getAdditionalValueMapping());
        byte base = a.getBase();
        int carry = 0;

        // main addition
        int i;
        for (i = a.getValue().size() - 1; i >= 0; i--) {
            int firstStep = carry * base + a.digitValueAt(i);
            carry = firstStep - firstStep / b.digitValueAt(0) * b.digitValueAt(0);
            char resultDigit = result.getAssociatedCharacter(firstStep / b.digitValueAt(0));
            result.addDigitAt(i, resultDigit);
        }

        // clean up trailing zeros
        for (int j = result.getValue().size() - 1; j > 0; j--) {
            if (result.getValue().get(j) != '0') { break; }
            result.removeLastDigit();
        }

        BaseNumber[] resultArray = { 
            result, 
            new BaseNumber(base, false, "" + result.getAssociatedCharacter(carry), result.getAdditionalValueMapping()) 
        };

        System.out.println(a.toString() + " / " + b.toString() + " == " + resultArray[0].toString() + " r " + resultArray[1]);
        
        return resultArray;
    }

}
