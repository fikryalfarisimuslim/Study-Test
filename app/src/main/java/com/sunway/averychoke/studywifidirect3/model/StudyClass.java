package com.sunway.averychoke.studywifidirect3.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 27/1/2017.
 */

public class StudyClass {

    private final String mName;
    private List<Meeting> mMeetingz;
    private List<StudyMaterial> mStudyMaterials;

    public StudyClass(String name) {
        mName = name;
        mMeetingz = new ArrayList<>();
        mStudyMaterials = new ArrayList<>();
    }

    // for database
    public StudyClass(String name, List<Meeting> meetingz, List<StudyMaterial> studyMaterials) {
        mName = name;
        mMeetingz = meetingz;
        mStudyMaterials = studyMaterials;
    }

    // region get set
    public String getName() {
        return mName;
    }

    public List<Meeting> getMeetingz() {
        return mMeetingz;
    }

    public void setMeetingz(List<Meeting> meetingz) {
        mMeetingz = meetingz;
    }

    public List<StudyMaterial> getStudyMaterials() {
        return mStudyMaterials;
    }

    public void setStudyMaterials(List<StudyMaterial> studyMaterials) {
        mStudyMaterials = studyMaterials;
    }
    // endregion get set
}
