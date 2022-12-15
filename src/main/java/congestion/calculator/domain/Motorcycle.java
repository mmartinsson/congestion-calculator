package congestion.calculator.domain;

public class Motorcycle extends Vehicle {
    @Override
    public VehicleType getVehicleType() {
        return VehicleType.MOTORCYCLE;
    }
}
