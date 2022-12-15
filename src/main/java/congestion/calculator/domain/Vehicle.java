package congestion.calculator.domain;

import java.util.List;

public abstract class Vehicle {

    public abstract VehicleType getVehicleType();

    public enum VehicleType {
        CAR,
        MOTORCYCLE,
        TRACTOR,
        EMERGENCY,
        DIPLOMAT,
        FOREIGN,
        MILITARY
    }

    public static Vehicle createVehicle(String vehicleType) {
        return switch (VehicleType.valueOf(vehicleType.toUpperCase())) {
            case MOTORCYCLE -> new Motorcycle();
            case CAR -> new Car();
            default -> throw new IllegalArgumentException();
        };
    }

    private final static List<VehicleType> tollFreeVehicles = List.of(
        VehicleType.MOTORCYCLE,
        VehicleType.TRACTOR,
        VehicleType.EMERGENCY,
        VehicleType.DIPLOMAT,
        VehicleType.FOREIGN,
        VehicleType.MILITARY
    );

    boolean isTollFree() {
        return tollFreeVehicles.contains(getVehicleType());
    }
}
