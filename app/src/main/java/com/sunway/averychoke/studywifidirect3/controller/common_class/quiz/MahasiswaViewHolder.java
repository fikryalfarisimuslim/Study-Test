package com.sunway.averychoke.studywifidirect3.controller.common_class.quiz;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sunway.averychoke.studywifidirect3.databinding.CellQuestionBinding;
import com.sunway.averychoke.studywifidirect3.model.Mahasiswa;

/**
 * Created by AveryChoke on 4/4/2017.
 */

public class MahasiswaViewHolder extends RecyclerView.ViewHolder implements HasMahasiswa {

    private CellQuestionBinding mBinding;
    private Mahasiswa mMahasiswa;

    public MahasiswaViewHolder(View itemView) {
        super(itemView);

        mBinding = DataBindingUtil.bind(itemView);


    }



    @Override
    public void setMahasiswa(Mahasiswa mahasiswa, int lastIndex) {
        mMahasiswa = mahasiswa;


        if (mahasiswa != null) {
            mBinding.NIM.setText(mahasiswa.getMahasiswa());
            mBinding.NAMA.setText(mahasiswa.getCorrectAnswer());
        }
    }


    // region get set
    protected CellQuestionBinding getBinding() {
        return mBinding;
    }

    protected Mahasiswa getMahasiswa() {
        return mMahasiswa;
    }
    // endregion
}
