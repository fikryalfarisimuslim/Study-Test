package com.sunway.averychoke.studywifidirect3.controller.common_class.quiz;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sunway.averychoke.studywifidirect3.databinding.CellQuestionBinding;
import com.sunway.averychoke.studywifidirect3.model.Question;

/**
 * Created by AveryChoke on 4/4/2017.
 */

public class QuestionViewHolder extends RecyclerView.ViewHolder implements HasQuestion {

    private CellQuestionBinding mBinding;
    private Question mQuestion;

    public QuestionViewHolder(View itemView) {
        super(itemView);

        mBinding = DataBindingUtil.bind(itemView);


    }



    @Override
    public void setQuestion(Question question, int lastIndex) {
        mQuestion = question;


        if (question != null) {
            mBinding.NIM.setText(question.getQuestion());
            mBinding.NAMA.setText(question.getCorrectAnswer());
        }
    }


    // region get set
    protected CellQuestionBinding getBinding() {
        return mBinding;
    }

    protected Question getQuestion() {
        return mQuestion;
    }
    // endregion
}
