package autheventsanalyse.jobs.toomanylogins;

public class TooManyLoginsAlert {
    private String username;
    private long count;

    public TooManyLoginsAlert() {
    }

    public TooManyLoginsAlert(String username, long count) {
        this.username = username;
        this.count = count;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "TooManyLoginsAlert{" +
                "username='" + username + '\'' +
                ", count=" + count +
                '}';
    }
}
