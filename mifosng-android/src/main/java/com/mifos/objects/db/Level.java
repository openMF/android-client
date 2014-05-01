package com.mifos.objects.db;


import com.orm.SugarRecord;

public class Level extends SugarRecord<Level>
{
    private int groupId;
    private int levelId;
    private String levelName;
    public int getGroupId()
    {
        return groupId;
    }

    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }
    public int getLevelId()
    {
        return levelId;
    }

    public void setLevelId(int levelId)
    {
        this.levelId = levelId;
    }

    public String getLevelName()
    {
        return levelName;
    }

    public void setLevelName(String levelName)
    {
        this.levelName = levelName;
    }

    @Override
    public String toString()
    {
        return "Level{" +
                "groupId=" + groupId +
                ", levelId=" + levelId +
                ", levelName='" + levelName + '\'' +
                '}';
    }
}
