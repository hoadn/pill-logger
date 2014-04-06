package uk.co.pilllogger.dialogs;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.pilllogger.R;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.UpdatePillTask;
import uk.co.pilllogger.views.ColourIndicator;

/**
 * Created by Alex on 05/03/14.
 */
public class PillInfoDialog extends InfoDialog {
    private PillInfoDialogListener _listener;

    public PillInfoDialog(){
        super();
    }

    public PillInfoDialog(Pill pill, PillInfoDialogListener listener) {
        super(pill);
        _listener = listener;
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
        if (_pill.isFavourite())
            favourite.setText(getResources().getString(R.string.info_dialog_unset_favourite));

        Typeface typeface = State.getSingleton().getTypeface();
        addConsumption.setTypeface(typeface);
        delete.setTypeface(typeface);
        changeColour.setTypeface(typeface);
        favourite.setTypeface(typeface);

        if(_pill == null) {
            dismiss();
            return;
        }

        addConsumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.onDialogAddConsumption(_pill, PillInfoDialog.this);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.onDialogDelete(_pill, PillInfoDialog.this);
            }
        });
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.setDialogFavourite(_pill, PillInfoDialog.this);
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
                                    _listener.onDialogChangePillColour(_pill, PillInfoDialog.this);
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
    }
}
