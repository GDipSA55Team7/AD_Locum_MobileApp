package sg.nus.iss.team7.locum.Model;

//import java.time.LocalDateTime;
//
//public class JobPost {
//
//    private long id;
//    private String description;
//    private String startDateTime;
//    private String endDateTime;
//    private double ratePerHour;
//    private double totalRate;
//    private String status;
//    private User clinicUser;
//    private User freelancer;
//    private Clinic clinic;
//
//    public JobPost() {
//    }
//
//    public JobPost(long id, String description, String startDateTime, String endDateTime, double ratePerHour, double totalRate,
//                   String status, User clinicUser, User freelancer, Clinic clinic) {
//        this.id = id;
//        this.description = description;
//        this.startDateTime = startDateTime;
//        this.endDateTime = endDateTime;
//        this.ratePerHour = ratePerHour;
//        this.totalRate = totalRate;
//        this.status = status;
//        this.clinicUser = clinicUser;
//        this.freelancer = freelancer;
//        this.clinic = clinic;
//    }
//
//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public String getStartDateTime() {
//        return startDateTime;
//    }
//
//    public void setStartDateTime(String startDateTime) {
//        this.startDateTime = startDateTime;
//    }
//
//    public String getEndDateTime() {
//        return endDateTime;
//    }
//
//    public void setEndDateTime(String endDateTime) {
//        this.endDateTime = endDateTime;
//    }
//
//    public double getRatePerHour() {
//        return ratePerHour;
//    }
//
//    public void setRatePerHour(double ratePerHour) {
//        this.ratePerHour = ratePerHour;
//    }
//
//    public double getTotalRate() {
//        return totalRate;
//    }
//
//    public void setTotalRate(double totalRate) {
//        this.totalRate = totalRate;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public User getClinicUser() {
//        return clinicUser;
//    }
//
//    public void setClinicUser(User clinicUser) {
//        this.clinicUser = clinicUser;
//    }
//
//    public User getFreelancer() {
//        return freelancer;
//    }
//
//    public void setFreelancer(User freelancer) {
//        this.freelancer = freelancer;
//    }
//
//    public Clinic getClinic() {
//        return clinic;
//    }
//
//    public void setClinic(Clinic clinic) {
//        this.clinic = clinic;
//    }
//}


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JobPost {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("startDateTime")
    @Expose
    private String startDateTime;
    @SerializedName("endDateTime")
    @Expose
    private String endDateTime;
    @SerializedName("ratePerHour")
    @Expose
    private Double ratePerHour;
    @SerializedName("totalRate")
    @Expose
    private Double totalRate;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("clinicUser")
    @Expose
    private Object clinicUser;
    @SerializedName("freelancer")
    @Expose
    private Object freelancer;
    @SerializedName("clinic")
    @Expose
    private Clinic clinic;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public Double getRatePerHour() {
        return ratePerHour;
    }

    public void setRatePerHour(Double ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    public Double getTotalRate() {
        return totalRate;
    }

    public void setTotalRate(Double totalRate) {
        this.totalRate = totalRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getClinicUser() {
        return clinicUser;
    }

    public void setClinicUser(Object clinicUser) {
        this.clinicUser = clinicUser;
    }

    public Object getFreelancer() {
        return freelancer;
    }

    public void setFreelancer(Object freelancer) {
        this.freelancer = freelancer;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

}

