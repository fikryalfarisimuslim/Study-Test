package com.sunway.averychoke.studywifidirect3.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by AveryChoke on 27/1/2017.
 */

public class Mahasiswa implements Parcelable, Serializable {

    public static long mCounter = 0;

    private long mId;
    private String mMahasiswa;
    private String mCorrectAnswer;
    private String mUserAnswer;

    public Mahasiswa(String mahasiswa, String correctAnswer) {
        this(++mCounter, mahasiswa, correctAnswer,  "");
    }

    //for database
    public Mahasiswa(long id, String mahasiswa, String correctAnswer,  String userAnswer) {
        mId = id;
        mMahasiswa = mahasiswa;
        mCorrectAnswer = correctAnswer;
        mUserAnswer = userAnswer;
    }

    // for create mahassiwa
    public Mahasiswa() {
        this("", "");
    }

    // convert to mahasiswa
    public static Mahasiswa cloneFrom(Mahasiswa mahasiswa) {
        Mahasiswa cloneMahasiswa = new Mahasiswa(
                mahasiswa.getId(),
                mahasiswa.getMahasiswa(),
                mahasiswa.getCorrectAnswer(),
                "");
        return cloneMahasiswa;
    }

    public boolean checkAnswer() {
        return checkAnswer(mUserAnswer);
    }

    public boolean checkAnswer(String userAnswer) {
        return mCorrectAnswer.equalsIgnoreCase(userAnswer);
    }

    public void resetId() {
        mId = ++mCounter;
        mUserAnswer = "";
    }

    // region get set
    public long getId()
    {
        return mId;
    }

    public String getMahasiswa()
    {
        return mMahasiswa;
    }

    public void setMahasiswa(String mahasiswa)
    {
        mMahasiswa = mahasiswa;
    }

    public String getCorrectAnswer()
    {
        return mCorrectAnswer;
    }

    public void setCorrectAnswer(String correctAnswer)
    {
        mCorrectAnswer = correctAnswer;
    }


    public String getUserAnswer() {
        return mUserAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        mUserAnswer = userAnswer;
    }
    // endregion get set

    // region Parcelable
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(mId);
        out.writeString(mMahasiswa);
        out.writeString(mCorrectAnswer);
        out.writeString(mUserAnswer);
    }


    public static final Parcelable.Creator<Mahasiswa> CREATOR = new Parcelable.Creator<Mahasiswa>() {
        public Mahasiswa createFromParcel(Parcel in) {
            return new Mahasiswa(in);
        }

        public Mahasiswa[] newArray(int size) {
            return new Mahasiswa[size];
        }
    };

    protected Mahasiswa(Parcel in) {
        mId = in.readLong();
        mMahasiswa = in.readString();
        mCorrectAnswer = in.readString();
        mUserAnswer = in.readString();
    }
    // endregion Parcelable
}
