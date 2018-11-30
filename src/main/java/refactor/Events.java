package refactor;

/**
 * Helper to build events.
 */
public final class Events {
    private Events() {

    }

    static NotifyOutcome notify(String email, Outcome outcome, Campaign.CampaignType type) {
        return new NotifyOutcome(type, email, outcome);
    }

    static ReviewFlight toReview(Flight flight) {
        return new ReviewFlight(flight);
    }

    static ToCleaningQueue toCleaningQueue(Flight flight) {
        return new ToCleaningQueue(flight);
    }

    /**
     * Marker interface for flow events.
     */
    public interface FlowEvent {
        //
    }

    /**
     * Signals manual intervention.
     */
    static class ReviewFlight implements FlowEvent {
        ReviewFlight(Flight flight) {
            // out of scope
        }
    }

    /**
     * Puts a flight into the cleaning queue.
     */
    static class ToCleaningQueue implements FlowEvent {
        ToCleaningQueue(Flight flight) {
            // out of scope
        }
    }

    /**
     * Mail notification.
     */
    static class NotifyOutcome implements FlowEvent {
        public final Outcome outcome;
        public final String email;

        NotifyOutcome(Campaign.CampaignType type, String email, Outcome outcome) {
            this.email = email;
            this.outcome = outcome;
        }
    }
}
