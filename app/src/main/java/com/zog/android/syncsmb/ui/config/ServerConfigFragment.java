package com.zog.android.syncsmb.ui.config;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import android.widget.Button;
import android.widget.EditText;

import com.zog.android.syncsmb.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ServerConfigFragment extends Fragment {

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
        View root = inflater.inflate(R.layout.fragment_server_config, container, false);


        config = this.getActivity().getSharedPreferences("ShareConfig", 0); // 0 - for private mode

        Button button = root.findViewById(R.id.buttonConfig);
        final EditText textServer = root.findViewById(R.id.editServer);
        final EditText textUsername = root.findViewById(R.id.editUsername);
        final EditText textPassword = root.findViewById(R.id.editPassword);
        final EditText textDomain = root.findViewById(R.id.editDomain);
        final EditText textShare = root.findViewById(R.id.editShare);

        textServer.setText(config.getString("server", "192.168.1.5"));
        textUsername.setText(config.getString("username", "Administrator"));
        textPassword.setText(config.getString("password", ""));
        textDomain.setText(config.getString("domain", "pc-name"));
        textShare.setText(config.getString("share", "testsmb"));

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String server = textServer.getText().toString();
                String username = textUsername.getText().toString();
                String password = textPassword.getText().toString();
                String domain = textDomain.getText().toString();
                String share = textShare.getText().toString();


                SharedPreferences.Editor editor = config.edit();
                editor.putString("server", server);
                editor.putString("username", username);
                editor.putString("password", password);
                editor.putString("domain", domain);
                editor.putString("share", share);
                editor.commit();

                Intent returnIntent = new Intent();
                getActivity().setResult(Activity.RESULT_OK,returnIntent);
                getActivity().finish();


            }
        });

        return root;
    }
}