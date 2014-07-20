package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.pilllogger.R;
import uk.co.pilllogger.state.State;

/**
 * Created by nick on 04/06/14.
 */
public class PillRecurringFragment extends PillLoggerFragmentBase {

    private TextView _startTimeView;
    private TextView _endTimeView;
    private TextView _startTimeTitle;
    private TextView _endTimeTitle;
    private TextView _done;
    private TextView _exportTimeWarning;
    private View _clearStartTime;
    private View _clearEndTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_pill_recurring, container, false);

        if(view != null) {
            _startTimeTitle = (TextView) view.findViewById(R.id.export_start_time_title);
            _endTimeTitle = (TextView) view.findViewById(R.id.export_end_time_title);
            _done = (TextView) view.findViewById(R.id.export_pills_done);

            _startTimeView = (TextView) view.findViewById(R.id.export_start_time);
            _endTimeView = (TextView) view.findViewById(R.id.export_end_time);
            _exportTimeWarning = (TextView) view.findViewById(R.id.export_time_warning);
            _clearStartTime = view.findViewById(R.id.export_start_time_clear);
            _clearEndTime = view.findViewById(R.id.export_end_time_clear);

            setTypeface();

            final Activity activity = getActivity();


            _clearStartTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _startTimeView.setVisibility(View.GONE);
                    _clearStartTime.setVisibility(View.GONE);
                    _startTimeTitle.setText(getActivity().getResources().getString(R.string.export_start_time_select));
                }
            });

            _clearEndTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _endTimeView.setVisibility(View.GONE);
                    _clearEndTime.setVisibility(View.GONE);
                    _endTimeTitle.setText(getActivity().getResources().getString(R.string.export_end_time_select));
                }
            });



            View doneLayout = view.findViewById(R.id.export_pills_list_layout);
            doneLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.getFragmentManager().popBackStack();
                }
            });
        }


        return view;
    }

    private void setTypeface() {
        Typeface typeface = State.getSingleton().getRobotoTypeface();
        _done.setTypeface(typeface);
        _startTimeTitle.setTypeface(typeface);
        _endTimeTitle.setTypeface(typeface);
        _startTimeView.setTypeface(typeface);
        _endTimeView.setTypeface(typeface);
    }
}
