package com.mifos.objects.templates.clients;

import com.mifos.objects.InterestType;

import java.util.Arrays;
import java.util.List;

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

/**
 * Created by rajan on 13/3/16.
 */
public class ClientsTemplate {

	private int [] activationDate;
	private int officeId;
	private List<OfficeOptions> officeOptions;
	private List<StaffOptions> staffOptions;
	private List<SavingProductOptions> savingProductOptions;
	private List<Options> genderOptions;
	private List<Options> clientTypeOptions;
	private List<Options> clientClassificationOptions;
	private List<InterestType> clientLegalFormOptions;

	public int[] getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(int[] activationDate) {
		this.activationDate = activationDate;
	}

	public int getOfficeId() {
		return officeId;
	}

	public void setOfficeId(int officeId) {
		this.officeId = officeId;
	}

	public List<OfficeOptions> getOfficeOptions() {
		return officeOptions;
	}

	public void setOfficeOptions(List<OfficeOptions> officeOptions) {
		this.officeOptions = officeOptions;
	}

	public List<StaffOptions> getStaffOptions() {
		return staffOptions;
	}

	public void setStaffOptions(List<StaffOptions> staffOptions) {
		this.staffOptions = staffOptions;
	}

	public List<SavingProductOptions> getSavingProductOptions() {
		return savingProductOptions;
	}

	public void setSavingProductOptions(List<SavingProductOptions> savingProductOptions) {
		this.savingProductOptions = savingProductOptions;
	}

	public List<Options> getGenderOptions() {
		return genderOptions;
	}

	public void setGenderOptions(List<Options> genderOptions) {
		this.genderOptions = genderOptions;
	}

	public List<Options> getClientTypeOptions() {
		return clientTypeOptions;
	}

	public void setClientTypeOptions(List<Options> clientTypeOptions) {
		this.clientTypeOptions = clientTypeOptions;
	}

	public List<InterestType> getClientLegalFormOptions() {
		return clientLegalFormOptions;
	}

	public void setClientLegalFormOptions(List<InterestType> clientLegalFormOptions) {
		this.clientLegalFormOptions = clientLegalFormOptions;
	}

	public List<Options> getClientClassificationOptions() {
		return clientClassificationOptions;
	}

	public void setClientClassificationOptions(List<Options> clientClassificationOptions) {
		this.clientClassificationOptions = clientClassificationOptions;
	}

	@Override
	public String toString() {
		return "ClientsTemplate{" +
				"activationDate=" + Arrays.toString(activationDate) +
				", officeId=" + officeId +
				", officeOptions=" + officeOptions +
				", staffOptions=" + staffOptions +
				", savingProductOptions=" + savingProductOptions +
				", genderOptions=" + genderOptions +
				", clientTypeOptions=" + clientTypeOptions +
				", clientClassificationOptions=" + clientClassificationOptions +
				", clientLegalFormOptions=" + clientLegalFormOptions +
				'}';
	}
}
