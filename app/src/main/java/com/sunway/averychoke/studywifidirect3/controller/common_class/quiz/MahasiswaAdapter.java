package com.sunway.averychoke.studywifidirect3.controller.common_class.quiz;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.model.Mahasiswa;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 3/4/2017.
 */

public class MahasiswaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Mahasiswa> mMahasiswa;
    private int mLastShortMahasiswaIndex = -1;

    public MahasiswaAdapter() {
        mMahasiswa = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_question, parent, false);
        MahasiswaViewHolder viewHolder = new MahasiswaViewHolder(rootView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HasMahasiswa) {
            HasMahasiswa mahasiswaVH = ((HasMahasiswa) holder);
            mahasiswaVH.setMahasiswa(mMahasiswa.get(position), mLastShortMahasiswaIndex);
        }
    }

    @Override
    public int getItemCount() {
        return mMahasiswa.size();
    }

    public void setMahasiswa(List<Mahasiswa> mahasiswa) {
        mMahasiswa.clear();
        mMahasiswa.addAll(mahasiswa);
        //findLastShortQuestionIndex();
        notifyDataSetChanged();
    }
/*
    private void findLastShortQuestionIndex() {
        for (int i = 0; i < mQuestions.size(); i++) {
            if (!(mQuestions.get(i) instanceof ChoiceQuestion)) {
                mLastShortQuestionIndex = i;
            }
        }
    }*/

    // region get set
    protected List<Mahasiswa> getQuestions() {
        return mMahasiswa;
    }
    // endregion
}
