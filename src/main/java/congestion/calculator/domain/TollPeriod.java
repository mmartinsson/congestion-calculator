package congestion.calculator.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

class TollPeriod {
    private final LocalTime startingAt;
    private LocalTime endingAt;
    private int taxedWith;

    // Make private
    TollPeriod(LocalTime startingAt) {
        this.startingAt = startingAt;
    }

    // Make package private (default)
    public TollPeriod isTaxedWith(int taxedWith) {
        this.taxedWith = taxedWith;
        return this;
    }

    boolean matches(LocalDateTime eventTime) {
        return (eventTime.toLocalTime().equals(startingAt) || eventTime.toLocalTime().isAfter(startingAt))
                && (eventTime.toLocalTime().isBefore(endingAt) || (eventTime.toLocalTime().equals(endingAt)));
    }

    // Make package private (default)
    public int getTaxedWith() {
        return taxedWith;
    }

    // Move to top with the other DSL method
    static List<TollPeriod> periods(TollPeriod... periodsWithoutEnding) {
        List<TollPeriod> completePeriods = Arrays.asList(periodsWithoutEnding);
        for (int i = 0; i < completePeriods.size()-1; i++) {
            completePeriods.get(i).endingAt = completePeriods.get(i+1).startingAt.minusSeconds(1);
        }
        completePeriods.get(completePeriods.size()-1).endingAt = completePeriods.get(0).startingAt.minusSeconds(1);
        return completePeriods;
    }

    // Move to top with the other DSL method
    // Extract DSL to separate file?
    static TollPeriod periodFrom(int hours, int minutes) {
        return new TollPeriod(LocalTime.of(hours, minutes));
    }
}
