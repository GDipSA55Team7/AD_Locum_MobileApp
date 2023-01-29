//package sg.nus.iss.team7.locum.Model;
//
//import com.google.gson.annotations.SerializedName;
//
//public class Clinic {
//
//    @SerializedName("id")
//    private long id;
//    @SerializedName("name")
//    private String name;
//    @SerializedName("address")
//    private String address;
//    @SerializedName("postalCode")
//    private String postalCode;
//    @SerializedName("contact")
//    private String contact;
//    @SerializedName("hcicode")
//    private String hciCode;
//
//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public String getPostalCode() {
//        return postalCode;
//    }
//
//    public void setPostalCode(String postalCode) {
//        this.postalCode = postalCode;
//    }
//
//    public String getContact() {
//        return contact;
//    }
//
//    public void setContact(String contact) {
//        this.contact = contact;
//    }
//
//    public String getHciCode() {
//        return hciCode;
//    }
//
//    public void setHciCode(String hciCode) {
//        this.hciCode = hciCode;
//    }
//}

package sg.nus.iss.team7.locum.Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Clinic {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("postalCode")
    @Expose
    private String postalCode;
    @SerializedName("contact")
    @Expose
    private Object contact;
    @SerializedName("hcicode")
    @Expose
    private Object hcicode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Object getContact() {
        return contact;
    }

    public void setContact(Object contact) {
        this.contact = contact;
    }

    public Object getHcicode() {
        return hcicode;
    }

    public void setHcicode(Object hcicode) {
        this.hcicode = hcicode;
    }

}
