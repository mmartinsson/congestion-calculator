package congestion.calculator.domain;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;

public class SingleChargeRule {

    private final List<TaxEvent> taxEvents = new ArrayList<>();

    public void accept(TaxEvent taxEvent) {
        taxEvents.add(taxEvent);
    }

    public SingleChargeRule combine(SingleChargeRule other) {
        throw new UnsupportedOperationException("parallel stream not supported");
    }

    public List<TaxEvent> finish() {
        List<TaxEvent> eventsIn60Minutes = new ArrayList<>();
        for (TaxEvent taxEvent : taxEvents) {

            if (eventsIn60Minutes.isEmpty()) {
                eventsIn60Minutes.add(taxEvent);

            } else if (isWithin60Minutes(eventsIn60Minutes, taxEvent)) {
                eventsIn60Minutes.add(taxEvent);

            } else {
                keepEventWithMaxTax(eventsIn60Minutes);
                eventsIn60Minutes.clear();
                eventsIn60Minutes.add(taxEvent);
            }
        }
        keepEventWithMaxTax(eventsIn60Minutes);
        return taxEvents;
    }

    // A better name would be markTaxFreeEvents
    // Also the list should be cloned before working on it
    private void keepEventWithMaxTax(List<TaxEvent> eventsIn60Minutes) {
        TaxEvent eventWithMaxTax = new ArrayList<>(eventsIn60Minutes).stream().max(Comparator.comparing(TaxEvent::getTax)).orElseThrow();
        eventsIn60Minutes.remove(eventWithMaxTax);
        eventsIn60Minutes.forEach(taxEvent -> taxEvent.setStatus(TaxEvent.Status.WITHIN_ONE_HOUR));
    }

    private boolean isWithin60Minutes(List<TaxEvent> eventsIn60Minutes, TaxEvent candidate) {
        // Use constants for the magic numbers. Existing ones in java.time?
        return Duration.between(eventsIn60Minutes.get(0).getDateTime(), candidate.getDateTime()).getSeconds() < 60 * 60;
    }

    public static Collector<TaxEvent, ?, List<TaxEvent>> collector() {
        return Collector.of(SingleChargeRule::new, SingleChargeRule::accept, SingleChargeRule::combine, SingleChargeRule::finish);
    }
}
