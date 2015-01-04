package org.rhq.alerts.api.common.data;

/**
 * An internal incoming event.
 * It represents some internal state inside AlertsService.
 */
public class State extends Event {

    String state;

    public State(String id, Long time, String state) {
        super(id, time);
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State)) return false;
        if (!super.equals(o)) return false;

        State state1 = (State) o;

        if (state != null ? !state.equals(state1.state) : state1.state != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("State{");
        sb.append("state='").append(state).append('\'');
        sb.append(", id='").append(getId()).append('\'');
        sb.append(", time=").append(getTime());
        sb.append('}');
        return sb.toString();
    }
}
