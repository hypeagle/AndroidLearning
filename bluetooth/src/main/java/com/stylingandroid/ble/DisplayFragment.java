package com.stylingandroid.ble;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hypeagle.bluetooth.R;

public class DisplayFragment extends Fragment {
    public static DisplayFragment newInstance() {
        return new DisplayFragment();
    }

    private TextView mTemperature = null;
    private TextView mHumidity = null;

    private OnDisplayFragmentInteractionListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_display, container, false);
        if (v != null) {
            mTemperature = (TextView) v.findViewById(R.id.temperature);
            mHumidity = (TextView) v.findViewById(R.id.humidity);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onDisplayFragmentInteraction();
                }
            });
        }
        return v;
    }

    public void setData(float temperature, float humidity) {
        if (mTemperature != null) {
            mTemperature.setText(getString(R.string.temp_format, temperature));
        }
        if (mHumidity != null) {
            mHumidity.setText(getString(R.string.humidity_format, humidity));
        }
    }

    public interface OnDisplayFragmentInteractionListener {
        public void onDisplayFragmentInteraction();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDisplayFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
}
