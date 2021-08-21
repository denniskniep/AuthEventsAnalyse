package autheventsanalyse.entity;

public final class EnrichedAuthenticationEvent {
    private String username;
    private String ip;
    private boolean successful;
    private long timestamp;

    private long failedLoginsSinceLastSuccessful;
    private String country;
    private boolean isOnVacation;
    private int risk;

    public EnrichedAuthenticationEvent() {
    }

    public EnrichedAuthenticationEvent(AuthenticationEvent authenticationEvent) {
        this.username = authenticationEvent.getUsername();
        this.ip = authenticationEvent.getIp();
        this.successful = authenticationEvent.isSuccessful();
        this.timestamp = authenticationEvent.getTimestamp();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getFailedLoginsSinceLastSuccessful() {
        return failedLoginsSinceLastSuccessful;
    }

    public void setFailedLoginsSinceLastSuccessful(long failedLoginsSinceLastSuccessful) {
        this.failedLoginsSinceLastSuccessful = failedLoginsSinceLastSuccessful;
    }

    public int getRisk() {
        return risk;
    }

    public void setRisk(int risk) {
        this.risk = risk;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isOnVacation() {
        return isOnVacation;
    }

    public void setOnVacation(boolean onVacation) {
        isOnVacation = onVacation;
    }

    @Override
    public String toString() {
        return "EnrichedAuthenticationEvent{" +
                "username='" + username + '\'' +
                ", successful=" + successful +
                ", risk=" + risk +
                '}';
    }
}
