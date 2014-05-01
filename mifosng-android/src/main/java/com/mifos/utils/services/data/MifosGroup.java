package com.mifos.utils.services.data;

import com.google.gson.Gson;
import com.mifos.objects.db.Level;
import com.mifos.objects.db.Staff;

public class MifosGroup {
    public int groupId;
    public String groupName;
    public int staffId;
    public String staffName;

    public int levelId;
    public String levelName;

    public Client[] clients;


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public com.mifos.objects.db.MifosGroup getData() {
        com.mifos.objects.db.MifosGroup group = new com.mifos.objects.db.MifosGroup();
        group.setGroupId(this.groupId);
        group.setGroupName(this.groupName);

        Level level = new Level();
        level.setLevelId(this.levelId);
        level.setLevelName(this.levelName);
        level.setGroupId(group.getGroupId());

        Staff staff = new Staff();
        staff.setStaffId(this.staffId);
        staff.setStaffName(this.staffName);
        staff.setGroupId(group.getGroupId());

        group.setStaff(staff);
        group.setLevel(level);

        return group;
    }

}
