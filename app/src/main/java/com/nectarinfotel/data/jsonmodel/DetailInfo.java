
package com.nectarinfotel.data.jsonmodel;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DetailInfo implements Parcelable
{

    @SerializedName("ticketid")
    @Expose
    private String ticketid;
    @SerializedName("department")
    @Expose
    private String department;
    @SerializedName("ref")
    @Expose
    private String ref;
    @SerializedName("caller")
    @Expose
    private String caller;
    @SerializedName("agent")
    @Expose
    private String agent;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("priority")
    @Expose
    private String priority;
    @SerializedName("status")
    @Expose
    private String status;
    public final static Creator<DetailInfo> CREATOR = new Creator<DetailInfo>() {


        @SuppressWarnings({
            "unchecked"
        })
        public DetailInfo createFromParcel(Parcel in) {
            return new DetailInfo(in);
        }

        public DetailInfo[] newArray(int size) {
            return (new DetailInfo[size]);
        }

    }
    ;

    protected DetailInfo(Parcel in) {
        this.ticketid = ((String) in.readValue((String.class.getClassLoader())));
        this.department = ((String) in.readValue((String.class.getClassLoader())));
        this.ref = ((String) in.readValue((String.class.getClassLoader())));
        this.caller = ((String) in.readValue((String.class.getClassLoader())));
        this.agent = ((String) in.readValue((String.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.startDate = ((String) in.readValue((String.class.getClassLoader())));
        this.priority = ((String) in.readValue((String.class.getClassLoader())));
        this.status = ((String) in.readValue((String.class.getClassLoader())));
    }

    public DetailInfo() {
    }

    public String getTicketid() {
        return ticketid;
    }

    public void setTicketid(String ticketid) {
        this.ticketid = ticketid;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(ticketid);
        dest.writeValue(department);
        dest.writeValue(ref);
        dest.writeValue(caller);
        dest.writeValue(agent);
        dest.writeValue(title);
        dest.writeValue(startDate);
        dest.writeValue(priority);
        dest.writeValue(status);
    }

    public int describeContents() {
        return  0;
    }

}
