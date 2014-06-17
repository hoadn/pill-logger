package uk.co.pilllogger.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.pilllogger.R;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.Observer;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.views.ColourIndicator;

/**
 * Created by Alex on 05/03/14.
 */
public class PillInfoDialogFragment extends InfoDialogFragment {

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();

        if(activity == null)
            return;

        TextView addConsumption = (TextView) activity.findViewById(R.id.info_dialog_add_consumption);
        TextView delete = (TextView) activity.findViewById(R.id.info_dialog_delete);
        final TextView changeColour = (TextView) activity.findViewById(R.id.info_dialog_change_colour);
        TextView favourite = (TextView) activity.findViewById(R.id.info_dialog_set_favourite);
        TextView changeNameDosage = (TextView) activity.findViewById(R.id.info_dialog_set_name_dosage);
        TextView setReminders = (TextView) activity.findViewById(R.id.info_dialog_set_reminders);

        Typeface typeface = State.getSingleton().getTypeface();
        addConsumption.setTypeface(typeface);
        delete.setTypeface(typeface);
        changeColour.setTypeface(typeface);
        favourite.setTypeface(typeface);
        changeNameDosage.setTypeface(typeface);

        if(_pill == null) {
            activity.finish();
            return;
        }

        if (_pill.isFavourite())
            favourite.setText(getResources().getString(R.string.info_dialog_unset_favourite));

        addConsumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observer.getSingleton().notifyOnPillDialogAddConsumption(_pill, PillInfoDialogFragment.this);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observer.getSingleton().notifyOnPillDialogDelete(_pill, PillInfoDialogFragment.this);
            }
        });
        changeNameDosage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observer.getSingleton().notifyOnPillDialogChangeNameDosage(_pill, PillInfoDialogFragment.this);
            }
        });
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observer.getSingleton().notifyOnPillDialogFavourite(_pill, PillInfoDialogFragment.this);
            }

        });

        setReminders.setOnClickListener(new View.OnClickListener() {
            public PillRecurringFragment _selectDateFragment;

            @Override
            public void onClick(View v) {
                _selectDateFragment = new PillRecurringFragment();
                FragmentManager fm = PillInfoDialogFragment.this.getActivity().getFragmentManager();
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                        .replace(R.id.export_container, _selectDateFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        final uk.co.pilllogger.views.ColourIndicator colourTop = (uk.co.pilllogger.views.ColourIndicator) activity.findViewById(R.id.colour);
        changeColour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View colourHolder = activity.findViewById(R.id.info_dialog_colour_picker_container);
                final ViewGroup colourContainer = (ViewGroup) colourHolder.findViewById(R.id.colour_container);
                if (colourHolder.getVisibility() == View.VISIBLE) {
                    int colourCount = colourContainer.getChildCount();
                    for (int i = 0; i < colourCount; i++) {
                        View colourView = colourContainer.getChildAt(i);
                        if (colourView != null) {
                            colourView.setOnClickListener(null);
                        }
                    }
                    colourHolder.setVisibility(View.GONE);
                } else {
                    colourHolder.setVisibility(View.VISIBLE);
                    int colourCount = colourContainer.getChildCount();
                    for (int i = 0; i < colourCount; i++) {
                        View colourView = colourContainer.getChildAt(i);
                        if (colourView != null) {
                            colourView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int colour = ((ColourIndicator) view).getColour();
                                    colourTop.setColour(colour);
                                    colourHolder.setVisibility(View.GONE);
                                    _pill.setColour(colour);
                                    Observer.getSingleton().notifyOnPillDialogChangePillColour(_pill, PillInfoDialogFragment.this);
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public interface PillInfoDialogListener {
        public void onDialogAddConsumption(Pill pill, InfoDialogFragment dialog);
        public void onDialogDelete(Pill pill, InfoDialogFragment dialog);
        public void setDialogFavourite(Pill pill, InfoDialogFragment dialog);
        public void onDialogChangePillColour(Pill pill, InfoDialogFragment dialog);
        public void onDialogChangeNameDosage(Pill pill, InfoDialogFragment dialog);
    }
}
