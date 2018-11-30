package refactor;

import com.google.common.eventbus.EventBus;

/**
 * The smelly flow executor.
 */
public class FlowExecutor {

    private EventBus bus;

    public FlowExecutor() {
        this.bus = new EventBus();
    }

    /**
     * Executes a flow depending on the given parameters.
     *
     * @param c The campaign
     * @param f The flight data
     * @param email The e-mail address for notifications
     * @return the execution outcome
     */
    public Outcome execute(Campaign c, Flight f, String email) {
        Outcome outcome = new Outcome();

        if (c.getType() == Campaign.CampaignType.A) {
            // 1. Determine absolute target
            int target = f.getCapacity() + c.getOversell();
            outcome.setTarget(target);
            // 2. Flag closed flights
            if (f.getSold() >= f.getCapacity()) {
                outcome.setInventoryStatus(Outcome.FlightInventoryStatus.CLOSED);
            } else {
                outcome.setInventoryStatus(Outcome.FlightInventoryStatus.OPEN);
            }
            // 3. Notify outcome
            bus.post(Events.notify(email, outcome, c.getType()));
            return outcome;
        } else if (c.getType() == Campaign.CampaignType.B) {
            // 1. Determine relative target
            int target = f.getCapacity() + (int) (f.getCapacity() * c.getOversellFactor());
            outcome.setTarget(target);
            // 2. Flag closed flights
            int safetyMargin = f.isLongHaul() ? 40 : 10;
            if (f.getCapacity() - safetyMargin >= f.getSold() + (target - f.getCapacity())) {
                outcome.setInventoryStatus(Outcome.FlightInventoryStatus.OPEN);
            } else {
                outcome.setInventoryStatus(Outcome.FlightInventoryStatus.CLOSED);
                // 3. Signal flight to be reviewed
                bus.post(Events.toReview(f));
            }
            return outcome;
        } else if (c.getType() == Campaign.CampaignType.C) {
            // 1. Determine target by load factor
            if (f.getSold().floatValue() / f.getCapacity() > 1) {
                outcome.setTarget(f.getCapacity());
            } else {
                // 1.1. Put flight into cleaning queue
                this.bus.post(Events.toCleaningQueue(f));
            }
            return outcome;
        } else {
            throw new UnsupportedFlowType();
        }
    }

    void register(Object listener) {
        this.bus.register(listener);
    }
}
