package sg.nus.iss.team7.locum.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FreeLancer {


    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("userName")
    @Expose
    private String userName;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
}
