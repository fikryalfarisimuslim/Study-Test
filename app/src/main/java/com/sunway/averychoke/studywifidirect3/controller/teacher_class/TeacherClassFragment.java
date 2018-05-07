package com.sunway.averychoke.studywifidirect3.controller.teacher_class;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseFragment;
import com.sunway.averychoke.studywifidirect3.controller.teacher_class.quiz.TeacherQuizFragment;
import com.sunway.averychoke.studywifidirect3.controller.teacher_class.study_material.TeacherStudyMaterialFragment;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentClassBinding;
import com.sunway.averychoke.studywifidirect3.manager.TeacherManager;
import com.sunway.averychoke.studywifidirect3.model.Question;
import com.sunway.averychoke.studywifidirect3.model.Quiz;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 29/1/2017.
 */

public class TeacherClassFragment extends SWDBaseFragment {
    public interface OnTeacherRestartListener {
        void onTeacherRestart();
    }

    private TeacherManager sManager;

    private FragmentClassBinding mBinding;
    private ClassPagerFragmentAdapter mClassPagerFragmentAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sManager = TeacherManager.getInstance();
        mClassPagerFragmentAdapter = new ClassPagerFragmentAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_class, container, false);
        mBinding = DataBindingUtil.bind(rootView);
        getActivity().setTitle("(D) " + sManager.getClassName());
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        ((TabLayout) getActivity().findViewById(R.id.class_tab_layout)).setupWithViewPager(mBinding.classViewPager);
        mBinding.classViewPager.setAdapter(mClassPagerFragmentAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_class_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restart:

                if (getActivity() instanceof OnTeacherRestartListener) {
                    ((OnTeacherRestartListener) getActivity()).onTeacherRestart();
                }
                return true;
            case R.id.class_info:
                WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                String message = "";
                if (wifiManager != null && !wifiManager.isWifiEnabled()) {
                    message += getString(R.string.dialog_class_info_teacher_offline_message, sManager.getClassName());
                } else {
                    message = getString(R.string.dialog_class_info_teacher_online_message, sManager.getClassName());
                }
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.dialog_class_info_title)
                        .setMessage(message)
                        .show();
                return true;
            case R.id.rekap:
                List<Quiz> quiz = sManager.getQuizzes();
                StringBuffer buffer = new StringBuffer();
                List<String> NIM = new ArrayList<String>();
                List<String> Nama = new ArrayList<String>();
                List<Integer> Jumlah = new ArrayList<Integer>();
                Integer status = 0;

                for (int i=0; i<quiz.size(); i++) {
                    List<Question> question = quiz.get(i).getQuestions();
                    for (int j=0; j<question.size(); j++) {
                        Integer jumlahData = NIM.size();
                        status = 0;
                        int k = 0;
                        while(k<jumlahData && status == 0){
                            //jika data sudah ada
                            if(NIM.get(k).toString().equals(question.get(j).getQuestion())){
                                Integer value = Jumlah.get(k) + 1;
                                Jumlah.set(k,value);
                                if(Nama.get(k).toString().equals("")){
                                    Nama.set(k,Nama.get(k).toString());
                                }
                                status = 1;
                            }
                            k++;
                        }


                        //add data baru
                        if(jumlahData == 0 || status == 0){
                            NIM.add(question.get(j).getQuestion());
                            Nama.add(question.get(j).getCorrectAnswer());
                            Jumlah.add(1);
                        }
                    }
                }

                for (int i=0; i<NIM.size(); i++) {
                    buffer.append(NIM.get(i) + " - " + Nama.get(i)+"\n");
                    buffer.append("Kehadiran : "+Jumlah.get(i)+"\n\n");
                }




                new AlertDialog.Builder(getContext())
                        .setTitle("Rekap Kelas")
                        .setMessage(buffer.toString())
                        .show();

                //showMessage("Data", buffer.toString());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // adapter for the view pager
    class ClassPagerFragmentAdapter extends FragmentStatePagerAdapter {
        private String[] tabTitles = { getString(R.string.tab_quiz), getString(R.string.tab_study_material) };

        private ClassPagerFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TeacherQuizFragment();
                default:
                    return new TeacherStudyMaterialFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
