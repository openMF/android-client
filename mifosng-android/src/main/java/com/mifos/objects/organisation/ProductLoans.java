/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.organisation;

/**
 * Created by nellyk on 12/15/2015.
 */
public class ProductLoans {
        private Integer id;
        private String name;
        private String shortName;
        private String description;

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

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return "loanproducts{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", shortName='" + shortName + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

