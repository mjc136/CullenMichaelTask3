package cm;

import java.util.List;

public class Period {
    private final int startHour;
    private final int endHour;

    public Period(int start, int end) {
        if (start >= end) {
            throw new IllegalArgumentException("start of period cannot be later or equal to end of period");
        }
        if (start < 0 || end > 24) {
            throw new IllegalArgumentException("start of period and end of period must be between 0 and 24");
        }
        this.startHour = start;
        this.endHour = end;
    }

    /**
     * checks if a given hour is within the period
     * @param hour the start of the hour to check
     * @return true if the hour is within the period
     */
    private boolean isIn(int hour) {
        return hour >= this.startHour && hour < this.endHour;
    }

    private static boolean isIn(int hour, List<Period> list) {
        for (Period period : list) {
            if (period.isIn(hour)) {
                return true; // Hour is found in one of the periods
            }
        }
        return false; // Hour is not found in any period
    }

    /**
     * The duration of a period
     * @return the number of whole hours this period covers
     */
    public int duration() {
        return this.endHour - this.startHour;
    }

    /**
     * Returns the numbers of hours this period is included in the list
     * @param list the list of periods to check
     * @return the number of full hours covered by this period
     */
    public int occurrences(List<Period> list) {
        int occurrences = 0;
        for (int hour = this.startHour; hour < this.endHour; hour++) {
            if (isIn(hour, list)) {
                occurrences++;
            }
        }
        return occurrences;
    }

    public boolean overlaps(Period period) {
        return this.endHour>period.startHour && this.startHour<period.endHour;
    }
}