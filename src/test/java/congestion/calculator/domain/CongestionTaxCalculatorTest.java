package congestion.calculator.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CongestionTaxCalculatorTest {

    @Test
    void carShouldBeTaxed() {
        int tax = getTax("2013-02-08 06:27:00");
        assertEquals(8, tax);
    }

    @Test
    void motorcycleIsExempt() {
        int tax = new CongestionTaxCalculator().getTax(new Motorcycle(), List.of(
                taxEvent("2013-02-08 06:27:00")
        ));

        assertEquals(0, tax);
    }

    @Test
    void couldBeTaxedTwiceInADay() {
        int tax = new CongestionTaxCalculator().getTax(new Car(), List.of(
                taxEvent("2013-02-08 06:27:00"), // 8 SEK
                taxEvent("2013-02-08 15:47:00") // 18 SEK
        ));

        assertEquals(26, tax);
    }

    @Test
    void shouldTaxTimesCorrectly() {
        assertEquals(8, getTax("2013-02-08 06:00:00"));
        assertEquals(8, getTax("2013-02-08 06:29:59"));

        assertEquals(13, getTax("2013-02-08 06:30:00"));
        assertEquals(13, getTax("2013-02-08 06:59:59"));

        assertEquals(18, getTax("2013-02-08 07:00:00"));
        assertEquals(18, getTax("2013-02-08 07:59:59"));

        assertEquals(13, getTax("2013-02-08 08:00:00"));
        assertEquals(13, getTax("2013-02-08 08:29:59"));

        assertEquals(8, getTax("2013-02-08 08:30:00"));
        assertEquals(8, getTax("2013-02-08 14:59:59"));

        assertEquals(13, getTax("2013-02-08 15:00:00"));
        assertEquals(13, getTax("2013-02-08 15:29:59"));

        assertEquals(18, getTax("2013-02-08 15:30:00"));
        assertEquals(18, getTax("2013-02-08 16:59:59"));

        assertEquals(13, getTax("2013-02-08 17:00:00"));
        assertEquals(13, getTax("2013-02-08 17:59:00"));

        assertEquals(8, getTax("2013-02-08 18:00:00"));
        assertEquals(8, getTax("2013-02-08 18:29:59"));

        assertEquals(0, getTax("2013-02-08 18:30:00"));
        assertEquals(0, getTax("2013-02-08 05:59:59"));
    }

    @Nested
    class TheSingleChargeRule {

        @Test
        void highestValueIsTheLastOne() {
            int tax = new CongestionTaxCalculator().getTax(new Car(), List.of(
                    taxEvent("2013-02-08 15:27:00"), // 13 SEK
                    taxEvent("2013-02-08 15:47:00") // 18 SEK
            ));

            assertEquals(18, tax);
        }

        @Test
        void highestValueIsTheFirstOne() {
            int tax = new CongestionTaxCalculator().getTax(new Car(), List.of(
                    taxEvent("2013-02-08 16:47:00"), // 18 SEK
                    taxEvent("2013-02-08 17:15:00") // 13 SEK
            ));

            assertEquals(18, tax);
        }
    }

    @Test
    void max60SekPerDay() {
        int tax = new CongestionTaxCalculator().getTax(new Car(), List.of(
                taxEvent("2013-02-08 07:29:00"), // 18 SEK
                taxEvent("2013-02-08 15:31:00"), // 18 SEK
                taxEvent("2013-02-08 16:42:00"), // 18 SEK
                taxEvent("2013-02-08 17:43:00") // 13 SEK
        ));

        assertEquals(60, tax);
    }

    @Test
    void moreThan60SekForMultipleDays() {
        int tax = new CongestionTaxCalculator().getTax(new Car(), List.of(
                taxEvent("2013-02-06 07:29:00"), // 18 SEK
                taxEvent("2013-02-07 15:31:00"), // 18 SEK
                taxEvent("2013-02-06 16:42:00"), // 18 SEK
                taxEvent("2013-02-08 17:43:00") // 13 SEK
        ));

        assertEquals(67, tax);
    }

    @Test
    void shouldHandleThePostItUseCases() {
        int tax = new CongestionTaxCalculator().getTax(new Car(), List.of(
                taxEvent("2013-01-14 21:00:00"), // tax-free time
                taxEvent("2013-01-15 21:00:00"), // tax-free time
                taxEvent("2013-02-07 06:23:27"), // 8 SEK
                taxEvent("2013-02-07 15:27:00"), // 13 SEK
                taxEvent("2013-02-08 06:27:00"), // 8 SEK
                taxEvent("2013-02-08 06:20:27"), // within 60 minutes

                taxEvent("2013-02-08 14:35:00"), // within 60 minutes
                taxEvent("2013-02-08 15:29:00"), // 13 SEK

                taxEvent("2013-02-08 15:47:00"), // 18 SEK
                taxEvent("2013-02-08 16:01:00"), // within 60 minutes

                taxEvent("2013-02-08 16:48:00"), // 18 SEK

                taxEvent("2013-02-08 17:49:00"), // 13 SEK
                taxEvent("2013-02-08 18:29:00"), // within 60 minutes
                taxEvent("2013-02-08 18:35:00"), // tax-free time
                taxEvent("2013-03-26 14:25:00"), // 8 SEK
                taxEvent("2013-03-28 14:07:27") // tax-free day
        ));

        assertEquals(89, tax);
    }

    @Test
    void shouldTaxOnlyWeekdays() {
        assertEquals(8, getTax("2013-08-12 06:27:00")); // Monday
        assertEquals(8, getTax("2013-08-13 06:27:00")); // Tuesday
        assertEquals(8, getTax("2013-08-14 06:27:00")); // Wednesday
        assertEquals(8, getTax("2013-08-15 06:27:00")); // Thursday
        assertEquals(8, getTax("2013-08-16 06:27:00")); // Friday
        assertEquals(0, getTax("2013-08-17 06:27:00")); // Saturday
        assertEquals(0, getTax("2013-08-18 06:27:00")); // Sunday
    }

    @Test
    void shouldNotTaxInJuly() {
        // Non-holiday Monday in July 2013
        assertEquals(0, getTax("2013-07-01 06:27:00")); // Monday
    }

    private int getTax(String dateTimeString) {
        return new CongestionTaxCalculator().getTax(new Car(), List.of(
                taxEvent(dateTimeString)
        ));
    }

    private TaxEvent taxEvent(String isoDateTime) {
        return new TaxEvent(
                LocalDateTime.parse(isoDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                TaxEvent.Status.TAXABLE,
                0
        );
    }
}