package com.anhdt.doranewsvermain.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anhdt.doranewsvermain.fragment.generalfragment.UpdateUIFollow;

public abstract class BaseFragmentNeedUpdateUI extends Fragment implements UpdateUIFollow {
    //Đây là Base cho những thằng con nằm trong
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("Fragment-x", activity.getLocalClassName());
        Log.d("Fragment-x", "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initProgressbar();
        Log.d("Fragment-x", "onCreate");
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d("Fragment-x", "onCreateView");
        return inflater.inflate(getFragmentLayout(), container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("Fragment-x", "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Fragment-x", "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Fragment-x", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Fragment-x", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Fragment-x", "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("Fragment-x", "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Fragment-x", "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("Fragment-x", "onDetach");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeComponents();
    }

    protected abstract void initializeComponents();

    protected abstract int getFragmentLayout();

    protected abstract void initProgressbar();
}
