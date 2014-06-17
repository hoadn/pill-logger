package uk.co.pilllogger.dialogs;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.pilllogger.R;
import uk.co.pilllogger.fragments.ExportMainFragment;
import uk.co.pilllogger.fragments.ExportSelectDateFragment;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.Observer;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.UpdatePillTask;
import uk.co.pilllogger.views.ColourIndicator;

/**
 * Created by Alex on 05/03/14.
 */
public class PillInfoDialog extends InfoDialog {

    public PillInfoDialog(){
        super();
    }

    @SuppressLint("ValidFragment")
    public PillInfoDialog(Pill pill) {
        super(pill);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.pill_info_dialog;
    }

    @Override
    protected void setupMenu(View view) {

        TextView addConsumption = (TextView) view.findViewById(R.id.info_dialog_add_consumption);
        TextView delete = (TextView) view.findViewById(R.id.info_dialog_delete);
        final TextView changeColour = (TextView) view.findViewById(R.id.info_dialog_change_colour);
        TextView favourite = (TextView) view.findViewById(R.id.info_dialog_set_favourite);
        TextView changeNameDosage = (TextView) view.findViewById(R.id.info_dialog_set_name_dosage);
        TextView setReminders = (TextView) view.findViewById(R.id.info_dialog_set_reminders);

        Typeface typeface = State.getSingleton().getTypeface();
        addConsumption.setTypeface(typeface);
        delete.setTypeface(typeface);
        changeColour.setTypeface(typeface);
        favourite.setTypeface(typeface);
        changeNameDosage.setTypeface(typeface);

        if(_pill == null) {
            getActivity().finish();
            return;
        }

        if (_pill.isFavourite())
            favourite.setText(getResources().getString(R.string.info_dialog_unset_favourite));

        addConsumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observer.getSingleton().notifyOnPillDialogAddConsumption(_pill, PillInfoDialog.this);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observer.getSingleton().notifyOnPillDialogDelete(_pill, PillInfoDialog.this);
            }
        });
        changeNameDosage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observer.getSingleton().notifyOnPillDialogChangeNameDosage(_pill, PillInfoDialog.this);
            }
        });
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observer.getSingleton().notifyOnPillDialogFavourite(_pill, PillInfoDialog.this);
            }

        });

        setReminders.setOnClickListener(new View.OnClickListener() {
            public ExportSelectDateFragment _selectDateFragment;

            @Override
            public void onClick(View v) {
                _selectDateFragment = new ExportSelectDateFragment();
                FragmentManager fm = PillInfoDialog.this.getActivity().getFragmentManager();
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                        .replace(R.id.export_container, _selectDateFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        final View mainView = view;
        final uk.co.pilllogger.views.ColourIndicator colourTop = (uk.co.pilllogger.views.ColourIndicator) view.findViewById(R.id.colour);
        changeColour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View colourHolder = mainView.findViewById(R.id.info_dialog_colour_picker_container);
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
                                    Observer.getSingleton().notifyOnPillDialogChangePillColour(_pill, PillInfoDialog.this);
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public interface PillInfoDialogListener {
        public void onDialogAddConsumption(Pill pill, InfoDialog dialog);
        public void onDialogDelete(Pill pill, InfoDialog dialog);
        public void setDialogFavourite(Pill pill, InfoDialog dialog);
        public void onDialogChangePillColour(Pill pill, InfoDialog dialog);
        public void onDialogChangeNameDosage(Pill pill, InfoDialog dialog);
    }
}
