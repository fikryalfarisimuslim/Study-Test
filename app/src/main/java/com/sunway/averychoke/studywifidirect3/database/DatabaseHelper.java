package com.sunway.averychoke.studywifidirect3.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sunway.averychoke.studywifidirect3.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 29/1/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // region All Static variables

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "presence.db";

    // region Class Table
    private static final String TABLE_CLASS = "class";
    // columns name
    private static final String CLASS_NAME = "name";
    // create table statement
    private static final String CREATE_TABLE_CLASS =
            "CREATE TABLE " + TABLE_CLASS + "("
                + CLASS_NAME + " TEXT PRIMARY KEY)";
    // endregion Class Table

    // region Class Material Table
    private static final String TABLE_CLASS_MATERIAL = "class_material";
    // columns name
    private static final String CLASS_MATERIAL_ID = "id";
    private static final String CLASS_MATERIAL_CLASS_NAME = "class_name";
    private static final String CLASS_MATERIAL_NAME = "name";
    private static final String CLASS_MATERIAL_VISIBLE = "visible";
    // create table statement
    private static final String CREATE_TABLE_CLASS_MATERIAL =
            "CREATE TABLE " + TABLE_CLASS_MATERIAL + "("
                    + CLASS_MATERIAL_ID + " INTEGER PRIMARY KEY,"
                    + CLASS_MATERIAL_CLASS_NAME + " TEXT NOT NULL,"
                    + CLASS_MATERIAL_NAME + " TEXT,"
                    + CLASS_MATERIAL_VISIBLE + " BOOLEAN,"
                    + "FOREIGN KEY(" + CLASS_MATERIAL_CLASS_NAME + ") REFERENCES " + TABLE_CLASS + "(" + CLASS_NAME + ") ON DELETE CASCADE)";
    // endregion Class Material Table

    // region Quiz Table
    private static final String TABLE_MEETING = "meeting";
    // columns names
    private static final String MEETING_ID = "id";
    private static final String MEETING_ANSWERED = "answered";
    private static final String MEETING_VERSION = "version";
    //create table statement
    private static final String CREATE_TABLE_MEETING =
            "CREATE TABLE " + TABLE_MEETING + "("
                    + MEETING_ID + " INTEGER PRIMARY KEY,"
                    + MEETING_ANSWERED + " BOOLEAN,"
                    + MEETING_VERSION + " INTEGER,"
                    + "FOREIGN KEY(" + MEETING_ID + ") REFERENCES " + TABLE_CLASS_MATERIAL + "(" + CLASS_MATERIAL_ID + ") ON DELETE CASCADE)";
    // endregion Quiz Table

    // region Question Table
    private static final String TABLE_MAHASISWA = "mahasiswa_table";
    //columns name
    private static final String MAHASISWA_ID = "id";
    private static final String MAHASISWA_PRESENCE_ID = "presence_id";
    private static final String MAHASISWA_MAHASISWA = "mahasiswa";
    private static final String MAHASISWA_CORRECT_ANSWER = "correct_answer";
    private static final String MAHASISWA_USER_ANSWER = "user_answer";
    //create table statement
    private static final String CREATE_TABLE_MAHASISWA =
            "CREATE TABLE " + TABLE_MAHASISWA + "("
                    + MAHASISWA_ID + " INTEGER PRIMARY KEY,"
                    + MAHASISWA_PRESENCE_ID + " INTEGER NOT NULL,"
                    + MAHASISWA_MAHASISWA + " TEXT,"
                    + MAHASISWA_CORRECT_ANSWER + " TEXT,"
                    + MAHASISWA_USER_ANSWER + " TEXT,"
                    + "FOREIGN KEY(" + MAHASISWA_PRESENCE_ID + ") REFERENCES " + TABLE_MEETING + "(" + MEETING_ID + ") ON DELETE CASCADE)";
    // endregion Question Table



    // region Study Material Table
    private static final String TABLE_STUDY_MATERIAL = "study_material";
    //columns names
    private static final String STUDY_MATERIAL_ID = "id";
    private static final String STUDY_MATERIAL_PATH = "path";
    //create table statement
    private static final String CREATE_TABLE_STUDY_MATERIAL =
            "CREATE TABLE " + TABLE_STUDY_MATERIAL + "("
                + STUDY_MATERIAL_ID + " INTEGER PRIMARY KEY,"
                + STUDY_MATERIAL_PATH + " TEXT,"
                + "FOREIGN KEY(" + STUDY_MATERIAL_ID + ") REFERENCES " + TABLE_CLASS_MATERIAL + "(" + CLASS_MATERIAL_ID + ") ON DELETE CASCADE)";
    // endregion Study Material Table

    // endregion All Static variables


    //Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CLASS);
        db.execSQL(CREATE_TABLE_CLASS_MATERIAL);
        db.execSQL(CREATE_TABLE_MEETING);
        db.execSQL(CREATE_TABLE_MAHASISWA);
        db.execSQL(CREATE_TABLE_STUDY_MATERIAL);
        //db.execSQL("INSERT INTO " + TABLE_QUESTION + " VALUES (null, 1, 1401456,'Fikry Al Farisi Muslim')");
        //db.execSQL("INSERT INTO " + TABLE_QUESTION + " VALUES (null, 1, 1111111,'Al Fikhan')");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_MATERIAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEETING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAHASISWA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDY_MATERIAL);

        // Create tables again
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    // region --------------- SQL FUNCTIONS ---------------------------------------

    // region Class Table
    public long addClass(StudyClass studyClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        ContentValues values = new ContentValues();
        values.put(CLASS_NAME, studyClass.getName());

        // insert row
        long id = db.insert(TABLE_CLASS, null, values);

        // insert quizzes
        for (Meeting meeting : studyClass.getMeetingz()) {
            addMeeting(meeting, studyClass.getName());
        }
        //insert study materials
        for (StudyMaterial studyMaterial : studyClass.getStudyMaterials()) {
            addStudyMaterial(studyMaterial, studyClass.getName());
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        return id;
    }

    public List<String> getAllClassesName() {
        List<String> classesName = new ArrayList<>();
        String selectQuery = "SELECT " + CLASS_NAME + " FROM " + TABLE_CLASS;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                do {
                    try {
                        classesName.add(c.getString(c.getColumnIndex(CLASS_NAME)));
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                } while (c.moveToNext());
            }
        }
        finally {
            if(c != null) {
                c.close();
            }
        }

        return classesName;
    }

    public StudyClass getClass(String name) {
        StudyClass studyClass = null;
        String selectQuery =
                "SELECT * FROM " + TABLE_CLASS
                        + " WHERE " + CLASS_NAME + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, new String[]{ name });

            if (c.moveToFirst()) {
                try {
                    String className = c.getString(c.getColumnIndex(CLASS_NAME));
                    List<Meeting> meetingz = getClassMeetingz(className);
                    List<StudyMaterial> studyMaterials = getClassStudyMaterials(className);

                    studyClass = new StudyClass(className, meetingz, studyMaterials);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
            db.setTransactionSuccessful();
        } finally {
            if(c != null) {
                c.close();
            }
        }
        db.endTransaction();

        return studyClass;
    }

    public void updateClassMeetingz(StudyClass studyClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        // clear previous quizzes
        clearClassMeetingz(studyClass.getName());

        // insert new quizzes
        for (Meeting meeting : studyClass.getMeetingz()) {
            addMeeting(meeting, studyClass.getName());
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void deleteClass(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        //delete study material
        db.delete(TABLE_CLASS, CLASS_NAME + " = ?",
                new String[] { name });
    }
    // endregion Class Table

    // region Class Material Table
    private long addClassMaterial(ClassMaterial classMaterial, String className) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CLASS_MATERIAL_ID, classMaterial.getId());
        values.put(CLASS_MATERIAL_CLASS_NAME, className);
        values.put(CLASS_MATERIAL_NAME, classMaterial.getName());
        values.put(CLASS_MATERIAL_VISIBLE, classMaterial.isVisible());

        long id = db.insert(TABLE_CLASS_MATERIAL, null, values);
        return id;
    }

    private int updateClassMaterial(ClassMaterial classMaterial) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CLASS_MATERIAL_NAME, classMaterial.getName());
        values.put(CLASS_MATERIAL_VISIBLE, classMaterial.isVisible());

        // updating row
        return db.update(TABLE_CLASS_MATERIAL, values, CLASS_MATERIAL_ID + " = ?",
                new String[] { String.valueOf(classMaterial.getId()) });
    }

    public int updateClassMaterialVisible(ClassMaterial classMaterial) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CLASS_MATERIAL_VISIBLE, classMaterial.isVisible());

        // updating row
        return db.update(TABLE_CLASS_MATERIAL, values, CLASS_MATERIAL_ID + " = ?",
                new String[] { String.valueOf(classMaterial.getId()) });
    }

    public int deleteClassMaterial(ClassMaterial classMaterial) {
        SQLiteDatabase db = this.getWritableDatabase();

        //delete quiz
        return db.delete(TABLE_CLASS_MATERIAL, CLASS_MATERIAL_ID + " = ?",
                new String[] { String.valueOf(classMaterial.getId()) });
    }

    public long getClassMaterialMaxId() {
        return getMaxId(CLASS_MATERIAL_ID, TABLE_CLASS_MATERIAL);
    }
    // endregion Class Material Table

    // region Quiz Table
    public long addMeeting(Meeting meeting, String className) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        // insert superclass
        addClassMaterial(meeting, className);

        ContentValues values = new ContentValues();
        values.put(MEETING_ID, meeting.getId());
        values.put(MEETING_ANSWERED, meeting.isAnswered());
        values.put(MEETING_VERSION, meeting.getVersion());

        // insert row
        long id = db.insert(TABLE_MEETING, null, values);

        //insert questions
        for(Mahasiswa mahasiswa : meeting.getMahasiswa()) {
        /*Question question = new Question();
        question.setQuestion("1401456");
        question.setCorrectAnswer("Fikry");
        question.setUserAnswer("");*/
            addMahasiswa(mahasiswa, meeting.getId());
        }


        db.setTransactionSuccessful();
        db.endTransaction();
        return id;
    }

    // get the quizzes based on the class name
    public List<Meeting> getClassMeetingz(String className) {
        List<Meeting> meetingz = new ArrayList<>();
        String selectQuery =
                "SELECT " + TABLE_CLASS_MATERIAL + ".*," + TABLE_MEETING + ".*"
                        + " FROM " + TABLE_CLASS_MATERIAL + "," + TABLE_MEETING
                        + " WHERE " + TABLE_CLASS_MATERIAL + "." + CLASS_MATERIAL_ID + "=" + TABLE_MEETING + "." + MEETING_ID
                        + " AND " + TABLE_CLASS_MATERIAL + "." + CLASS_MATERIAL_CLASS_NAME + " =?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery,  new String[]{ className });

            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                do {
                    try {
                        long classMaterialId = c.getLong(c.getColumnIndex(CLASS_MATERIAL_ID));
                        List<Mahasiswa> meetingMahasiswa = getMeetingMahasiswa(classMaterialId);
                        Meeting meeting = new Meeting(
                                classMaterialId,
                                c.getString(c.getColumnIndex(CLASS_MATERIAL_NAME)),
                                meetingMahasiswa,
                                c.getInt(c.getColumnIndex(MEETING_ANSWERED)) != 0,
                                c.getInt(c.getColumnIndex(MEETING_VERSION)),
                                c.getInt(c.getColumnIndex(CLASS_MATERIAL_VISIBLE)) != 0
                        );

                        meetingz.add(meeting);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                } while (c.moveToNext());
            }
            return meetingz;
        }
        finally {
            if(c != null) {
                c.close();
            }
        }
    }

    public int updateMeeting(Meeting meeting) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        // update superclass
        updateClassMaterial(meeting);

        ContentValues values = new ContentValues();
        values.put(MEETING_ANSWERED, meeting.isAnswered());
        values.put(MEETING_VERSION, meeting.getVersion());

        //update the question list
        //clearQuizQuestion(quiz.getId());
        for(Mahasiswa mahasiswa : meeting.getMahasiswa()) {

                addMahasiswa(mahasiswa, meeting.getId());

        }

        // updating row
        int id = db.update(TABLE_MEETING, values, MEETING_ID + " = ?",
                new String[] { String.valueOf(meeting.getId()) });

        db.setTransactionSuccessful();
        db.endTransaction();
        return id;
    }

    public int updateQuizAnswers(Meeting meeting) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        ContentValues values = new ContentValues();
        values.put(MEETING_ANSWERED, meeting.isAnswered());

        // update questions
        for (Mahasiswa mahasiswa : meeting.getMahasiswa()) {
            updateQuestionAnswer(mahasiswa);
        }

        // updating row
        int id = db.update(TABLE_MEETING, values, MEETING_ID + " = ?",
                new String[] { String.valueOf(meeting.getId()) });

        db.setTransactionSuccessful();
        db.endTransaction();
        return id;
    }

    // delete all quizzes that belong to the class
    private void clearClassMeetingz(String className) {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereString = CLASS_MATERIAL_ID + " in ("
                + "SELECT " + TABLE_CLASS_MATERIAL + "." + CLASS_MATERIAL_ID
                + " FROM " + TABLE_CLASS_MATERIAL + "," + TABLE_MEETING
                + " WHERE " + TABLE_CLASS_MATERIAL + "." + CLASS_MATERIAL_ID + " = " +  TABLE_MEETING + "." + MEETING_ID
                + " AND " + CLASS_MATERIAL_CLASS_NAME + " = ? )";

        db.delete(TABLE_CLASS_MATERIAL, whereString,
                new String[] { String.valueOf(className)});
    }
    // endregion Quiz Table

    // region Question Table
    public long addMahasiswa(Mahasiswa mahasiswa, long meetingId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MAHASISWA_ID, mahasiswa.getId());
        values.put(MAHASISWA_PRESENCE_ID, meetingId);
        values.put(MAHASISWA_MAHASISWA, mahasiswa.getMahasiswa());
        values.put(MAHASISWA_CORRECT_ANSWER, mahasiswa.getCorrectAnswer());
        values.put(MAHASISWA_USER_ANSWER, mahasiswa.getUserAnswer());

        // insert row
        long id = db.insert(TABLE_MAHASISWA, null, values);

        return id;
    }

    public Mahasiswa getMahasiswa(long id) {
        Mahasiswa mahasiswa = null;
        String selectQuery =
                "SELECT * FROM " + TABLE_MAHASISWA
                        + " WHERE " + MAHASISWA_ID + " =?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});

            if (c.moveToFirst()) {
                try {
                    mahasiswa = new Mahasiswa(
                            c.getLong(c.getColumnIndex(MAHASISWA_ID)),
                            c.getString(c.getColumnIndex(MAHASISWA_MAHASISWA)),
                            c.getString(c.getColumnIndex(MAHASISWA_CORRECT_ANSWER)),
                            c.getString(c.getColumnIndex(MAHASISWA_USER_ANSWER))
                    );
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            } else {
                return null;
            }
        }
        finally {
            if(c != null) {
                c.close();
            }
        }
        return mahasiswa;
    }

    // get questions based on quiz id
    public List<Mahasiswa> getMeetingMahasiswa(long quizId) {
        List<Mahasiswa> mahasiswa = new ArrayList<>();

        String selectQuery =
                "SELECT " + MAHASISWA_ID + " FROM " + TABLE_MAHASISWA
                        + " WHERE " + MAHASISWA_PRESENCE_ID + " =?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, new String[]{String.valueOf(quizId)});

            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                do {
                    try {
                        long mahasiswaID = c.getLong(c.getColumnIndex(MAHASISWA_ID));

                            mahasiswa.add(getMahasiswa(mahasiswaID));

                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                } while (c.moveToNext());
            }
            return mahasiswa;
        }
        finally {
            if(c != null)
            {
                c.close();
            }
        }
    }

    public int updateQuestion(Mahasiswa mahasiswa) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MAHASISWA_MAHASISWA, mahasiswa.getMahasiswa());
        values.put(MAHASISWA_CORRECT_ANSWER, mahasiswa.getCorrectAnswer());

        // updating row
        return db.update(TABLE_MAHASISWA, values, MAHASISWA_ID + " = ?",
                new String[] { String.valueOf(mahasiswa.getId()) });
    }

    public int updateQuestionAnswer(Mahasiswa mahasiswa) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MAHASISWA_USER_ANSWER, mahasiswa.getUserAnswer());

        // updating row
        return db.update(TABLE_MAHASISWA, values, MAHASISWA_ID + " = ?",
                new String[] { String.valueOf(mahasiswa.getId()) });
    }

    // delete all questions that a quiz has
    public void clearQuizQuestion(long quizId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_MAHASISWA, MAHASISWA_PRESENCE_ID + " = ?",
                new String[] { String.valueOf(quizId)});
    }

    public long getMahasiswaMaxId()
    {
        return getMaxId(MAHASISWA_ID, TABLE_MAHASISWA);
    }
    // endregion Question Table



    // region Study Material Table
    public long addStudyMaterial(StudyMaterial studyMaterial, String className) {
        SQLiteDatabase db = this.getWritableDatabase();

        // insert superclass
        addClassMaterial(studyMaterial, className);

        ContentValues values = new ContentValues();
        values.put(STUDY_MATERIAL_ID, studyMaterial.getId());
        values.put(STUDY_MATERIAL_PATH, studyMaterial.getFile().getPath());

        // insert row
        return db.insert(TABLE_STUDY_MATERIAL, null, values);
    }

    // get study materials based on class name
    public List<StudyMaterial> getClassStudyMaterials(String className) {
        List<StudyMaterial> studyMaterials = new ArrayList<>();

        String selectQuery =
                "SELECT " + TABLE_CLASS_MATERIAL + ".*," + TABLE_STUDY_MATERIAL + ".*"
                        + " FROM " + TABLE_CLASS_MATERIAL + "," + TABLE_STUDY_MATERIAL
                        + " WHERE " + TABLE_CLASS_MATERIAL + "." + CLASS_MATERIAL_ID + "=" + TABLE_STUDY_MATERIAL + "." + STUDY_MATERIAL_ID
                        + " AND " + TABLE_CLASS_MATERIAL + "." + CLASS_MATERIAL_CLASS_NAME + " =?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, new String[]{className});

            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                do {
                    try {
                        StudyMaterial studyMaterial = new StudyMaterial(
                                c.getLong(c.getColumnIndex(CLASS_MATERIAL_ID)),
                                c.getString(c.getColumnIndex(CLASS_MATERIAL_NAME)),
                                c.getString(c.getColumnIndex(STUDY_MATERIAL_PATH)),
                                c.getInt(c.getColumnIndex(CLASS_MATERIAL_VISIBLE)) != 0
                        );
                        studyMaterials.add(studyMaterial);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                } while (c.moveToNext());
            }
        } finally {
            if(c != null) {
                c.close();
            }
        }

        return studyMaterials;
    }

    public int updateStudyMaterial(StudyMaterial studyMaterial) {
        SQLiteDatabase db = this.getWritableDatabase();

        // update superclass
        updateClassMaterial(studyMaterial);

        ContentValues values = new ContentValues();
        values.put(STUDY_MATERIAL_PATH, studyMaterial.getFile().getPath());

        // updating row
        return db.update(TABLE_STUDY_MATERIAL, values, STUDY_MATERIAL_ID + " = ?",
                new String[] { String.valueOf(studyMaterial.getId()) });
    }

    public boolean existStudyMaterial(StudyMaterial studyMaterial) {
        return existInTable(studyMaterial.getId(), TABLE_STUDY_MATERIAL, STUDY_MATERIAL_ID);
    }
    // endregion Study Material Table

    // region Helper SQL
    private boolean existInTable(long id, String table, String tableId) {
        String selectQuery =
                "SELECT 1 FROM " + table
                        + " WHERE " + tableId + " =?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
            boolean exists = c.moveToFirst();
            return exists;
        }
        finally {
            if(c != null)
            {
                c.close();
            }
        }
    }

    private long getMaxId(String columnID, String table) {
        String selectQuery = "SELECT MAX(" + columnID + ") FROM " + table;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                return c.getLong(0);
            } else {
                return 0;
            }
        }
        finally {
            if(c != null) {
                c.close();
            }
        }
    }
    // endregion Helper SQL

    // endregion --------------- SQL FUNCTIONS ---------------------------------------
}
