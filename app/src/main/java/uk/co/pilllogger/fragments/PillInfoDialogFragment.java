package uk.co.pilllogger.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import hugo.weaving.DebugLog;
import uk.co.pilllogger.R;
import uk.co.pilllogger.events.CreateConsumptionEvent;
import uk.co.pilllogger.events.DeletePillEvent;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;

/**
 * Created by Alex on 05/03/14.
 */
public class PillInfoDialogFragment extends InfoDialogFragment {

    private TextView _addConsumption;
    private TextView _delete;
    private TextView _editPill;
    private TextView _editPillSummary;
    private TextView _deletePillSummary;
    private TextView _addConsumptionSummary;

    public PillInfoDialogFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public PillInfoDialogFragment(Pill pill){
        super(pill);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.pill_info_dialog;
    }

    @Override @DebugLog
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();

        if(activity == null)
            return;

        _addConsumption = (TextView) activity.findViewById(R.id.info_dialog_add_consumption_title);
        _addConsumptionSummary = (TextView) activity.findViewById(R.id.info_dialog_add_consumption_summary);
        _delete = (TextView) activity.findViewById(R.id.info_dialog_delete_pill_title);
        _deletePillSummary = (TextView) activity.findViewById(R.id.info_dialog_delete_pill_summary);
        _editPill = (TextView) activity.findViewById(R.id.info_dialog_edit_pill_title);
        _editPillSummary = (TextView) activity.findViewById(R.id.info_dialog_edit_pill_summary);

        View editPillContainer = activity.findViewById(R.id.info_dialog_edit_pill);
        View addConsumptionContainer = activity.findViewById(R.id.info_dialog_take_now);
        View deletePillContainer = activity.findViewById(R.id.info_dialog_delete_pill);

        setTypeFace();

        if(_pill == null) {
            activity.finish();
            return;
        }

        _addConsumptionSummary.setText(getString(R.string.pill_info_dialog_take_now_prefix) + " " + _pill.getName() + " " + getString(R.string.pill_info_dialog_take_now_suffix));

        addConsumptionContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _bus.post(new CreateConsumptionEvent(_pill, PillInfoDialogFragment.this));
            }
        });
        deletePillContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _bus.post(new DeletePillEvent(_pill, PillInfoDialogFragment.this));
            }
        });
        editPillContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditPillFragment fragment = new EditPillFragment(_pill);
                FragmentManager fm = PillInfoDialogFragment.this.getActivity().getFragmentManager();
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                        .replace(R.id.export_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void setTypeFace() {
        Typeface typeface = State.getSingleton().getRobotoTypeface();
        _addConsumption.setTypeface(typeface);
        _addConsumptionSummary.setTypeface(typeface);
        _delete.setTypeface(typeface);
        _deletePillSummary.setTypeface(typeface);
        _editPill.setTypeface(typeface);
        _editPillSummary.setTypeface(typeface);
    }
}
