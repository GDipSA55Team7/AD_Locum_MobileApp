package sg.nus.iss.team7.locum.Model;

import android.os.Parcel;
import android.os.Parcelable;


public class PaymentDTO implements Parcelable {
    private Integer jobId;
    private Double jobRatePerHr,jobTotalRate;
    private String jobDescription,jobStartDateTime,jobEndDateTime;
    private String clinicName,clinicAddress,clinicPostalCode,clinicContact,clinicHciCode;
    private String flName,flEmail,flContact,flMedicalLicenseNo;

    public PaymentDTO(Integer jobId, Double jobRatePerHr,Double jobTotalRate,String jobDescription,String jobStartDateTime,String jobEndDateTime
                ,String clinicName, String clinicAddress,String clinicPostalCode, String clinicContact, String clinicHciCode
                      ,String flName,String flEmail, String flContact, String flMedicalLicenseN
    ){
        this.jobId = jobId;
        this.jobRatePerHr = jobRatePerHr;
        this.jobTotalRate = jobTotalRate;
        this.jobDescription = jobDescription;
        this.jobStartDateTime = jobStartDateTime;
        this.jobEndDateTime = jobEndDateTime;
        this.clinicName = clinicName;
        this.clinicAddress = clinicAddress;
        this.clinicPostalCode = clinicPostalCode;
        this.clinicContact = clinicContact;
        this.clinicHciCode = clinicHciCode;
        this.flName = flName;
        this.flEmail = flEmail;
        this.flContact = flContact;
        this.flMedicalLicenseNo = flMedicalLicenseN;
    }


    protected PaymentDTO(Parcel in){
        this.jobId = in.readInt();
        this.jobRatePerHr = in.readDouble();
        this.jobTotalRate = in.readDouble();
        this.jobDescription = in.readString();
        this.jobStartDateTime = in.readString();
        this.jobEndDateTime = in.readString();
        this.clinicName = in.readString();
        this.clinicAddress = in.readString();
        this.clinicPostalCode = in.readString();
        this.clinicContact = in.readString();
        this.clinicHciCode = in.readString();
        this.flName = in.readString();
        this.flEmail = in.readString();
        this.flContact = in.readString();
        this.flMedicalLicenseNo = in.readString();

    }


    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(jobId);
        out.writeDouble(jobRatePerHr);
        out.writeDouble(jobTotalRate);
        out.writeString(jobDescription);
        out.writeString(jobStartDateTime);
        out.writeString(jobEndDateTime);
        out.writeString(clinicName);
        out.writeString(clinicAddress);
        out.writeString(clinicPostalCode);
        out.writeString(clinicContact);
        out.writeString(clinicHciCode);
        out.writeString(flName);
        out.writeString(flEmail);
        out.writeString(flContact);
        out.writeString(flMedicalLicenseNo);

    }

    public static final Creator<PaymentDTO> CREATOR = new Creator<PaymentDTO>() {
        public PaymentDTO createFromParcel(Parcel in) {
            return new PaymentDTO(in);
        }

        public PaymentDTO[] newArray(int size) {
            return new PaymentDTO[size];
        }
    };

    public Double getJobRatePerHr() {
        return jobRatePerHr;
    }

    public void setJobRatePerHr(Double jobRatePerHr) {
        this.jobRatePerHr = jobRatePerHr;
    }

    public Double getJobTotalRate() {
        return jobTotalRate;
    }

    public void setJobTotalRate(Double jobTotalRate) {
        this.jobTotalRate = jobTotalRate;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobStartDateTime() {
        return jobStartDateTime;
    }

    public void setJobStartDateTime(String jobStartDateTime) {
        this.jobStartDateTime = jobStartDateTime;
    }

    public String getJobEndDateTime() {
        return jobEndDateTime;
    }

    public void setJobEndDateTime(String jobEndDateTime) {
        this.jobEndDateTime = jobEndDateTime;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }

    public String getClinicPostalCode() {
        return clinicPostalCode;
    }

    public void setClinicPostalCode(String clinicPostalCode) {
        this.clinicPostalCode = clinicPostalCode;
    }

    public String getClinicContact() {
        return clinicContact;
    }

    public void setClinicContact(String clinicContact) {
        this.clinicContact = clinicContact;
    }

    public String getClinicHciCode() {
        return clinicHciCode;
    }

    public void setClinicHciCode(String clinicHciCode) {
        this.clinicHciCode = clinicHciCode;
    }

    public String getFlName() {
        return flName;
    }

    public void setFlName(String flName) {
        this.flName = flName;
    }

    public String getFlEmail() {
        return flEmail;
    }

    public void setFlEmail(String flEmail) {
        this.flEmail = flEmail;
    }

    public String getFlContact() {
        return flContact;
    }

    public void setFlContact(String flContact) {
        this.flContact = flContact;
    }

    public String getFlMedicalLicenseNo() {
        return flMedicalLicenseNo;
    }

    public void setFlMedicalLicenseNo(String flMedicalLicenseNo) {
        this.flMedicalLicenseNo = flMedicalLicenseNo;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }
}
