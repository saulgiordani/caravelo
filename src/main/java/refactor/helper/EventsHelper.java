package refactor.helper;

import refactor.entities.Flight;
import refactor.entities.Outcome;
import refactor.type.CampaignType;

/**
 * Helper to build events.
 */
public final class EventsHelper {

    static public NotifyOutcome notify(String email, Outcome outcome, CampaignType type) {
        return new NotifyOutcome(type, email, outcome);
    }

    static public WarningOverbooking warnForPossibleOverbooking(Flight flight) {
        return new WarningOverbooking(flight);
    }

    static public ReviewFlight toReview(Flight flight) {
        return new ReviewFlight(flight);
    }

    static public ToCleaningQueue toCleaningQueue(Flight flight) {
        return new ToCleaningQueue(flight);
    }

    /**
     * Marker interface for flow events.
     */
    public interface FlowEvent {
        //
    }

    /**
     * Warn the gate's desk for possible overbooking.
     */
    public static class WarningOverbooking implements FlowEvent {
        WarningOverbooking(Flight flight) {
            // out of scope
        }
    }

    /**
     * Signals manual intervention.
     */
    public static class ReviewFlight implements FlowEvent {
        ReviewFlight(Flight flight) {
            // out of scope
        }
    }

    /**
     * Puts a flight into the cleaning queue.
     */
    public static class ToCleaningQueue implements FlowEvent {
        ToCleaningQueue(Flight flight) {
            // out of scope
        }
    }

    /**
     * Mail notification.
     */
    public static class NotifyOutcome implements FlowEvent {
        public final Outcome outcome;
        public final String email;

        NotifyOutcome(CampaignType type, String email, Outcome outcome) {
            this.email = email;
            this.outcome = outcome;
        }
    }
}
