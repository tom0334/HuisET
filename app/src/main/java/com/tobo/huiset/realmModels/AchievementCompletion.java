package com.tobo.huiset.realmModels;

import io.realm.RealmObject;

public class AchievementCompletion extends RealmObject {

    private int achievement = -1;
    private long timeStamp;
    private String personID;

    public AchievementCompletion(){}

    public static AchievementCompletion create(int achievement, long timeStamp, String personID){
        AchievementCompletion comp = new AchievementCompletion();

        comp.personID = personID;
        comp.achievement = achievement;
        comp.timeStamp = timeStamp;
        return comp;
    }

    public int getAchievement() {
        return achievement;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getPersonID() {
        return personID;
    }
}
