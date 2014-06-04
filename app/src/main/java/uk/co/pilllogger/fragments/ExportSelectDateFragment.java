package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;

import org.joda.time.MutableDateTime;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.ExportActivity;
import uk.co.pilllogger.adapters.PillsListExportAdapter;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetPillsTask;

/**
 * Created by nick on 04/06/14.
 */
public class ExportSelectDateFragment extends PillLoggerFragmentBase {

    public static String DATE_FORMAT = "E, MMM dd, yyyy";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export_select_date, container, false);

        if(view != null) {
            TextView startDate = (TextView) view.findViewById(R.id.export_start_date_title);
            TextView endDate = (TextView) view.findViewById(R.id.export_end_date_title);
            TextView done = (TextView) view.findViewById(R.id.export_pills_done);
            done.setTypeface(State.getSingleton().getRobotoTypeface());
            startDate.setTypeface(State.getSingleton().getRobotoTypeface());
            endDate.setTypeface(State.getSingleton().getRobotoTypeface());
            final Activity activity = getActivity();

//            View startDateLayout = view.findViewById(R.id.export_start_date_layout);
//            final MutableDateTime finalDate = new MutableDateTime();
//            startDateLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
//                    CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
//                            .newInstance(new CalendarDatePickerDialog.OnDateSetListener() {
//                                             @Override
//                                             public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int monthOfYear, int dayOfMonth) {
//                                                 finalDate.setYear(year);
//                                                 finalDate.setMonthOfYear(monthOfYear + 1);
//                                                 finalDate.setDayOfMonth(dayOfMonth);
//
//                                                 String dateString = DateFormat.format(DATE_FORMAT, finalDate.toDate().getTime()).toString();
//                                             }
//                                         }, finalDate.getYear(), finalDate.getMonthOfYear() - 1,
//                                    finalDate.getDayOfMonth());
//                    calendarDatePickerDialog.show(fm, "Start Date Picker");
//                }
//            });


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


}
