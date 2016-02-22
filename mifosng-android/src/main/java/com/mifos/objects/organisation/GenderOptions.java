package com.mifos.objects.organisation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nkiboi on 12/15/2015.
 */
public class GenderOptions {
        private Integer id;
        private String name;

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


        @Override
        public String toString() {
            return "genderOptions{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }