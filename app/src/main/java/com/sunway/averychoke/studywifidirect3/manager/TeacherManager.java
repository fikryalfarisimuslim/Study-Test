package com.sunway.averychoke.studywifidirect3.manager;

import android.content.Context;
import android.support.annotation.Nullable;

import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.Meeting;
import com.sunway.averychoke.studywifidirect3.model.StudyMaterial;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by AveryChoke on 2/4/2017.
 */

public class TeacherManager extends BaseManager {

    private static final TeacherManager sInstance = new TeacherManager();

    private final Map<String, Meeting> mMeetingMap;
    private final Map<String, StudyMaterial> mStudyMaterialMap;
    private final ReentrantReadWriteLock mMeetingLock;
    private final ReentrantReadWriteLock mStudyMaterialLock;

    private TeacherManager() {
        super();
        mMeetingMap = new HashMap<>();
        mStudyMaterialMap = new HashMap<>();
        mMeetingLock = new ReentrantReadWriteLock();
        mStudyMaterialLock = new ReentrantReadWriteLock();
    }

    public static TeacherManager getInstance() {
        return sInstance;
    }

    @Override
    public void initialize(String className, Context context) {
        super.initialize(className, context);

        // clear data
        mMeetingMap.clear();
        mStudyMaterialMap.clear();

        // change the list into map
        for (Meeting meeting : getStudyClass().getMeetingz()) {
            mMeetingMap.put(meeting.getName().toLowerCase(), meeting);
        }
        for (StudyMaterial studyMaterial : getStudyClass().getStudyMaterials()) {
            mStudyMaterialMap.put(studyMaterial.getName().toLowerCase(), studyMaterial);
        }
    }

    // region Quiz
    public List<Meeting> getVisibleMeetingz() {
        mMeetingLock.readLock().lock();
        try {
            List<Meeting> visibleQuizzes = new ArrayList<>();
            for (Meeting meeting : getQuizzes()) {
                if (meeting.isVisible()) {
                    visibleQuizzes.add(meeting);
                }
            }
            return visibleQuizzes;
        } finally {
            mMeetingLock.readLock().unlock();
        }
    }

    public Meeting findMeeting(String name) {
        mMeetingLock.readLock().lock();
        try {
            Meeting meeting = mMeetingMap.get(name.toLowerCase());
            if (meeting != null && meeting.isVisible()) {
                return meeting;
            } else {
                return null;
            }
        } finally {
            mMeetingLock.readLock().unlock();
        }
    }

    public boolean addMeeting(Meeting meeting) {
        if (getDatabase() == null || getStudyClass() == null
                // save the Meeting to database
                || getDatabase().addMeeting(meeting, getStudyClass().getName()) == -1) {
            return false;
        }

        mMeetingLock.writeLock().lock();
        try {
            getStudyClass().getMeetingz().add(meeting);
            mMeetingMap.put(meeting.getName().toLowerCase(), meeting);
            return true;
        } finally {
            mMeetingLock.writeLock().unlock();
        }
    }

    public boolean updateMeeting(Meeting meeting, String oldName) {
        meeting.setVersion(meeting.getVersion() + 1);
        if (getDatabase() == null || getStudyClass() == null
                // update the quiz in database
                || getDatabase().updateMeeting(meeting) == -1) {
            return false;
        }

        int index = -1;
        mMeetingLock.readLock().lock();
        try {
            index = getStudyClass().getMeetingz().indexOf(meeting);
        } finally {
            mMeetingLock.readLock().unlock();
        }

        if (index >= 0) {
            mMeetingLock.writeLock().lock();
            try {
                getStudyClass().getMeetingz().set(index, meeting);
                mMeetingMap.remove(oldName);
                mMeetingMap.put(meeting.getName().toLowerCase(), meeting);
                return true;
            } finally {
                mMeetingLock.writeLock().unlock();
            }
        } else {
            return false;
        }
    }

    public void updateMeetingVisible(Meeting meeting) {
        if (getStudyClass() != null && getDatabase() != null) {
            getDatabase().updateClassMaterialVisible(meeting);

            mMeetingLock.readLock().lock();
            try {
                int index = getStudyClass().getMeetingz().indexOf(meeting);
                getStudyClass().getMeetingz().set(index, meeting);
                mMeetingMap.remove(meeting.getName());
                mMeetingMap.put(meeting.getName().toLowerCase(), meeting);
            } finally {
                mMeetingLock.readLock().unlock();
            }
        }
    }

    @Override
    public void deleteMeeting(Meeting meeting) {
        mMeetingLock.writeLock().lock();
        try {
            super.deleteMeeting(meeting);
            mMeetingMap.remove(meeting.getName().toLowerCase());
        } finally {
            mMeetingLock.writeLock().unlock();
        }
    }

    public boolean isMeetingNameConflicting(String newName, @Nullable String oldName) {
        if (oldName != null) {
            mMeetingLock.readLock().lock();
            try {
                return !newName.equalsIgnoreCase(oldName) && mMeetingMap.containsKey(newName.toLowerCase());
            } finally {
                mMeetingLock.readLock().unlock();
            }
        } else {
            return mMeetingMap.containsKey(newName.toLowerCase());
        }
    }
    // endregion

    // region Study Material
    public List<String> getVisibleStudyMaterialsName() {
        mStudyMaterialLock.readLock().lock();
        try {
            List<String> visibleNames = new ArrayList<>();
            for (StudyMaterial studyMaterial : getStudyMaterials()) {
                if (studyMaterial.isVisible() && studyMaterial.getStatus() != ClassMaterial.Status.ERROR) {
                    visibleNames.add(studyMaterial.getName());
                }
            }
            return visibleNames;
        } finally {
            mStudyMaterialLock.readLock().unlock();
        }
    }

    public StudyMaterial findStudyMaterial(String name) {
        mStudyMaterialLock.readLock().lock();
        try {
            StudyMaterial studyMaterial = mStudyMaterialMap.get(name.toLowerCase());
            if (studyMaterial != null && studyMaterial.isVisible() && studyMaterial.getStatus() != ClassMaterial.Status.ERROR) {
                return studyMaterial;
            } else {
                return null;
            }
        } finally {
            mStudyMaterialLock.readLock().unlock();
        }
    }

    public boolean addStudyMaterial(StudyMaterial studyMaterial) {
        if (getDatabase() == null || getStudyClass() == null
                // save the study material to database
                || getDatabase().addStudyMaterial(studyMaterial, getStudyClass().getName()) == -1) {
            return false;
        }

        mStudyMaterialLock.writeLock().lock();
        try {
            getStudyClass().getStudyMaterials().add(studyMaterial);
            mStudyMaterialMap.put(studyMaterial.getName().toLowerCase(), studyMaterial);
            return true;
        } finally {
            mStudyMaterialLock.writeLock().unlock();
        }
    }

    public void updateStudyMaterialVisible(StudyMaterial studyMaterial) {
        if (getStudyClass() != null && getDatabase() != null) {
            getDatabase().updateClassMaterialVisible(studyMaterial);

            mStudyMaterialLock.readLock().lock();
            try {
                int index = getStudyClass().getStudyMaterials().indexOf(studyMaterial);
                if (index >= 0) {
                    getStudyClass().getStudyMaterials().set(index, studyMaterial);
                    mStudyMaterialMap.put(studyMaterial.getName().toLowerCase(), studyMaterial);
                }
            } finally {
                mStudyMaterialLock.readLock().unlock();
            }
        }
    }

    public boolean renameStudyMaterial(StudyMaterial studyMaterial, String newName) {
        if (getStudyClass() != null && getDatabase() != null) {
            // get index
            int index = -1;
            mStudyMaterialLock.readLock().lock();
            try {
                index = getStudyClass().getStudyMaterials().indexOf(studyMaterial);
            } finally {
                mStudyMaterialLock.readLock().unlock();
            }

            StudyMaterial existingStudyMaterial = mStudyMaterialMap.get(newName.toLowerCase());
            File newFile = new File(studyMaterial.getFile().getParent(), newName);
            boolean success = (existingStudyMaterial == null || studyMaterial.equals(existingStudyMaterial))
                    && studyMaterial.getFile().renameTo(newFile);

            if (index >= 0 && success) {
                // rename
                String oldName = studyMaterial.getName();
                studyMaterial.setName(newName);
                studyMaterial.setFile(newFile);

                // change data
                mStudyMaterialLock.writeLock().lock();
                try {
                    getDatabase().updateStudyMaterial(studyMaterial);
                    getStudyClass().getStudyMaterials().set(index, studyMaterial);
                    mStudyMaterialMap.remove(oldName.toLowerCase());
                    mStudyMaterialMap.put(newName.toLowerCase(), studyMaterial);
                    return true;
                } finally {
                    mStudyMaterialLock.writeLock().unlock();
                }
            }
        }
        return false;
    }

    @Override
    public void deleteStudyMaterial(StudyMaterial studyMaterial) {
        mStudyMaterialLock.writeLock().lock();
        try {
            super.deleteStudyMaterial(studyMaterial);
            mStudyMaterialMap.remove(studyMaterial.getName().toLowerCase());
        } finally {
            mStudyMaterialLock.writeLock().unlock();
        }
    }
    // endregion

    // region Get Set

    // endregion
}
