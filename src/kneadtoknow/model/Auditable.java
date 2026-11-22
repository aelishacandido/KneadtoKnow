package kneadtoknow.model;

public interface Auditable {
    boolean isAlertTriggered();
    String getAlertStatus();
}
