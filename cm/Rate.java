package cm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Rate {
    private final CarParkKind kind;
    private final BigDecimal hourlyNormalRate;
    private final BigDecimal hourlyReducedRate;
    private final ArrayList<Period> reduced;
    private final ArrayList<Period> normal;

    public Rate(CarParkKind kind, ArrayList<Period> reducedPeriods, ArrayList<Period> normalPeriods, BigDecimal normalRate, BigDecimal reducedRate) {
        if (reducedPeriods == null || normalPeriods == null) {
            throw new IllegalArgumentException("periods cannot be null");
        }
        if (normalRate == null || reducedRate == null) {
            throw new IllegalArgumentException("The rates cannot be null");
        }
        if (normalRate.compareTo(BigDecimal.TEN) > 0) {
            throw new IllegalArgumentException("The rates cannot be more than 10");
        }
        if (normalRate.compareTo(BigDecimal.ZERO) < 0 || reducedRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("A rate cannot be negative");
        }
        if (normalRate.compareTo(reducedRate) < 0) {
            throw new IllegalArgumentException("The normal rate cannot be less then reduced rate");
        }
        if (!isValidPeriods(reducedPeriods) || !isValidPeriods(normalPeriods)) {
            throw new IllegalArgumentException("The periods are not valid individually");
        }
        if (!isValidPeriods(reducedPeriods, normalPeriods)) {
            throw new IllegalArgumentException("The periods overlaps");
        }
        this.kind = kind;
        this.hourlyNormalRate = normalRate;
        this.hourlyReducedRate = reducedRate;
        this.reduced = reducedPeriods;
        this.normal = normalPeriods;
    }

    /**
     * Checks if two collections of periods are valid together
     * @param periods1
     * @param periods2
     * @return true if the two collections of periods are valid together
     */
    private boolean isValidPeriods(ArrayList<Period> periods1, ArrayList<Period> periods2) {
        boolean isValid = true;
        int i = 0;
        while (i < periods1.size() && isValid) {
            isValid = isValidPeriod(periods1.get(i), periods2);
            i++;
        }
        return isValid;
    }

    /**
     * checks if a collection of periods is valid
     * @param list the collection of periods to check
     * @return true if the periods do not overlap
     */
    private boolean isValidPeriods(ArrayList<Period> list) {
        boolean isValid = true;
        if (list.size() >= 2) {
            int i = 0;
            int lastIndex = list.size()-1;
            while (i < lastIndex && isValid) {
                isValid = isValidPeriod(list.get(i), ((List<Period>)list).subList(i + 1, lastIndex+1));
                i++;
            }
        }
        return isValid;
    }

    /**
     * checks if a period is a valid addition to a collection of periods
     * @param period the Period addition
     * @param list the collection of periods to check
     * @return true if the period does not overlap in the collection of periods
     */
    private boolean isValidPeriod(Period period, List<Period> list) {
        boolean isValid = true;
        int i = 0;
        while (i < list.size() && isValid) {
            isValid = !period.overlaps(list.get(i));
            i++;
        }
        return isValid;
    }
    public BigDecimal calculate(Period periodStay) {
        // Calculate normal and reduced rate hours
        int normalRateHours = periodStay.occurrences(normal);
        int reducedRateHours = periodStay.occurrences(reduced);

        // Calculate the total cost before applying reductions
        BigDecimal totalCost = hourlyNormalRate.multiply(BigDecimal.valueOf(normalRateHours))
                .add(hourlyReducedRate.multiply(BigDecimal.valueOf(reducedRateHours)));

        // Apply reduction rules based on the CarParkKind
        switch (kind) {
            case VISITOR:
                if (totalCost.compareTo(BigDecimal.TEN) <= 0) {
                    return BigDecimal.ZERO; // Free if total is <= 10
                } else {
                    return totalCost.subtract(BigDecimal.TEN).multiply(BigDecimal.valueOf(0.5));
                }

            case MANAGEMENT:
                // Ensure a minimum payable amount of 4.00
                return totalCost.compareTo(BigDecimal.valueOf(4)) < 0 ? BigDecimal.valueOf(4) : totalCost;

            case STUDENT:
                if (totalCost.compareTo(BigDecimal.valueOf(5.50)) <= 0) {
                    return totalCost; // No reduction for amounts <= 5.50
                } else {
                    BigDecimal reducedAmount = totalCost.subtract(BigDecimal.valueOf(5.50)).multiply(BigDecimal.valueOf(0.75));
                    return reducedAmount.add(BigDecimal.valueOf(5.50));
                }

            case STAFF:
                // Cap the total payable amount at 16.00
                return totalCost.compareTo(BigDecimal.valueOf(16)) > 0 ? BigDecimal.valueOf(16) : totalCost;

            default:
                throw new IllegalArgumentException("Invalid CarParkKind");
        }
    }

}