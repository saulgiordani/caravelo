package refactor.support;

import com.google.common.eventbus.Subscribe;

import refactor.helper.EventsHelper.FlowEvent;

public class DummyEventListener {
    private FlowEvent lastEvent;

    @Subscribe
    public void on(FlowEvent event) {
        this.lastEvent = event;
    }

    public Class<? extends FlowEvent> getLastEventType() {
        return lastEvent.getClass();
    }

    public <T extends FlowEvent> T getLastEvent() {
        return (T) lastEvent;
    }
}