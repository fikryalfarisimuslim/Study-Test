package com.sunway.averychoke.studywifidirect3.controller.student_class.quiz;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseFragment;
import com.sunway.averychoke.studywifidirect3.controller.common_class.ClassMaterialAdapter;
import com.sunway.averychoke.studywifidirect3.controller.common_class.ClassMaterialViewHolder;
import com.sunway.averychoke.studywifidirect3.controller.connection.ClassMaterialsUpdaterListener;
import com.sunway.averychoke.studywifidirect3.controller.connection.ClassMaterialsRequestTask;
import com.sunway.averychoke.studywifidirect3.controller.connection.DownloadException;
import com.sunway.averychoke.studywifidirect3.controller.connection.TeacherThread;
import com.sunway.averychoke.studywifidirect3.database.DatabaseHelper;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentClassMaterialBinding;
import com.sunway.averychoke.studywifidirect3.manager.StudentManager;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.Question;
import com.sunway.averychoke.studywifidirect3.model.Quiz;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by AveryChoke on 29/1/2017.
 */

public class StudentQuizFragment extends SWDBaseFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        ClassMaterialViewHolder.OnClassMaterialSelectListener,
        ClassMaterialsUpdaterListener {
    public static final int ANSWER_QUIZ_CODE = 101;

    private StudentManager sManager;
    private ClassMaterialAdapter mAdapter;

    private FragmentClassMaterialBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sManager = StudentManager.getInstance();
        mAdapter = new ClassMaterialAdapter(
                false, this);

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

        mBinding.materialsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.materialsRecyclerView.setAdapter(mAdapter);
        mAdapter.setClassMaterials(sManager.getQuizzes());

        mBinding.addButton.setVisibility(View.GONE);


        if (!sManager.isOffline()) {
            mBinding.materialsSwipeRefreshLayout.setOnRefreshListener(this);
            mBinding.materialsSwipeRefreshLayout.setColorSchemeResources(R.color.color_primary);
            mBinding.materialsRecyclerView.setNestedScrollingEnabled(false);
            onRefresh();
        } else {
            mBinding.materialsSwipeRefreshLayout.setEnabled(false);
        }
       //
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ANSWER_QUIZ_CODE:
                if (resultCode == RESULT_OK) {
                    Quiz quiz = data.getParcelableExtra(AnswerQuizActivity.ARGS_QUIZ_KEY);
                    if (quiz != null) {
                        mAdapter.replaceClassMaterial(quiz);
                    }
                }
        }
    }

    @Override
    public void onRefresh() {
        mBinding.materialsSwipeRefreshLayout.setRefreshing(true);
        ClassMaterialsRequestTask task = new ClassMaterialsRequestTask(sManager.getTeacherAddress(), this);
        task.execute(TeacherThread.Request.QUIZZES);
    }

    // region class material view holder
    @Override
    public void onClassMaterialSelected(@NonNull final ClassMaterial classMaterial) {
        switch (classMaterial.getStatus()) {
            case DOWNLOADING:
                // // TODO: 10/4/2017 view download progress
                return;
            case ERROR:
                Toast.makeText(getContext(), "Anda sudah submit kehadiran!", Toast.LENGTH_SHORT).show();
                downloadQuiz((Quiz) classMaterial);
                //Toast.makeText(getContext(), "name"+classMaterial.getName(), Toast.LENGTH_SHORT).show();
                //Quiz quiz = sManager.updateQuiz();
                //classMaterial.getName();
                /*
                quiz.setAnswered(true);
                                sManager.updateQuizAnswers(quiz);
                                mAdapter.setClassMaterials(sManager.getQuizzes());
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.dialog_conflict_title)
                        .setMessage(R.string.dialog_conflict_message)
                        .setPositiveButton(R.string.dialog_download_from_teacher, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                downloadQuiz((Quiz) classMaterial);
                            }
                        })
                        .setNeutralButton(R.string.dialog_ignore_and_continue, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                answerQuiz(classMaterial);
                            }
                        })
                        .setNegativeButton(R.string.dialog_use_my_version, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sManager.updateQuizStatus((Quiz) classMaterial, ClassMaterial.Status.NORMAL);
                                mAdapter.replaceClassMaterial(classMaterial);
                            }
                        })
                        .show();*/
                break;
            default:
                    answerQuiz(classMaterial);
                break;
        }
    }

    @Override
    public void onClassMaterialLongClicked(@NonNull final ClassMaterial classMaterial, @NonNull final int index) {
        if (classMaterial.getStatus() == ClassMaterial.Status.DOWNLOADING) {
            return;
        }
        /*
        // region setup options
        final Quiz quiz = (Quiz) classMaterial;
        final List<CharSequence> options = new ArrayList<>();
        if (!sManager.isOffline()) {
            options.add(getString(R.string.option_download));
        }
        if (quiz.isAnswered()) {
            options.add(getString(R.string.option_reset_answer));
        }
        options.add(getString(R.string.option_delete));
        // endregion

        if (options.size() > 1) {
            new AlertDialog.Builder(getContext())
                    .setItems(options.toArray(new CharSequence[options.size()]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String option = options.get(which).toString();
                            if (option.equals(getString(R.string.option_download))) {
                                downloadQuiz(quiz);
                            } else if (option.equals(getString(R.string.option_reset_answer))) {
                                resetAnswer(quiz, index);
                            } else if (option.equals(getString(R.string.option_delete))) {
                                deleteQuiz(quiz, index);
                            }
                        }
                    })
                    .show();
        } else {
            deleteQuiz(quiz, index);
        }*/
    }

    @Override
    public void onClassMaterialChecked(@NonNull ClassMaterial classMaterial, @NonNull boolean isChecked) {

    }
    // endregion class material view holder

    // region Class Materials Updater
    @Override
    public void onClassMaterialUpdated(ClassMaterial classMaterial) {
        mBinding.materialsSwipeRefreshLayout.setRefreshing(false);

        mAdapter.replaceClassMaterial(classMaterial);

        if (getContext() != null) {
            //Toast.makeText(getContext(), getString(R.string.download_success_message, classMaterial.getName()), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClassMaterialsUpdated() {
        mAdapter.setClassMaterials(sManager.getQuizzes());
        mBinding.materialsSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onError(Exception e) {
        mBinding.materialsSwipeRefreshLayout.setRefreshing(false);
        if (e != null) {
            e.printStackTrace();
        }

        if (e instanceof DownloadException) {
            DownloadException downloadException = (DownloadException) e;
            ClassMaterial classMaterial = downloadException.getClassMaterial();
            if (classMaterial instanceof Quiz) {
                Quiz quiz = (Quiz) classMaterial;
                sManager.updateQuizStatus(quiz, downloadException.getInitialStatus());
                mAdapter.replaceClassMaterial(quiz);
            }
            if (getContext() != null) {
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        } else if (getContext() != null) {
            Toast.makeText(getContext(), R.string.student_class_connection_error_message, Toast.LENGTH_SHORT).show();
        }
    }
    // endregion

    private void answerQuiz(ClassMaterial classMaterial) {
        final Quiz quiz = (Quiz) classMaterial;

        //Intent intent = new Intent(getActivity(), AnswerQuizActivity.class);
        //intent.putExtra(AnswerQuizActivity.ARGS_QUIZ_KEY, (Parcelable) quiz);
        //startActivityForResult(intent, ANSWER_QUIZ_CODE);


        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final LinearLayout layout2 = new LinearLayout(getContext());
        layout2.setOrientation(LinearLayout.VERTICAL);

        final EditText NIM = new EditText(getContext());
        NIM.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        //NIM.setHint("NIM");
        layout.addView(NIM);

        final EditText NAME = new EditText(getContext());
        NAME.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        //NAME.setHint("Nama");
        layout2.addView(NAME);


        final Dialog dialog3 = new AlertDialog.Builder(getContext())
                .setTitle("Berhasil")
                .setMessage("Anda telah hadir!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();


        if(quiz.isAnswered()==false){
            final Dialog dialog = new AlertDialog.Builder(getContext())
                    .setTitle("Submit Kehadiran")
                    .setMessage("Masukkan NIM anda!")
                    .setView(layout)
                    .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(NIM.getText().toString().equals("")){
                                Toast.makeText(getContext(), "Mohon untuk isi NIM!", Toast.LENGTH_SHORT).show();
                            }else {
                                int status = 0;
                                List<Quiz> listQuiz = sManager.getQuizzes();
                                for (int i=0; i<listQuiz.size(); i++) {
                                    List<Question> question = listQuiz.get(i).getQuestions();
                                    for (int j=0; j<question.size(); j++) {
                                        if(NIM.getText().toString().equals(question.get(j).getQuestion())){
                                            status = 1;
                                        }
                                    }
                                }

                                if(status == 0) {
                                    final Dialog dialog2 = new AlertDialog.Builder(getContext())
                                            .setTitle("Submit Kehadiran")
                                            .setMessage("Masukkan nama lengkap anda!")
                                            .setView(layout2)
                                            .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (NAME.getText().toString().equals("")) {
                                                        Toast.makeText(getContext(), "Mohon untuk isi Nama!", Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        ClassMaterialsRequestTask task = new ClassMaterialsRequestTask(sManager.getTeacherAddress(), null, quiz);
                                                        task.execute(TeacherThread.Request.NIMNAMA, quiz.getName(), NIM.getText().toString(), NAME.getText().toString());
                                                        //Question question = new Question();
                                                        //question.getId();
                                                       // question.setQuestion(NIM.getText().toString());
                                                        //question.setCorrectAnswer(NAME.getText().toString());
                                                       // quiz.getQuestions().add(question);
                                                       // Quiz submit = sManager.updateQuiz(quiz);
                                                        /*quiz.setAnswered(true);
                                                        sManager.updateQuizAnswers(quiz);

                                                        mAdapter.setClassMaterials(sManager.getQuizzes());*/
                                                        downloadQuiz(quiz);
                                                        dialog3.show();

                                                    }
                                                }
                                            })
                                            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            }).show();
                                }else {
                                    //Question question = new Question();
                                    //question.getId();
                                    //question.setQuestion(NIM.getText().toString());
                                    //question.setCorrectAnswer(NAME.getText().toString());
                                    //quiz.getQuestions().add(question);
                                    ClassMaterialsRequestTask task = new ClassMaterialsRequestTask(sManager.getTeacherAddress(), null, quiz);
                                    task.execute(TeacherThread.Request.NIMNAMA, quiz.getName(), NIM.getText().toString(), NAME.getText().toString());

                                    /*quiz.setAnswered(true);
                                    sManager.updateQuizAnswers(quiz);
                                    mAdapter.setClassMaterials(sManager.getQuizzes());*/
                                    dialog3.show();
                                    downloadQuiz(quiz);
                                    //downloadQuiz((Quiz) classMaterial);
                                }

                            }
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
        }else{
            Toast.makeText(getContext(), "Anda sudah submit kehadiran!", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadQuiz(final Quiz quiz) {
        if (!sManager.isOffline()) {
            ClassMaterialsRequestTask task = new ClassMaterialsRequestTask(sManager.getTeacherAddress(), this, quiz);
            task.execute(TeacherThread.Request.QUIZ, quiz.getName());
            sManager.updateQuizStatus(quiz, ClassMaterial.Status.DOWNLOADING);
            quiz.setAnswered(true);
            sManager.updateQuizAnswers(quiz);
            mAdapter.setClassMaterials(sManager.getQuizzes());
            if(quiz.isAnswered()==false){
                quiz.setAnswered(true);
                Quiz submit = sManager.updateQuiz(quiz);
                sManager.updateQuizAnswers(quiz);
                mAdapter.setClassMaterials(sManager.getQuizzes());
            }

            //gimana caranya bisa true ketika update
            //sManager.updateQuizAnswers(quiz);
            //mAdapter.replaceClassMaterial(quiz);
            //Toast.makeText(getContext(), "--"+quiz.isAnswered(), Toast.LENGTH_SHORT).show();

        }
    }

    private void resetAnswer(final Quiz quiz, final int index) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.option_reset_answer)
                .setMessage(R.string.dialog_reset_answer_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sManager.resetQuizAnswer(quiz);
                        mAdapter.notifyItemChanged(index);
                        mAdapter.replaceClassMaterial(quiz);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void deleteQuiz(final Quiz quiz, final int index) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_delete_quiz_title)
                .setMessage(R.string.delete_quiz_dialog_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.removeClassMaterial(index);
                        sManager.deleteQuiz(quiz);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}
