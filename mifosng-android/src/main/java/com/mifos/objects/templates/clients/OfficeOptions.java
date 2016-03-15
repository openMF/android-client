package com.mifos.objects.templates.clients;


/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

/**
 * Created by rajan on 13/3/16.
 */

public class OfficeOptions{
		private int id;
		private String name;
		private String nameDecorated;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameDecorated() {
		return nameDecorated;
	}

	public void setNameDecorated(String nameDecorated) {
		this.nameDecorated = nameDecorated;
	}

	@Override
	public String toString() {
		return "OfficeOptions{" +
				"id=" + id +
				", name='" + name + '\'' +
				", nameDecorated='" + nameDecorated + '\'' +
				'}';
	}
}