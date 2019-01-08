package refactor.entities;

import refactor.type.FlightInventoryStatus;

public class Outcome {
    private Integer target;
    private FlightInventoryStatus inventoryStatus = FlightInventoryStatus.NA;

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }

    public FlightInventoryStatus getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(FlightInventoryStatus inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }
}
