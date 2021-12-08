package com.green.bubuddies;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A fragment with the bottom menu that allows the user to switch between activities.
 * Each activity that includes this fragment must implement the BtmMenuActivity interface detailed below
 */
public class BottomMenu extends Fragment {

    //Public static final ints used to identify which activity the user is on/wants to change to.
    public static final int PROFILE = 001;
    public static final int PAIR = 002;
    public static final int MESSAGE = 003;
    public static final int STORE = 004;

    //Referenes to all of the buttons
    private Button btn_profile;
    private Button btn_pair;
    private Button btn_message;
    private Button btn_store;

    //Reference to context containing the BtmMenu
    public BtmMenuActivity currentAct;

    public BottomMenu() {
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
        View v = inflater.inflate(R.layout.fragment_bottom_menu, container, false);
        btn_profile = v.findViewById(R.id.btn_profile);
        btn_pair = v.findViewById(R.id.btn_pair);
        btn_message = v.findViewById(R.id.btn_message);
        btn_store = v.findViewById(R.id.btn_store);

        //add onClick listeners for each button
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentAct.changeActivity(PROFILE);
            }
        });
        btn_pair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentAct.changeActivity(PAIR);
            }
        });
        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentAct.changeActivity(MESSAGE);
            }
        });
        btn_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentAct.changeActivity(STORE);
            }
        });
        currentAct.updateClickableButtons();
        return v;
    }

    public interface BtmMenuActivity {
        /**
         * Within this method each activity will need to create the intent for the next activity,
         * put any relevant key value pairs, and start the next activity
         */
        void changeActivity(int nextAct);

        /**
         * Within this method each activity will need to update the button corresponding to the current
         * activity such that the button is unclickable and grayed out.
         */
        void updateClickableButtons();
    }

    public void onAttach(Context c) {
        super.onAttach(c);
        currentAct = (BtmMenuActivity) c;
    }

    /**
     * Called from within each activity's updateClickableButtons method, it grays out and disables
     * clicking of the button corresponding to the current activity
     */
    public void disableClick(int act){
        switch(act) {
            case (PROFILE):
                btn_profile.setClickable(false);
                btn_profile.setAlpha(.5f);
                btn_store.setClickable(true);
                btn_pair.setClickable(true);
                btn_message.setClickable(true);
                break;
            case (MESSAGE):
                btn_message.setClickable(false);
                btn_message.setAlpha(.5f);
                btn_store.setClickable(true);
                btn_pair.setClickable(true);
                btn_profile.setClickable(true);
                break;
            case (PAIR):
                btn_pair.setClickable(false);
                btn_pair.setAlpha(.5f);
                btn_store.setClickable(true);
                btn_profile.setClickable(true);
                btn_message.setClickable(true);
                break;
            case(STORE):
                btn_store.setClickable(false);
                btn_store.setAlpha(.5f);
                btn_profile.setClickable(true);
                btn_pair.setClickable(true);
                btn_message.setClickable(true);
                break;
        }
    }
}
