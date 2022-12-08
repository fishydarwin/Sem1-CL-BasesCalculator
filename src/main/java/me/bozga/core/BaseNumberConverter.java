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
    public static BaseNumber convertBySuccessiveDivisions(final BaseNumber n, BaseNumber r) {
        if (r.getBase() > n.getBase()) { 
            throw new IllegalArgumentException("Target number cannot have a larger base than source."); 
        }

        BaseNumber divisorInSourceBase = new BaseNumber(n.getBase(), false, "" + n.getAssociatedCharacter(r.getBase()), 
                                                        n.getAdditionalValueMapping());

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
    public static BaseNumber convertBySubstitution(final BaseNumber n, BaseNumber r) {
        if (r.getBase() < n.getBase()) { 
            throw new IllegalArgumentException("Target number cannot have a smaller base than source."); 
        }

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

    /**
     * Takes a BaseNumber n and rapid converts it to BaseNumber r, assuming r is 0.
     * This only works if log br (bn) returns an integer number
     * @param n the number to convert
     * @param r the number where to place the conversion
     * @return r
     * @throws IllegalArgumentException if n's base cannot be written as r to an integer power
     */
    public static BaseNumber convertByRapidConversion(final BaseNumber n, BaseNumber r) throws IllegalArgumentException {
        if (r.getBase() < n.getBase()) { // lower conversion

            double predictedGroupSize = Math.log(n.getBase()) / Math.log(r.getBase()); // log br (bn) = ln(bn) / ln (br)
            int groupSize = (int) predictedGroupSize;
            if (groupSize != predictedGroupSize) {
                throw new IllegalArgumentException("Cannot lower rapid convert to target number."); 
            }
            
            // convert 1 digit into groups of digit in target base
            BaseNumber groupNumber;
            int currentPosition = 0;
            for (int i = 0; i < n.getValue().size(); i++) {
                groupNumber = new BaseNumber(r.getBase(), false, "0", r.getAdditionalValueMapping());
                groupNumber = convertBySuccessiveDivisions(
                    new BaseNumber(n.getBase(), false, "" + n.getValue().get(i), n.getAdditionalValueMapping()), groupNumber);
                while (groupNumber.getValue().size() % groupSize != 0) {
                    groupNumber.addDigitAt(groupNumber.getValue().size(), '0');
                }
                for (int j = 0; j < groupNumber.getValue().size(); j++) {
                    r.addDigitAt(currentPosition, groupNumber.getValue().get(j));
                    currentPosition++;
                }
            }
            
            // clean up inevitable trailing zeros
            for (int j = r.getValue().size() - 1; j > 0; j--) {
                if (r.getValue().get(j) != '0') { break; }
                r.removeLastDigit();
            }

            return r;

        } else if (r.getBase() > n.getBase()) { // upper conversion

            double predictedGroupSize = Math.log(r.getBase()) / Math.log(n.getBase()); // log bn (br) = ln(br) / ln (bn)
            int groupSize = (int) predictedGroupSize;
            if (groupSize != predictedGroupSize) {
                throw new IllegalArgumentException("Cannot upper rapid convert to target number."); 
            }

            while (n.getValue().size() % groupSize != 0) {
                n.addDigitAt(n.getValue().size(), '0');
            }

            // convert groups of digit into 1 digit in target base
            BaseNumber groupNumber;
            int currentPosition = 0;
            for (int i = 0; i < n.getValue().size(); i += groupSize) {
                String concatenatedGroup = "";
                for (int j = i + groupSize - 1; j >= i; j--) {
                    concatenatedGroup += n.getValue().get(j);
                }
                groupNumber = new BaseNumber(r.getBase(), false, "0", r.getAdditionalValueMapping());
                groupNumber = convertBySubstitution(
                    new BaseNumber(n.getBase(), false, concatenatedGroup, n.getAdditionalValueMapping()), groupNumber);
                r.addDigitAt(currentPosition, groupNumber.getValue().get(0));
                currentPosition++;
            }

            return r;

        } else {
            return n;
        }

    }

}
