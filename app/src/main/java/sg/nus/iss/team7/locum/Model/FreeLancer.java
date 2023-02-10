package sg.nus.iss.team7.locum.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class FreeLancer implements Parcelable{

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("name")
    @Expose
    private String name;


    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("contact")
    @Expose
    private String contact;

    @SerializedName("medicalLicenseNo")
    @Expose
    private String medicalLicenseNo;

    @SerializedName("errorsFieldString")
    @Expose
    private String errorsFieldString;

    @SerializedName("deviceToken")
    @Expose
    private String deviceToken;

    public FreeLancer() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getErrorsFieldString() {
        return errorsFieldString;
    }

    public void setErrorsFieldString(String errorsFieldString) {
        this.errorsFieldString = errorsFieldString;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getMedicalLicenseNo() {
        return medicalLicenseNo;
    }

    public void setMedicalLicenseNo(String medicalLicenseNo) {
        this.medicalLicenseNo = medicalLicenseNo;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    protected FreeLancer(Parcel in) {
        id = in.readString();
        username = in.readString();
        password = in.readString();
        name = in.readString();
        email = in.readString();
        contact = in.readString();
        medicalLicenseNo = in.readString();
        errorsFieldString = in.readString();
      //  deviceToken = in .readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(contact);
        dest.writeString(medicalLicenseNo);
        dest.writeString(errorsFieldString);
      //  dest.writeString(deviceToken);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FreeLancer> CREATOR = new Creator<FreeLancer>() {
        @Override
        public FreeLancer createFromParcel(Parcel in) {
            return new FreeLancer(in);
        }

        @Override
        public FreeLancer[] newArray(int size) {
            return new FreeLancer[size];
        }
    };



}
