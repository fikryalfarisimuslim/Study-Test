package com.sunway.averychoke.studywifidirect3.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 27/1/2017.
 */

public class Meeting extends ClassMaterial implements Parcelable, Serializable {
    private List<Mahasiswa> mMahasiswa;
    private boolean mAnswered;
    private int mVersion;

    public Meeting(String name) {
        super(name, false);
        mMahasiswa = new ArrayList<>();
        mAnswered = false;
        mVersion = 1;
    }

    //for database
    public Meeting(long meetingId, String name, List<Mahasiswa> mahasiswas, boolean answered, int version, boolean visible) {
        super(meetingId, name, visible);
        mAnswered = answered;
        mMahasiswa = mahasiswas;
        mVersion = version;
    }

    public void update(Meeting meeting) {
        mMahasiswa.clear();
        mMahasiswa.addAll(meeting.getMahasiswa());
        resetMahasiswaId();

        setStatus(Status.NORMAL);
        mVersion = meeting.mVersion;
    }

    public void resetId() {
        updateId();
        resetMahasiswaId();
    }

    private void resetMahasiswaId() {
        //mAnswered = false;
        for (Mahasiswa mahasiswa : mMahasiswa) {
            mahasiswa.resetId();
        }
    }

    @Override
    public boolean hasCheck() {
        return mAnswered;
    }

    // region get set
    public List<Mahasiswa> getMahasiswa()
    {
        return mMahasiswa;
    }

    public void setQuestions(List<Mahasiswa> mahasiswa) {
        mMahasiswa = mahasiswa;
    }

    public boolean isAnswered() {
        return mAnswered;
    }

    public void setAnswered(boolean answered) {
        mAnswered = answered;
    }

    public int getVersion() {
        return mVersion;
    }

    public void setVersion(int version) {
        mVersion = version;
    }
    // endregion get set

    // region Parcelable
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        // write for superclass first
        out.writeLong(getId());
        out.writeString(getName());
        out.writeInt(isVisible() ? 1 : 0); // cannot write as boolean so change to int instead
        out.writeSerializable(getStatus());

        out.writeList(mMahasiswa);
        out.writeInt(mAnswered ? 1 : 0);
        out.writeInt(mVersion);
    }

    public static final Parcelable.Creator<Meeting> CREATOR = new Parcelable.Creator<Meeting>() {
        public Meeting createFromParcel(Parcel in) {
            return new Meeting(in);
        }

        public Meeting[] newArray(int size) {
            return new Meeting[size];
        }
    };

    private Meeting(Parcel in) {
        super(in.readLong(), in.readString(), in.readInt() != 0, (Status)in.readSerializable());
        mMahasiswa = in.readArrayList(Mahasiswa.class.getClassLoader());
        mAnswered = in.readInt() != 0;
        mVersion = in.readInt();
    }
    // endregion Parcelable
}
