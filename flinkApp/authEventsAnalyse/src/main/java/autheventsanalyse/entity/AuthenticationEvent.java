package autheventsanalyse.entity;

public final class AuthenticationEvent {
    private String username;
    private boolean successful;
    private long timestamp;

    public AuthenticationEvent() {
    }

    public AuthenticationEvent(String username) {
        this.username = username;
    }

    public AuthenticationEvent(String username, boolean successful, long timestamp) {
        this.username = username;
        this.successful = successful;
        this.timestamp = timestamp;
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
}
