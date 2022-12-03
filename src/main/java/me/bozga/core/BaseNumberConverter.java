package me.bozga.core;

public class BaseNumberConverter {
    
    /**
     * Takes a BaseNumber n and places it into BaseNumber r, assuming r is 0, in r's base.<br>
     * This only works if r's base is smaller than n's.
     * @param n the number to convert
     * @param r the number where to place the conversion
     * @returns r
     * @throws IllegalArgumentException if r's base is greater than n's
     */
    public static BaseNumber convertBySuccessiveDivisions(BaseNumber n, BaseNumber r) {
        if (r.getBase() > n.getBase()) { 
            throw new IllegalArgumentException("Target number cannot have a larger base than source."); 
        }

        BaseNumber divisorInSourceBase = new BaseNumber(n.getBase(), false, 
                                                        ((Byte) r.getBase()).toString(), n.getAdditionalValueMapping());

        int i = 0;
        BaseNumber[] divResult = {n, n};
        while (divResult[0].getValue().size() > 1 || divResult[0].getValue().get(0) != '0') {
            divResult = BaseNumberOperators.divDigit(divResult[0], divisorInSourceBase);
            r.addDigitAt(i, r.getAssociatedCharacter(divResult[1].convertToBaseTen()));
            i++;
        }

        return r;
    }

    /**
     * Takes a BaseNumber n and places it into BaseNumber r, assuming r is 0, in r's base.<br>
     * This only works if r's base is greater than n's.
     * @param n the number to convert
     * @param r the number where to place the conversion
     * @returns r
     * @throws IllegalArgumentException if r's base is lower than n's
     */
    public static BaseNumber convertBySubstitution(BaseNumber n, BaseNumber r) {
        if (r.getBase() < n.getBase()) { 
            throw new IllegalArgumentException("Target number cannot have a smaller base than source."); 
        }

        System.out.println(r.getAssociatedCharacter(n.getBase()));

        BaseNumber multiplicationPower = new BaseNumber(r.getBase(), false, 
                                                        "1", r.getAdditionalValueMapping());
        BaseNumber multiplicationBase = new BaseNumber(r.getBase(), false, 
                                                    "" + r.getAssociatedCharacter(n.getBase()), r.getAdditionalValueMapping());
        
        for (int i = 0; i < n.getValue().size(); i++) {
            BaseNumber digitToMultiply = new BaseNumber(r.getBase(), false, 
                                                        "" + n.getValue().get(i), r.getAdditionalValueMapping());
            r = BaseNumberOperators.add(r, BaseNumberOperators.mulDigit(multiplicationPower, digitToMultiply)); // order matters
            multiplicationPower = BaseNumberOperators.mulDigit(multiplicationPower, multiplicationBase); // here too
        }

        return r;

    }

}
