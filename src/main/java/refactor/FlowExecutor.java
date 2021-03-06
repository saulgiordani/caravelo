package refactor;

import com.google.common.eventbus.EventBus;

import refactor.entities.Campaign;
import refactor.entities.Flight;
import refactor.entities.Outcome;
import refactor.exception.UnsupportedFlowType;
import refactor.helper.EventsHelper;
import refactor.type.FlightInventoryStatus;

/**
 * The smelly flow executor.
 */
public class FlowExecutor {

    private EventBus bus = new EventBus();

    /**
     * Executes a flow depending on the given parameters.
     *
     * @param c     The campaign
     * @param f     The flight data
     * @param email The e-mail address for notifications
     * @return the execution outcome
     */
    public Outcome execute(Campaign c, Flight f, String email) {
        Outcome outcome = new Outcome();
        int target;

        switch (c.getType()) {
        case A:
            // 1. Determine absolute target
            target = f.getCapacity() + c.getOversell();
            outcome.setTarget(target);

            // 2. Flag closed flights
            if (f.getSold() >= f.getCapacity()) {
                outcome.setInventoryStatus(FlightInventoryStatus.CLOSED);
            } else {
                outcome.setInventoryStatus(FlightInventoryStatus.OPEN);
            }

            // 3. Notify outcome
            bus.post(EventsHelper.notify(email, outcome, c.getType()));
            break;

        case B:
            // 1. Determine relative target
            target = f.getCapacity() + (int) (f.getCapacity() * c.getOversellFactor());
            outcome.setTarget(target);

            // 2. Flag closed flights
            int safetyMargin = f.isLongHaul() ? 40 : 10;
            int remainingCapacity = f.getCapacity() - safetyMargin;
            int totalPassengers = f.getSold() + (target - f.getCapacity());

            if (remainingCapacity >= totalPassengers) {
                outcome.setInventoryStatus(FlightInventoryStatus.OPEN);
            } else {
                outcome.setInventoryStatus(FlightInventoryStatus.CLOSED);

                // 3. Signal flight to be reviewed
                bus.post(EventsHelper.toReview(f));
            }
            break;

        case C:
            // 1. Determine target by load factor
            if (f.getSold().floatValue() / f.getCapacity() > 1) {
                outcome.setTarget(f.getCapacity());
            } else {
                // 1.1. Put flight into cleaning queue
                this.bus.post(EventsHelper.toCleaningQueue(f));
            }
            break;

        default:
            throw new UnsupportedFlowType();
        }

        return outcome;
    }

    void register(Object listener) {
        this.bus.register(listener);
    }
}