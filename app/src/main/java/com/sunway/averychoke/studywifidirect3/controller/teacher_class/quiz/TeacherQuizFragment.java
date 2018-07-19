package com.sunway.averychoke.studywifidirect3.controller.teacher_class.quiz;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseFragment;
import com.sunway.averychoke.studywifidirect3.controller.common_class.ClassMaterialAdapter;
import com.sunway.averychoke.studywifidirect3.controller.common_class.ClassMaterialViewHolder;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentClassMaterialBinding;
import com.sunway.averychoke.studywifidirect3.manager.TeacherManager;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.Meeting;

/**
 * Created by AveryChoke on 29/1/2017.
 */

public class TeacherQuizFragment extends SWDBaseFragment implements
        ClassMaterialViewHolder.OnClassMaterialSelectListener {

    private static final int CREATE_QUIZ_CODE = 101;
    private static final int EDIT_QUIZ_CODE = 102;

    private TeacherManager sManager;
    private ClassMaterialAdapter mAdapter;
    String mOldName;

    private FragmentClassMaterialBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sManager = TeacherManager.getInstance();
        mAdapter = new ClassMaterialAdapter(true, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_class_material, container, false);
        mBinding = DataBindingUtil.bind(rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.materialsSwipeRefreshLayout.setEnabled(false);

        mBinding.materialsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.materialsRecyclerView.setAdapter(mAdapter);
        mAdapter.setClassMaterials(sManager.getQuizzes());

        mBinding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(), CreateQuizActivity.class);
                //intent.putExtra(CreateQuizFragment.ARGS_TYPE_KEY, CreateQuizFragment.TYPE_CREATE);
                //startActivityForResult(intent, CREATE_QUIZ_CODE);
                createMeeting();

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CREATE_QUIZ_CODE:
                /*if (resultCode == Activity.RESULT_OK) {
                    // update the adapter
                    Quiz quiz = data.getParcelableExtra(CreateQuizFragment.ARGS_QUIZ_KEY);
                    if (quiz != null) {
                        mAdapter.addClassMaterial(quiz);
                    }
                }*/
                break;
            case EDIT_QUIZ_CODE:
                /*if (resultCode == Activity.RESULT_OK) {
                    // update the adapter
                    Quiz quiz = data.getParcelableExtra(CreateQuizFragment.ARGS_QUIZ_KEY);
                    if (quiz != null) {
                        mAdapter.replaceClassMaterial(quiz);
                    }
                }*/
                break;
        }
    }

    // region class material view holder
    @Override
    public void onClassMaterialSelected(@NonNull ClassMaterial classMaterial) {
        final Meeting meeting = (Meeting) classMaterial;

        /*
        final CharSequence[] choices = new CharSequence[] {
                getString(R.string.option_view_quiz),
                getString(R.string.option_edit_quiz)
        };

        new AlertDialog.Builder(getContext())
                .setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // View Quiz*/
                                Intent viewIntent = new Intent(getActivity(), ViewQuizActivity.class);
                                viewIntent.putExtra(ViewQuizActivity.ARGS_QUIZ_KEY, (Parcelable)meeting);
                                startActivity(viewIntent);
                                /*break;
                            case 1: // Edit Quiz
                                /*
                                Intent editIntent = new Intent(getActivity(), CreateQuizActivity.class);
                                editIntent.putExtra(CreateQuizFragment.ARGS_TYPE_KEY, CreateQuizFragment.TYPE_EDIT);
                                editIntent.putExtra(CreateQuizFragment.ARGS_QUIZ_KEY, (Parcelable)quiz);
                                startActivityForResult(editIntent, EDIT_QUIZ_CODE);
                                break;
                        }
                    }
                })
                .show();*/
    }

    @Override
    public void onClassMaterialLongClicked(@NonNull final ClassMaterial classMaterial, @NonNull final int index) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_delete_quiz_title)
                .setMessage(R.string.delete_quiz_dialog_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Meeting meeting = (Meeting) classMaterial;
                        mAdapter.removeClassMaterial(index);
                        sManager.deleteMeeting(meeting);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        dialog.show();
    }

    @Override
    public void onClassMaterialChecked(@NonNull ClassMaterial classMaterial, @NonNull boolean isChecked) {
        Meeting meeting = (Meeting) classMaterial;
        meeting.setVisible(isChecked);
        sManager.updateMeetingVisible(meeting);
    }
    // endregion class material view holder

    private void createMeeting() {
        final EditText editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        final Dialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Buat Pertemuan")
                .setMessage("Masukkan nama pertemuan")
                .setView(editText)
                .setPositiveButton(R.string.dialog_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String meetingName = editText.getText().toString();
                        if (sManager.isMeetingNameConflicting(meetingName, mOldName)) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle(R.string.dialog_invalid_quiz_name_title)
                                    .setMessage(R.string.dialog_invalid_quiz_name_message)
                                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .show();
                            return;
                        }

                        if (!TextUtils.isEmpty(meetingName.trim())) {
                            // mMeeting.setName(meetingName);

                            Meeting meeting = new Meeting(meetingName);

                            //quiz.getQuestions().add(question);

                            //mDatabase.addMeeting(meeting, sManager.getClassName());
                            boolean success = sManager.addMeeting(meeting);
                            //Toast.makeText(getContext(), quiz.getId()+ "-" + quiz.getName()+ "-" + quiz.getVersion(), Toast.LENGTH_SHORT).show();
                            if (success) {

                                mAdapter.setClassMaterials(sManager.getQuizzes());

                                //sManager.updateQuiz(mQuiz, quiz.getName());
                                //mDatabase.getQuizQuestions(quiz.getId());
                                Toast.makeText(getContext(), "Berhasil dibuat " + meeting.getName(), Toast.LENGTH_SHORT).show();
                                //mAdapter.addClassMaterial(meeting);
                                /*
                                Question question = new Question();
                                question.getId();
                                question.setQuestion("  NIM   ");
                                question.setCorrectAnswer("NAMA");
                                quiz.getQuestions().add(question);
                                boolean success2 = sManager.updateQuiz(quiz, quiz.getName());
                                if(success2){
                                    //mAdapter.replaceClassMaterial(quiz);
                                }*/
                                return; // successfully exited the method
                            }
                        }
                        // return not called, means got error
                        Toast.makeText(getContext(), R.string.create_class_failure_message, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}
