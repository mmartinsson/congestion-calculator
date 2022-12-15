package congestion.calculator.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaxEvent {
    private final LocalDateTime dateTime;
    private Status status;
    private int tax;

    public TaxEvent(LocalDateTime dateTime, Status status, int tax) {
        this.dateTime = dateTime;
        this.status = status;
        this.tax = tax;
    }

    // make package private (default)
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    // make package private (default)
    public Status getStatus() {
        return status;
    }

    // make package private (default)
    public void setStatus(Status status) {
        this.status = status;
    }

    // make package private (default)
    public int getTax() {
        return tax;
    }

    // make package private (default)
    public void setTax(int tax) {
        this.tax = tax;
    }

    // make package private (default)
    public LocalDate date() {
        return dateTime.toLocalDate();
    }

    public enum Status {
        TAX_FREE_DATE,
        WITHIN_ONE_HOUR,
        TAX_FREE_TIME,
        TAXABLE
    }
}

