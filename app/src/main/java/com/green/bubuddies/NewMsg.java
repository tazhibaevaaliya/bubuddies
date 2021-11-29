package com.green.bubuddies;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A fragment for starting a new conversation with an individual
 */
public class NewMsg extends Fragment {

    //references to views in fragment
    private Button btn_newMsg;
    private EditText txt_msg;

    //Reference to context containing the BtmMenu
    newMsgActivity currentAct;

    public NewMsg() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_msg, container, false);
        btn_newMsg = v.findViewById(R.id.btn_newMsg);
        txt_msg = v.findViewById(R.id.txt_msg);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btn_newMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentAct.newConversation();
            }
        });
    }

    public interface newMsgActivity {
        void newConversation();
    }

    public void onAttach(Context c){
        super.onAttach(c);
        currentAct = (newMsgActivity) c;
    }

}