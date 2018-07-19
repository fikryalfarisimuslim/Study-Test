package com.sunway.averychoke.studywifidirect3.controller.student_class.quiz;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseFragment;
import com.sunway.averychoke.studywifidirect3.controller.student_class.quiz.adapter.ResultQuestionsAdapter;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentQuizResultBinding;
import com.sunway.averychoke.studywifidirect3.model.Meeting;

/**
 * Created by AveryChoke on 4/4/2017.
 */

public class QuizResultFragment extends SWDBaseFragment {
    public static final String ARGS_QUIZ_KEY = "args_quiz_key";

    private FragmentQuizResultBinding mBinding;

    private Meeting mMeeting;
    private ResultQuestionsAdapter mAdapter;

    public static QuizResultFragment newInstance(Meeting meeting) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_QUIZ_KEY, meeting);

        QuizResultFragment fragment = new QuizResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMeeting = getArguments().getParcelable(ARGS_QUIZ_KEY);
        mAdapter = new ResultQuestionsAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quiz_result, container, false);
        mBinding = DataBindingUtil.bind(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.questionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.questionsRecyclerView.setAdapter(mAdapter);
        if (mMeeting != null) {
            mAdapter.setMahasiswa(mMeeting.getMahasiswa());
        }
    }
}
