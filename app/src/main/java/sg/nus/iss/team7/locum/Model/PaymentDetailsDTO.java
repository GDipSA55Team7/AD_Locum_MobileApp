package sg.nus.iss.team7.locum.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class PaymentDetailsDTO implements Parcelable {
    public static final Parcelable.Creator<PaymentDetailsDTO> CREATOR = new Parcelable.Creator<PaymentDetailsDTO>() {
        @Override
        public PaymentDetailsDTO createFromParcel(Parcel source) {
            return new PaymentDetailsDTO(source);
        }

        @Override
        public PaymentDetailsDTO[] newArray(int size) {
            return new PaymentDetailsDTO[size];
        }
    };
    private Integer jobId;
    private Double jobRatePerHr, jobTotalRate;
    private String jobDescription, jobStartDateTime, jobEndDateTime, jobAdditionalFees, jobPaymentDate, jobPaymentRefNo;
    private String clinicName, clinicAddress, clinicPostalCode, clinicContact, clinicHciCode;
    private String flName, flEmail, flContact, flMedicalLicenseNo;

    public PaymentDetailsDTO(Integer jobId, Double jobRatePerHr, Double jobTotalRate, String jobAdditionalFees, String jobDescription,
                             String jobStartDateTime, String jobEndDateTime, String jobPaymentDate, String jobPaymentRefNo,
                             String clinicName, String clinicAddress, String clinicPostalCode, String clinicContact, String clinicHciCode,
                             String flName, String flEmail, String flContact, String flMedicalLicenseNo) {
        this.jobId = jobId;
        this.jobRatePerHr = jobRatePerHr;
        this.jobTotalRate = jobTotalRate;
        this.jobAdditionalFees = jobAdditionalFees;
        this.jobDescription = jobDescription;
        this.jobStartDateTime = jobStartDateTime;
        this.jobEndDateTime = jobEndDateTime;
        this.jobPaymentDate = jobPaymentDate;
        this.jobPaymentRefNo = jobPaymentRefNo;

        this.clinicName = clinicName;
        this.clinicAddress = clinicAddress;
        this.clinicPostalCode = clinicPostalCode;
        this.clinicContact = clinicContact;
        this.clinicHciCode = clinicHciCode;

        this.flName = flName;
        this.flEmail = flEmail;
        this.flContact = flContact;
        this.flMedicalLicenseNo = flMedicalLicenseNo;

    }

    protected PaymentDetailsDTO(Parcel in) {
        Log.d("PaymentDetailsDTO", "Reading from parcel");
        this.jobId = (Integer) in.readValue(Integer.class.getClassLoader());
        Log.d("PaymentDetailsDTO", "jobId: " + this.jobId);
        this.jobRatePerHr = (Double) in.readValue(Double.class.getClassLoader());
        Log.d("PaymentDetailsDTO", "jobRatePerHr: " + this.jobRatePerHr);
        this.jobTotalRate = (Double) in.readValue(Double.class.getClassLoader());
        Log.d("PaymentDetailsDTO", "jobTotalRate: " + this.jobTotalRate);

        this.jobAdditionalFees = in.readString();
        Log.d("PaymentDetailsDTO", "jobAdditionalFees: " + this.jobAdditionalFees);
        this.jobDescription = in.readString();
        Log.d("PaymentDetailsDTO", "jobDescription: " + this.jobDescription);
        this.jobStartDateTime = in.readString();
        Log.d("PaymentDetailsDTO", "jobStartDateTime: " + this.jobStartDateTime);
        this.jobEndDateTime = in.readString();
        Log.d("PaymentDetailsDTO", "jobEndDateTime: " + this.jobEndDateTime);
        this.jobPaymentDate = in.readString();
        Log.d("PaymentDetailsDTO", "jobPaymentDate: " + this.jobPaymentDate);
        this.jobPaymentRefNo = in.readString();
        Log.d("PaymentDetailsDTO", "jobPaymentRefNo: " + this.jobPaymentRefNo);

        this.clinicName = in.readString();
        Log.d("PaymentDetailsDTO", "clinicName: " + this.clinicName);
        this.clinicAddress = in.readString();
        Log.d("PaymentDetailsDTO", "clinicAddress: " + this.clinicAddress);
        this.clinicPostalCode = in.readString();
        Log.d("PaymentDetailsDTO", "clinicPostalCode: " + this.clinicPostalCode);
        this.clinicContact = in.readString();
        Log.d("PaymentDetailsDTO", "clinicContact: " + this.clinicContact);
        this.clinicHciCode = in.readString();
        Log.d("PaymentDetailsDTO", "clinicHciCode: " + this.clinicHciCode);
        this.flName = in.readString();
        Log.d("PaymentDetailsDTO", "flName: " + this.flName);
        this.flEmail = in.readString();
        Log.d("PaymentDetailsDTO", "flEmail: " + this.flEmail);
        this.flContact = in.readString();
        Log.d("PaymentDetailsDTO", "flContact: " + this.flContact);
        this.flMedicalLicenseNo = in.readString();
        Log.d("PaymentDetailsDTO", "flMedicalLicenseNo: " + this.flMedicalLicenseNo);


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.d("PaymentDetailsDTO", "Writing to parcel");
        dest.writeValue(this.jobId);
        Log.d("PaymentDetailsDTO", "Writing jobId: " + this.jobId);
        dest.writeValue(this.jobRatePerHr);
        Log.d("PaymentDetailsDTO", "Writing jobRatePerHr: " + this.jobRatePerHr);
        dest.writeValue(this.jobTotalRate);
        Log.d("PaymentDetailsDTO", "Writing jobTotalRate: " + this.jobTotalRate);
        dest.writeString(this.jobAdditionalFees);
        Log.d("PaymentDetailsDTO", "Writing jobAdditionalFees: " + this.jobAdditionalFees);
        dest.writeString(this.jobDescription);
        Log.d("PaymentDetailsDTO", "Writing jobDescription: " + this.jobDescription);
        dest.writeString(this.jobStartDateTime);
        Log.d("PaymentDetailsDTO", "Writing jobStartDateTime: " + this.jobStartDateTime);
        dest.writeString(this.jobEndDateTime);
        Log.d("PaymentDetailsDTO", "Writing jobEndDateTime: " + this.jobEndDateTime);
        dest.writeString(this.jobPaymentDate);
        Log.d("PaymentDetailsDTO", "Writing jobPaymentDate: " + this.jobPaymentDate);
        dest.writeString(this.jobPaymentRefNo);
        Log.d("PaymentDetailsDTO", "Writing jobPaymentRefNo " + this.jobPaymentRefNo);


        dest.writeString(this.clinicName);
        Log.d("PaymentDetailsDTO", "Writing clinicName: " + this.clinicName);
        dest.writeString(this.clinicAddress);
        Log.d("PaymentDetailsDTO", "Writing clinicAddress: " + this.clinicAddress);
        dest.writeString(this.clinicPostalCode);
        Log.d("PaymentDetailsDTO", "Writing clinicPostalCode: " + this.clinicPostalCode);
        dest.writeString(this.clinicContact);
        Log.d("PaymentDetailsDTO", "Writing clinicContact: " + this.clinicContact);
        dest.writeString(this.clinicHciCode);
        Log.d("PaymentDetailsDTO", "Writing clinicHciCode: " + this.clinicHciCode);
        dest.writeString(this.flName);

        Log.d("PaymentDetailsDTO", "Writing flName: " + this.flName);
        dest.writeString(this.flEmail);
        Log.d("PaymentDetailsDTO", "Writing flEmail: " + this.flEmail);
        dest.writeString(this.flContact);
        Log.d("PaymentDetailsDTO", "Writing flContact: " + this.flContact);
        dest.writeString(this.flMedicalLicenseNo);
        Log.d("PaymentDetailsDTO", "Writing flMedicalLicenseNo: " + this.flMedicalLicenseNo);
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

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

    public String getJobAdditionalFees() {
        return jobAdditionalFees;
    }

    public void setJobAdditionalFees(String jobAdditionalFees) {
        this.jobAdditionalFees = jobAdditionalFees;
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

    public String getJobPaymentDate() {
        return jobPaymentDate;
    }

    public void setJobPaymentDate(String jobPaymentDate) {
        this.jobPaymentDate = jobPaymentDate;
    }

    public String getJobPaymentRefNo() {
        return jobPaymentRefNo;
    }

    public void setJobPaymentRefNo(String jobPaymentRefNo) {
        this.jobPaymentRefNo = jobPaymentRefNo;
    }
}
