package congestion.calculator;

import congestion.calculator.domain.CongestionTaxCalculator;
import congestion.calculator.domain.TaxEvent;
import congestion.calculator.domain.Vehicle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@RestController
public class CongestionCalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CongestionCalculatorApplication.class, args);
	}

	@PostMapping("/calculate-tax")
	@ResponseBody
	public Response calculateTax(@RequestBody Request request) {
		Vehicle vehicle = Vehicle.createVehicle(request.vehicleType());
		List<TaxEvent> taxEvents = request.taxEventTimes().stream().map(this::taxEvent).collect(Collectors.toList());
		// TODO This relies on the contents of the parameter taxEvents getting mutated in a get method.
		// TODO Create and return a composite domain object in stead
		int tax = new CongestionTaxCalculator().getTax(vehicle, taxEvents);
		return new Response(vehicle.getVehicleType(), tax, taxEvents);
	}

	private TaxEvent taxEvent(String isoDateTime) {
		return new TaxEvent(parse(isoDateTime), TaxEvent.Status.TAXABLE, 0);
	}

	private static LocalDateTime parse(String isoDateTime) {
		return LocalDateTime.parse(isoDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
}

record Request(String vehicleType, List<String> taxEventTimes) {}

record Response(Vehicle.VehicleType vehicleType, int tax, List<TaxEvent> taxEvents) {}
