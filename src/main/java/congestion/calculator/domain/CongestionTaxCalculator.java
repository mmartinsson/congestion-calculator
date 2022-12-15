package congestion.calculator.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.List;

import static congestion.calculator.domain.TaxEvent.Status.TAXABLE;
import static congestion.calculator.domain.TaxEvent.Status.TAX_FREE_DATE;
import static congestion.calculator.domain.TaxEvent.Status.TAX_FREE_TIME;
import static congestion.calculator.domain.TollPeriod.periodFrom;
import static congestion.calculator.domain.TollPeriod.periods;
import static java.util.stream.Collectors.groupingBy;

public class CongestionTaxCalculator {

    List<TollPeriod> tollPeriods = periods(
            periodFrom(0, 0).isTaxedWith(0),
            periodFrom(6, 0).isTaxedWith(8),
            periodFrom(6, 30).isTaxedWith(13),
            periodFrom(7, 0).isTaxedWith(18),
            periodFrom(8, 0).isTaxedWith(13),
            periodFrom(8, 30).isTaxedWith(8),
            periodFrom(15, 0).isTaxedWith(13),
            periodFrom(15, 30).isTaxedWith(18),
            periodFrom(17, 0).isTaxedWith(13),
            periodFrom(18, 0).isTaxedWith(8),
            periodFrom(18, 30).isTaxedWith(0)
    );

    List<LocalDate> tollFreeDates = List.of(
            LocalDate.of(2013, 1, 1),
            LocalDate.of(2013, 3, 28),
            LocalDate.of(2013, 3, 29),
            LocalDate.of(2013, 4, 1),
            LocalDate.of(2013, 4, 30),
            LocalDate.of(2013, 5, 1),
            LocalDate.of(2013, 5, 8),
            LocalDate.of(2013, 5, 9),
            LocalDate.of(2013, 6, 5),
            LocalDate.of(2013, 6, 6),
            LocalDate.of(2013, 6, 21),
            LocalDate.of(2013, 11, 1),
            LocalDate.of(2013, 12, 24),
            LocalDate.of(2013, 12, 25),
            LocalDate.of(2013, 12, 26),
            LocalDate.of(2013, 12, 31)
    );

    public int getTax(Vehicle vehicle, List<TaxEvent> taxEvents)
    {
        if (vehicle.isTollFree()) return 0;

        return taxEvents.stream()
                .sorted(Comparator.comparing(TaxEvent::getDateTime))
                .map(this::markIfTaxFreeDate)
                .map(this::calculateTaxPerEvent)
                .collect(SingleChargeRule.collector()).stream()
                .collect(groupingBy(TaxEvent::date)).values().stream()
                .map(this::calculateTaxPerDay).mapToInt(Integer::intValue)
                .sum();
    }

    private TaxEvent markIfTaxFreeDate(TaxEvent taxEvent) {
        if (isTaxFreeDate(taxEvent.getDateTime())) {
            taxEvent.setStatus(TAX_FREE_DATE);
        }
        return taxEvent;
    }

    private TaxEvent calculateTaxPerEvent(TaxEvent taxEvent) {
        int tollFee = getTollFee(taxEvent.getDateTime());
        taxEvent.setTax(tollFee);
        if (tollFee == 0) taxEvent.setStatus(TAX_FREE_TIME);
        return taxEvent;
    }

    private int calculateTaxPerDay(List<TaxEvent> taxEvents) {
        int totalAmount = taxEvents.stream()
                .filter(taxEvent -> taxEvent.getStatus().equals(TAXABLE))
                .map(TaxEvent::getTax)
                .mapToInt(Integer::intValue)
                .sum();

        return Math.min(totalAmount, 60);
    }

    private boolean isTaxFreeDate(LocalDateTime dateTime)
    {
        if (dateTime.getMonth().equals(Month.JULY)) return true;

        if (dateTime.getDayOfWeek().equals(DayOfWeek.SATURDAY)) return true;
        if (dateTime.getDayOfWeek().equals(DayOfWeek.SUNDAY)) return true;

        // Decide on either 'tax' or 'toll' and be consistent with naming
        return tollFreeDates.contains(dateTime.toLocalDate());
    }

    private int getTollFee(LocalDateTime dateTime)
    {
        for (TollPeriod tollPeriod : tollPeriods) {
            if (tollPeriod.matches(dateTime)) {
                return tollPeriod.getTaxedWith();
            }
        }
        return 0;
    }
}
