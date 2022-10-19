package com.zog.android.syncsmb.ui.config;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.zog.android.syncsmb.R;

import android.util.Log;
/**
 * A placeholder fragment containing a simple view.
 */
public class LocalConfigFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    SharedPreferences config;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_local_config, container, false);


        config = this.getActivity().getSharedPreferences("ShareConfig", 0); // 0 - for private mode

        Button button = root.findViewById(R.id.buttonSaveLocalConfig);
        final EditText textPhoneName = root.findViewById(R.id.editPhoneName);
        final EditText textTimeHour = root.findViewById(R.id.editTimeHour);
        final EditText textPathOne = root.findViewById(R.id.editPathOne);
        final EditText textPathTwo = root.findViewById(R.id.editPathTwo);
        final EditText textPathThree = root.findViewById(R.id.editPathThree);

        String model=Build.MODEL;
        textPhoneName.setText(config.getString("PhoneName",model ));
        textTimeHour.setText(config.getString("TimeHour", "2"));

        textPathOne.setText(config.getString("PathOne", null));
        textPathTwo.setText(config.getString("PathTwo", null));
        textPathThree.setText(config.getString("PathThree", null));

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SharedPreferences.Editor editor = config.edit();
                editor.putString("PhoneName", textPhoneName.getText().toString());
                editor.putString("TimeHour", textTimeHour.getText().toString());
                editor.putString("PathOne", textPathOne.getText().toString());

                editor.putString("PathTwo", textPathTwo.getText().toString());
                editor.putString("PathThree", textPathThree.getText().toString());
                editor.commit();

                Intent returnIntent = new Intent();
                getActivity().setResult(Activity.RESULT_OK,returnIntent);
                getActivity().finish();

            }
        });

        return root;
    }
}