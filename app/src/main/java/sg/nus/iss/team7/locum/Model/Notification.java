package sg.nus.iss.team7.locum.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Notification {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("userId")
    @Expose
    private Long userId;

    @SerializedName("jobId")
    @Expose
    private Long jobId;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("body")
    @Expose
    private String body;

    @SerializedName("notificationRead")
    @Expose
    private boolean isRead;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public boolean isRead() {
        return isRead;
    }
}
