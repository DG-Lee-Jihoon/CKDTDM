package com.parking.model;

public class SyncEvent {

    public enum Phase {
        LOCKED, TEMPED, UPDATED, SYNCED
    }

    private String eventId;
    private Phase phase;
    private int fromServerId;
    private Vehicle vehicle;
    private String operation;
    private long timestamp;
    private String message;

    public SyncEvent() {}

    public SyncEvent(String eventId, Phase phase, int fromServerId, Vehicle vehicle,
                     String operation, long timestamp, String message) {
        this.eventId = eventId;
        this.phase = phase;
        this.fromServerId = fromServerId;
        this.vehicle = vehicle;
        this.operation = operation;
        this.timestamp = timestamp;
        this.message = message;
    }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String eventId;
        private Phase phase;
        private int fromServerId;
        private Vehicle vehicle;
        private String operation;
        private long timestamp;
        private String message;

        public Builder eventId(String v) { this.eventId = v; return this; }
        public Builder phase(Phase v) { this.phase = v; return this; }
        public Builder fromServerId(int v) { this.fromServerId = v; return this; }
        public Builder vehicle(Vehicle v) { this.vehicle = v; return this; }
        public Builder operation(String v) { this.operation = v; return this; }
        public Builder timestamp(long v) { this.timestamp = v; return this; }
        public Builder message(String v) { this.message = v; return this; }

        public SyncEvent build() {
            return new SyncEvent(eventId, phase, fromServerId, vehicle, operation, timestamp, message);
        }
    }

    // Getters & Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public Phase getPhase() { return phase; }
    public void setPhase(Phase phase) { this.phase = phase; }

    public int getFromServerId() { return fromServerId; }
    public void setFromServerId(int fromServerId) { this.fromServerId = fromServerId; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
