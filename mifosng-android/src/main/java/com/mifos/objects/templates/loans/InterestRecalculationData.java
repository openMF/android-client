package com.mifos.objects.templates.loans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rajan Maurya on 16/07/16.
 */
public class InterestRecalculationData implements Parcelable {

    @SerializedName("id")
    Integer id;

    @SerializedName("productId")
    Integer productId;

    @SerializedName("interestRecalculationCompoundingType")
    InterestRecalculationCompoundingType interestRecalculationCompoundingType;

    @SerializedName("rescheduleStrategyType")
    RescheduleStrategyType rescheduleStrategyType;

    @SerializedName("recalculationRestFrequencyType")
    RecalculationRestFrequencyType recalculationRestFrequencyType;

    @SerializedName("recalculationRestFrequencyInterval")
    Integer recalculationRestFrequencyInterval;

    @SerializedName("isArrearsBasedOnOriginalSchedule")
    Boolean isArrearsBasedOnOriginalSchedule;

    @SerializedName("isCompoundingToBePostedAsTransaction")
    Boolean isCompoundingToBePostedAsTransaction;

    @SerializedName("preClosureInterestCalculationStrategy")
    PreClosureInterestCalculationStrategy preClosureInterestCalculationStrategy;

    @SerializedName("allowCompoundingOnEod")
    Boolean allowCompoundingOnEod;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public InterestRecalculationCompoundingType getInterestRecalculationCompoundingType() {
        return interestRecalculationCompoundingType;
    }

    public void setInterestRecalculationCompoundingType(
            InterestRecalculationCompoundingType interestRecalculationCompoundingType) {
        this.interestRecalculationCompoundingType = interestRecalculationCompoundingType;
    }

    public RescheduleStrategyType getRescheduleStrategyType() {
        return rescheduleStrategyType;
    }

    public void setRescheduleStrategyType(RescheduleStrategyType rescheduleStrategyType) {
        this.rescheduleStrategyType = rescheduleStrategyType;
    }

    public RecalculationRestFrequencyType getRecalculationRestFrequencyType() {
        return recalculationRestFrequencyType;
    }

    public void setRecalculationRestFrequencyType(RecalculationRestFrequencyType
                                                          recalculationRestFrequencyType) {
        this.recalculationRestFrequencyType = recalculationRestFrequencyType;
    }

    public Integer getRecalculationRestFrequencyInterval() {
        return recalculationRestFrequencyInterval;
    }

    public void setRecalculationRestFrequencyInterval(Integer recalculationRestFrequencyInterval) {
        this.recalculationRestFrequencyInterval = recalculationRestFrequencyInterval;
    }

    public Boolean getArrearsBasedOnOriginalSchedule() {
        return isArrearsBasedOnOriginalSchedule;
    }

    public void setArrearsBasedOnOriginalSchedule(Boolean arrearsBasedOnOriginalSchedule) {
        isArrearsBasedOnOriginalSchedule = arrearsBasedOnOriginalSchedule;
    }

    public Boolean getCompoundingToBePostedAsTransaction() {
        return isCompoundingToBePostedAsTransaction;
    }

    public void setCompoundingToBePostedAsTransaction(Boolean compoundingToBePostedAsTransaction) {
        isCompoundingToBePostedAsTransaction = compoundingToBePostedAsTransaction;
    }

    public PreClosureInterestCalculationStrategy getPreClosureInterestCalculationStrategy() {
        return preClosureInterestCalculationStrategy;
    }

    public void setPreClosureInterestCalculationStrategy(
            PreClosureInterestCalculationStrategy preClosureInterestCalculationStrategy) {
        this.preClosureInterestCalculationStrategy = preClosureInterestCalculationStrategy;
    }

    public Boolean getAllowCompoundingOnEod() {
        return allowCompoundingOnEod;
    }

    public void setAllowCompoundingOnEod(Boolean allowCompoundingOnEod) {
        this.allowCompoundingOnEod = allowCompoundingOnEod;
    }


    @Override
    public String toString() {
        return "InterestRecalculationData{" +
                "id=" + id +
                ", productId=" + productId +
                ", interestRecalculationCompoundingType=" + interestRecalculationCompoundingType +
                ", rescheduleStrategyType=" + rescheduleStrategyType +
                ", recalculationRestFrequencyType=" + recalculationRestFrequencyType +
                ", recalculationRestFrequencyInterval=" + recalculationRestFrequencyInterval +
                ", isArrearsBasedOnOriginalSchedule=" + isArrearsBasedOnOriginalSchedule +
                ", isCompoundingToBePostedAsTransaction=" + isCompoundingToBePostedAsTransaction +
                ", preClosureInterestCalculationStrategy=" +
                preClosureInterestCalculationStrategy +
                ", allowCompoundingOnEod=" + allowCompoundingOnEod +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.productId);
        dest.writeParcelable(this.interestRecalculationCompoundingType, flags);
        dest.writeParcelable(this.rescheduleStrategyType, flags);
        dest.writeParcelable(this.recalculationRestFrequencyType, flags);
        dest.writeValue(this.recalculationRestFrequencyInterval);
        dest.writeValue(this.isArrearsBasedOnOriginalSchedule);
        dest.writeValue(this.isCompoundingToBePostedAsTransaction);
        dest.writeParcelable(this.preClosureInterestCalculationStrategy, flags);
        dest.writeValue(this.allowCompoundingOnEod);
    }

    public InterestRecalculationData() {
    }

    protected InterestRecalculationData(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.productId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.interestRecalculationCompoundingType = in.readParcelable
                (InterestRecalculationCompoundingType.class.getClassLoader());
        this.rescheduleStrategyType = in.readParcelable(RescheduleStrategyType.class
                .getClassLoader());
        this.recalculationRestFrequencyType = in.readParcelable(RecalculationRestFrequencyType
                .class.getClassLoader());
        this.recalculationRestFrequencyInterval = (Integer) in.readValue(Integer.class
                .getClassLoader());
        this.isArrearsBasedOnOriginalSchedule = (Boolean) in.readValue(Boolean.class
                .getClassLoader());
        this.isCompoundingToBePostedAsTransaction = (Boolean) in.readValue(Boolean.class
                .getClassLoader());
        this.preClosureInterestCalculationStrategy = in.readParcelable
                (PreClosureInterestCalculationStrategy.class.getClassLoader());
        this.allowCompoundingOnEod = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<InterestRecalculationData> CREATOR =
            new Parcelable.Creator<InterestRecalculationData>() {
        @Override
        public InterestRecalculationData createFromParcel(Parcel source) {
            return new InterestRecalculationData(source);
        }

        @Override
        public InterestRecalculationData[] newArray(int size) {
            return new InterestRecalculationData[size];
        }
    };
}
