package refactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import refactor.entities.Campaign;
import refactor.entities.Events;
import refactor.entities.Flight;
import refactor.entities.Outcome;
import refactor.support.DummyEventListener;
import refactor.type.CampaignType;
import refactor.type.FlightInventoryStatus;

public class FlowExecutorTest {

    private static final String EMAIL = "abc@zyx.com";

    private FlowExecutor executor;
    private Flight flight;

    @Before
    public void beforeEach() {
        this.executor = new FlowExecutor();
        this.flight = new Flight(100, 50);
    }

    @Test
    public void flowA_NoOversell() {
        Campaign c = new Campaign(CampaignType.A, 0, 0);

        Outcome o = executor.execute(c, flight, EMAIL);

        Assert.assertEquals("Target should be equals to capacity", flight.getCapacity(), o.getTarget());
    }

    @Test
    public void flowA_OversellBy15Seats() {
        Campaign c = new Campaign(CampaignType.A, 15, 0);

        Outcome o = executor.execute(c, flight, EMAIL);

        Assert.assertEquals("Target should be cap + 15 seats", 115, o.getTarget().intValue());
    }

    @Test
    public void flowA_NotifyOutcome() {
        Campaign c = new Campaign(CampaignType.A, 15, 0);
        DummyEventListener listener = new DummyEventListener();
        executor.register(listener);

        Outcome o = executor.execute(c, flight, EMAIL);
        Events.NotifyOutcome notify = listener.getLastEvent();

        Assert.assertEquals("E-mail should match", EMAIL, notify.email);
        Assert.assertEquals("Inventory status should match", o.getInventoryStatus(), notify.outcome.getInventoryStatus());
    }

    @Test
    public void flowA_FlagsClosedFlight() {
        Campaign c = new Campaign(CampaignType.A, 0, 0);
        flight.setSold(flight.getCapacity());

        Outcome o = executor.execute(c, flight, EMAIL);

        Assert.assertEquals("Flight should be closed", FlightInventoryStatus.CLOSED, o.getInventoryStatus());
    }

    @Test
    public void flowA_FlagsOpenFlight() {
        Campaign c = new Campaign(CampaignType.A, 0, 0);

        Outcome o = executor.execute(c, flight, EMAIL);

        Assert.assertEquals("Flight should be open", FlightInventoryStatus.OPEN, o.getInventoryStatus());
    }

    @Test
    public void flowB_NoOversell() {
        Campaign c = new Campaign(CampaignType.B, 0, 0);

        Outcome o = executor.execute(c, flight, EMAIL);

        Assert.assertEquals("Target should be equals to capacity", flight.getCapacity(), o.getTarget());
    }

    @Test
    public void flowB_FlagClosedFlight() {
        Campaign c = new Campaign(CampaignType.B, 0, 0.15f);
        flight.setSold(flight.getCapacity());

        Outcome o = executor.execute(c, flight, EMAIL);

        Assert.assertEquals("Flight should be closed", FlightInventoryStatus.CLOSED, o.getInventoryStatus());
    }

    @Test
    public void flowB_FlagsClosedFlightByProjection() {
        Campaign c = new Campaign(CampaignType.B, 0, 0.75f);

        Outcome o = executor.execute(c, flight, EMAIL);

        Assert.assertEquals("Flight should be closed", FlightInventoryStatus.CLOSED, o.getInventoryStatus());
    }

    @Test
    public void flowB_SignalsToReviewEvent() {
        Campaign c = new Campaign(CampaignType.B, 0, 0.15f);
        flight.setSold(flight.getCapacity());
        DummyEventListener listener = new DummyEventListener();
        executor.register(listener);

        executor.execute(c, flight, EMAIL);

        Assert.assertEquals("Event type should be ReviewFlight", listener.getLastEventType(), Events.ReviewFlight.class);
    }

    @Test
    public void flowB_FlagsOpenFlight() {
        Campaign c = new Campaign(CampaignType.B, 0, 0.15f);

        Outcome o = executor.execute(c, flight, EMAIL);

        Assert.assertEquals("Flight should be open", FlightInventoryStatus.OPEN, o.getInventoryStatus());
    }

    @Test
    public void flowB_FlagsOpenFlightLongHaul() {
        Campaign c = new Campaign(CampaignType.B, 0, 0.10f);
        flight.setLongHaul(true);

        Outcome o = executor.execute(c, flight, EMAIL);

        Assert.assertEquals("Long haul flight should be open", FlightInventoryStatus.OPEN, o.getInventoryStatus());
    }

    @Test
    public void flowB_FlagsClosedFlightLongHaul() {
        Campaign c = new Campaign(CampaignType.B, 0, 0.15f);
        flight.setLongHaul(true);

        Outcome o = executor.execute(c, flight, EMAIL);

        Assert.assertEquals("Long haul flight should be closed", FlightInventoryStatus.CLOSED, o.getInventoryStatus());
    }


    @Test
    public void flowB_OversellBy25PctOfCap() {
        Campaign c = new Campaign(CampaignType.B, 0, 0.25f);

        Outcome o = executor.execute(c, flight, EMAIL);

        Assert.assertEquals("Target should be cap + 25%", 125, o.getTarget().intValue());
    }

    @Test
    public void flowC_ToCleaningQueue() {
        Campaign c = new Campaign(CampaignType.C, 0, 0);
        DummyEventListener listener = new DummyEventListener();
        executor.register(listener);

        executor.execute(c, flight, EMAIL);

        Assert.assertEquals("Event type should be ToCleaningQueue", listener.getLastEventType(), Events.ToCleaningQueue.class);
    }

    @Test
    public void flowC_EmptyOverbooked() {
        Campaign c = new Campaign(CampaignType.C, 0, 0);
        flight.setSold(flight.getCapacity() + 1);

        Outcome o = executor.execute(c, flight, EMAIL);

        Assert.assertEquals("Target should be the same as cap", flight.getCapacity(), o.getTarget());
    }

    @Test(expected = UnsupportedFlowType.class)
    public void flowTypeIsNotSupported() {
        Campaign c = new Campaign(CampaignType.Z, 0, 0);

        executor.execute(c, flight, EMAIL);

        Assert.fail("An UnsupportedFlowType exception should be thrown");
    }

}
