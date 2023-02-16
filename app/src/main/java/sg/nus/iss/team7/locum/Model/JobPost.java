package sg.nus.iss.team7.locum.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class JobPost implements Parcelable {

    public static final Creator<JobPost> CREATOR = new Creator<JobPost>() {
        @Override
        public JobPost createFromParcel(Parcel in) {
            return new JobPost(in);
        }

        @Override
        public JobPost[] newArray(int size) {
            return new JobPost[size];
        }
    };
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("startDateTime")
    @Expose
    private String startDateTime;
    @SerializedName("endDateTime")
    @Expose
    private String endDateTime;
    @SerializedName("paymentDate")
    @Expose
    private String paymentDate;
    @SerializedName("paymentRefNo")
    @Expose
    private String paymentRefNo;
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
    @SerializedName("similarityScore")
    @Expose
    private double similarity;
    @SerializedName("additionalFeeListString")
    @Expose
    private String additionalFeeListString;

    protected JobPost(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        similarity = in.readDouble();
        title = in.readString();
        description = in.readString();
        startDateTime = in.readString();
        endDateTime = in.readString();
        if (in.readByte() == 0) {
            paymentDate = null;
        } else {
            paymentDate = in.readString();
        }
        if (in.readByte() == 0) {
            paymentRefNo = null;
        } else {
            paymentRefNo = in.readString();
        }
        if (in.readByte() == 0) {
            ratePerHour = null;
        } else {
            ratePerHour = in.readDouble();
        }
        if (in.readByte() == 0) {
            totalRate = null;
        } else {
            totalRate = in.readDouble();
        }
        status = in.readString();
        additionalFeeListString = in.readString();
        if (clinic != null) {
            clinic = in.readParcelable(clinic.getClass().getClassLoader());
            if (in.readByte() == 0) {
                clinicUser = null;
                freelancer = null;
            } else {
                clinicUser = in.readParcelable(freelancer.getClass().getClassLoader());
                freelancer = in.readParcelable(freelancer.getClass().getClassLoader());
            }
        }

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeDouble(similarity);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(startDateTime);
        dest.writeString(endDateTime);
        dest.writeString(paymentDate);
        dest.writeString(paymentRefNo);
        if (ratePerHour == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(ratePerHour);
        }
        if (totalRate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(totalRate);
        }
        dest.writeString(status);
        dest.writeString(additionalFeeListString);
        if (clinic == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeValue(clinic);
        }
        if (clinicUser == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeValue(clinicUser);
        }
        if (freelancer == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeValue(freelancer);
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getAdditionalFeeListString() {
        return additionalFeeListString;
    }

    public void setAdditionalFeeListString(String additionalFeeListString) {
        this.additionalFeeListString = additionalFeeListString;
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentRefNo() {
        return paymentRefNo;
    }

    public void setPaymentRefNo(String paymentRefNo) {
        this.paymentRefNo = paymentRefNo;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }


    @Override
    public String toString() {
        return "JobPost{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", title='" + title + '\'' +
                ", startDateTime='" + startDateTime + '\'' +
                ", endDateTime='" + endDateTime + '\'' +
                ", paymentDate='" + paymentDate + '\'' +
                ", paymentRefNo='" + paymentRefNo + '\'' +
                ", ratePerHour=" + ratePerHour +
                ", totalRate=" + totalRate +
                ", status='" + status + '\'' +
                ", clinicUser=" + clinicUser +
                ", freelancer=" + freelancer +
                ", clinic=" + clinic +
                ", similarity=" + similarity +
                ", additionalFeeListString='" + additionalFeeListString + '\'' +
                '}';
    }
}

