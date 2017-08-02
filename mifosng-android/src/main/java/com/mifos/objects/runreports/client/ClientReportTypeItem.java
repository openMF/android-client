package com.mifos.objects.runreports.client;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tarun on 03-08-17.
 */

public class ClientReportTypeItem implements Parcelable {

    private int parameter_id;

    private String parameter_name;

    private  String report_category;

    private int report_id;

    private String report_name;

    private String report_parameter_name;

    private String report_subtype;

    private String report_type;

    public int getParameterId() {
        return parameter_id;
    }

    public void setParameterId(int parameter_id) {
        this.parameter_id = parameter_id;
    }

    public String getParameterName() {
        return parameter_name;
    }

    public void setParameterName(String parameter_name) {
        this.parameter_name = parameter_name;
    }

    public String getReportCategory() {
        return report_category;
    }

    public void setReportCategory(String report_category) {
        this.report_category = report_category;
    }

    public int getReportId() {
        return report_id;
    }

    public void setReportId(int report_id) {
        this.report_id = report_id;
    }

    public String getReportName() {
        return report_name;
    }

    public void setReportName(String report_name) {
        this.report_name = report_name;
    }

    public String getReportParameterName() {
        return report_parameter_name;
    }

    public void setReportParameterName(String report_parameter_name) {
        this.report_parameter_name = report_parameter_name;
    }

    public String getReportSubtype() {
        return report_subtype;
    }

    public void setReportSubtype(String report_subtype) {
        this.report_subtype = report_subtype;
    }

    public String getReportType() {
        return report_type;
    }

    public void setReportType(String report_type) {
        this.report_type = report_type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.parameter_id);
        dest.writeString(this.parameter_name);
        dest.writeString(this.report_category);
        dest.writeInt(this.report_id);
        dest.writeString(this.report_name);
        dest.writeString(this.report_parameter_name);
        dest.writeString(this.report_subtype);
        dest.writeString(this.report_type);
    }

    public ClientReportTypeItem() {
    }

    protected ClientReportTypeItem(Parcel in) {
        this.parameter_id = in.readInt();
        this.parameter_name = in.readString();
        this.report_category = in.readString();
        this.report_id = in.readInt();
        this.report_name = in.readString();
        this.report_parameter_name = in.readString();
        this.report_subtype = in.readString();
        this.report_type = in.readString();
    }

    public static final Parcelable.Creator<ClientReportTypeItem> CREATOR = new
            Parcelable.Creator<ClientReportTypeItem>() {
        @Override
        public ClientReportTypeItem createFromParcel(Parcel source) {
            return new ClientReportTypeItem(source);
        }

        @Override
        public ClientReportTypeItem[] newArray(int size) {
            return new ClientReportTypeItem[size];
        }
    };

    @Override
    public String toString() {
        return "{paramId: " + parameter_id +
                "\nparamName: " + parameter_name +
                "\nreportCategory: " + report_category +
                "\nreportId: " + report_id +
                "\nreportName: " + report_name +
                "\nreportType: " + report_type;
    }
}
