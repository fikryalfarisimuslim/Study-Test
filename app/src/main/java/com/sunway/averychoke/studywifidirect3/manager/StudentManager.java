package com.sunway.averychoke.studywifidirect3.manager;

import android.content.Context;

import com.sunway.averychoke.studywifidirect3.controller.connection.ClassMaterialsRequestTask;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.DeviceClass;
import com.sunway.averychoke.studywifidirect3.model.Mahasiswa;
import com.sunway.averychoke.studywifidirect3.model.Meeting;
import com.sunway.averychoke.studywifidirect3.model.StudyMaterial;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 2/4/2017.
 */

public class StudentManager extends BaseManager {

    private static final StudentManager sInstance = new StudentManager();

    private DeviceClass mDeviceClass;
    private String mTeacherAddress;
    private boolean mOffline;
    private List<ClassMaterialsRequestTask> mTasks;

    private StudentManager() {
        super();
        mTasks = new ArrayList<>();
    }

    public static StudentManager getInstance() {
        return sInstance;
    }

    public void initialize(String className, Context context, boolean offline) {
        super.initialize(className, context);
        mOffline = offline;
    }

    // region Quiz
    public void updateQuizAnswers(Meeting meeting) {
        if (getDatabase() == null || getStudyClass() == null
                || getDatabase().updateQuizAnswers(meeting) == -1) {
            return;
        }

        int index = getStudyClass().getMeetingz().indexOf(meeting);
        getStudyClass().getMeetingz().set(index, meeting);
    }

    public void resetQuizAnswer(Meeting meeting) {
        meeting.setAnswered(false);
        for (Mahasiswa mahasiswa : meeting.getMahasiswa()) {
            mahasiswa.setUserAnswer("");
        }

        int index = getStudyClass().getMeetingz().indexOf(meeting);
        getStudyClass().getMeetingz().set(index, meeting);

        if (getDatabase() != null) {
            getDatabase().updateQuizAnswers(meeting);
        }
    }

    public void updateQuizStatus(Meeting meeting, ClassMaterial.Status status) {
        if (getStudyClass() != null) {
            int index = getStudyClass().getMeetingz().indexOf(meeting);
            meeting.setStatus(status);
            getStudyClass().getMeetingz().set(index, meeting);
        }
    }

    public void updateMeetingz(List<Meeting> meetingz) {
        if (getStudyClass() == null || getDatabase() == null) {
            return;
        }

        // update current data
        for (Meeting meeting : meetingz) {
            int index = getQuizIndex(meeting);
            if (index == -1) {
                meeting.resetId();
                getStudyClass().getMeetingz().add(meeting);
            } else if(getStudyClass().getMeetingz().get(index).getVersion() != meeting.getVersion()) {
                getStudyClass().getMeetingz().get(index).setStatus(ClassMaterial.Status.ERROR);
            }
        }

        // update database
        getDatabase().updateClassMeetingz(getStudyClass());
    }

    // used for conflict or if user want to dl a new version
    public Meeting updateQuiz(Meeting meeting) {
        if (getStudyClass() == null || getDatabase() == null || meeting == null) {
            return null;
        }

        int index = getQuizIndex(meeting);
        if (index != -1) {
            Meeting myMeeting = getStudyClass().getMeetingz().get(index);
            myMeeting.update(meeting);
            getDatabase().updateMeeting(myMeeting);
            return myMeeting;
        }
        return meeting;
    }
    
    private int getQuizIndex(Meeting meeting) {
        if (getStudyClass() != null) {
            for (int i = 0; i < getStudyClass().getMeetingz().size(); i++) {
                if (getStudyClass().getMeetingz().get(i).getName().equalsIgnoreCase(meeting.getName())) {
                    return i;
                }
            }
        }
        return -1;
    }
    // endregion

    // region Study Material
    public void updateStudyMaterials(List<String> studyMaterialsName) {
        if (getStudyClass() == null || getDatabase() == null) {
            return;
        }

        // store list of pending study materials
        List<StudyMaterial> pendingStudyMaterials = new ArrayList<>();
        for (StudyMaterial studyMaterial : getStudyClass().getStudyMaterials()) {
            if (studyMaterial.getStatus() == ClassMaterial.Status.PENDING) {
                pendingStudyMaterials.add(studyMaterial);
            }
        }

        // update current data
        for (String name : studyMaterialsName) {
            int index = getStudyMaterialIndex(name);
            if (index >= 0) {
                pendingStudyMaterials.remove(getStudyClass().getStudyMaterials().get(index));
            } else {
                getStudyClass().getStudyMaterials().add(new StudyMaterial(name));
            }
        }

        // remove old pending study materials
        if (pendingStudyMaterials.size() > 0) {
            getStudyClass().getStudyMaterials().removeAll(pendingStudyMaterials);
        }
    }

    // download a single study material
    public StudyMaterial updateStudyMaterial(StudyMaterial studyMaterial) {
        if (getStudyClass() == null || getDatabase() == null || studyMaterial == null) {
            return null;
        }

        int index = getStudyMaterialIndex(studyMaterial.getName());
        if (index != -1) {
            StudyMaterial myStudyMaterial = getStudyClass().getStudyMaterials().get(index);
            myStudyMaterial.update(studyMaterial);
            if (getDatabase().existStudyMaterial(myStudyMaterial)) {
                getDatabase().updateStudyMaterial(myStudyMaterial);
            } else {
                getDatabase().addStudyMaterial(myStudyMaterial, getClassName());
            }
            return myStudyMaterial;
        }
        return studyMaterial;
    }

    public void updateStudyMaterialStatus(StudyMaterial studyMaterial, ClassMaterial.Status status) {
        if (getStudyClass() != null) {
            int index = getStudyClass().getStudyMaterials().indexOf(studyMaterial);
            studyMaterial.setStatus(status);
            getStudyClass().getStudyMaterials().set(index, studyMaterial);
        }
    }

    private int getStudyMaterialIndex(String name) {
        if (getStudyClass() != null) {
            for (int i = 0; i < getStudyClass().getStudyMaterials().size(); i++) {
                if (getStudyClass().getStudyMaterials().get(i).getName().equalsIgnoreCase(name)) {
                    return i;
                }
            }
        }
        return -1;
    }
    // endregion

    // region Others
    public synchronized void addTask(ClassMaterialsRequestTask task) {
        mTasks.add(task);
    }

    public synchronized void removeTask(ClassMaterialsRequestTask task) {
        mTasks.remove(task);
    }

    public synchronized void killAllTasks() {
        for (ClassMaterialsRequestTask mTask : mTasks) {
            mTask.disconnect();
            mTask.cancel(true);
        }
        mTasks.clear();
    }
    // endregion

    // region Get Set
    public DeviceClass getDeviceClass() {
        return mDeviceClass;
    }

    public void setDeviceClass(DeviceClass deviceClass) {
        mDeviceClass = deviceClass;
    }

    public String getTeacherAddress() {
        return mTeacherAddress;
    }

    public void setTeacherAddress(String teacherAddress) {
        mTeacherAddress = teacherAddress;
    }

    public boolean isOffline() {
        return mOffline;
    }

    public void setOffline(boolean offline) {
        mOffline = offline;
    }
    // endregion
}
