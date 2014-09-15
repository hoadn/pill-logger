package uk.co.pilllogger.adapters;

/**
 * Created by nick on 25/10/13.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.DialogActivity;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.views.ColourIndicator;


/**
 * Created by nick on 22/10/13.
 */
public class AddConsumptionPillListAdapter extends ArrayAdapter<Pill> {

    private static final int NEW = 0;
    private static final int EXISTING = 1;
    private List<Pill> _pills;
    private IConsumptionSelected _consumptionSelectedListener;
    private Context _context;
    private int _resourceId;
    private final boolean _showNewPill;
    private final ConsumptionRepository _consumptionRepository;

    public void addOpenPill(Pill pill) {
        State.getSingleton().addOpenPill(pill);
        _consumptionSelectedListener.setDoneEnabled(true);
    }

    public void removeOpenPill(Pill pill) {
        State.getSingleton().removeOpenPill(pill);
        if (!(State.getSingleton().getOpenPills().size() > 0))
            _consumptionSelectedListener.setDoneEnabled(false);
    }

    public void clearOpenPillsList() {
        State.getSingleton().clearOpenPillsList();
        State.getSingleton().clearConsumpedPills();
        _consumptionSelectedListener.setDoneEnabled(false);
    }

    public AddConsumptionPillListAdapter(Context context, IConsumptionSelected activity, int textViewResourceId, List<Pill> pills, boolean showNewPill, ConsumptionRepository consumptionRepository) {
        super(context, textViewResourceId, pills);
        _consumptionSelectedListener = activity;
        _context = context;
        _pills = pills;
        _resourceId = textViewResourceId;
        _showNewPill = showNewPill;
        _consumptionRepository = consumptionRepository;
    }

    public static class ViewHolder {
        public View container;

        public ViewHolder(View view) {
            container = view;
        }
    }

    public static class NewViewHolder extends ViewHolder{
        @InjectView(R.id.pill_list_create_new) public TextView create_new;

        public NewViewHolder(View view){
            super(view);
            ButterKnife.inject(this, view);
            setTypeFace();
        }

        private void setTypeFace(){
            Typeface typeface = State.getSingleton().getTypeface();
            create_new.setTypeface(typeface);
        }
    }

    public static class ExistingViewHolder extends ViewHolder {
        @InjectView(R.id.pill_list_name) public TextView name;
        @InjectView(R.id.pill_list_size) public TextView size;
        @InjectView(R.id.pill_list_units) public TextView units;
        @InjectView(R.id.pill_list_last_taken) public TextView lastTaken;
        @InjectView(R.id.add_consumption_after_click_layout) public View buttonLayout;
        @InjectView(R.id.add_consumption_amount) public TextView amount;
        @InjectView(R.id.add_consumption_pill_colour) public ColourIndicator color;

        public ExistingViewHolder(View view){
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    private void showNewPillDialog() {
        Intent intent = new Intent(_context, DialogActivity.class);
        intent.putExtra("DialogType", DialogActivity.DialogType.NewPill.ordinal());
        _context.startActivity(intent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            switch (rowType){
                case NEW:
                    v = inflater.inflate(R.layout.add_consumption_pill_list_new, null);
                    if(v != null){
                        holder = new NewViewHolder(v);
                        v.setTag(holder);
                    }
                    break;

                case EXISTING:
                    v = inflater.inflate(_resourceId, null);
                    if(v != null){
                        holder = new ExistingViewHolder(v);
                        v.setTag(holder);
                    }
                    break;
            }
        }
        else
            holder = (ViewHolder)v.getTag();

        switch(rowType) {
            case NEW:
                holder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showNewPillDialog();
                    }
                });
                break;

            case EXISTING:
                ExistingViewHolder existingViewHolder = (ExistingViewHolder) holder;
                Pill pill = _pills.get(position);
                if (pill != null && holder != null) {
                    existingViewHolder.name.setText(pill.getName());
                    if (pill.getSize() <= 0) {
                        existingViewHolder.size.setVisibility(View.INVISIBLE);
                        existingViewHolder.units.setVisibility(View.INVISIBLE);
                    } else {
                        existingViewHolder.size.setText(NumberHelper.getNiceFloatString(pill.getSize()));
                        existingViewHolder.units.setText(pill.getUnits());
                        existingViewHolder.size.setVisibility(View.VISIBLE);
                        existingViewHolder.units.setVisibility(View.VISIBLE);
                    }
                    existingViewHolder.color.setColour(pill.getColour());

                    Consumption latest = pill.getLatestConsumption(_consumptionRepository);
                    if (latest != null) {
                        String prefix = _context.getString(R.string.last_taken_message_prefix);
                        String lastTaken = DateHelper.getRelativeDateTime(_context, latest.getDate());
                        existingViewHolder.lastTaken.setText(prefix + " " + lastTaken);
                    } else {
                        existingViewHolder.lastTaken.setText(_context.getString(R.string.no_consumptions_message));
                    }
                }
                View add = v.findViewById(R.id.add_consumption_add);
                add.setOnClickListener(new buttonClick(true, existingViewHolder.amount, holder.container, position, this));
                View minus = v.findViewById(R.id.add_consumption_minus);
                minus.setOnClickListener(new buttonClick(false, existingViewHolder.amount, holder.container, position, this));

        }
        return v;
    }

    @Override
    public int getCount() {
        int count = 0;

        if (_pills != null) {
            count = _pills.size();
        }

        return _showNewPill ? count + 1 : count;
    }

    @Override
    public int getItemViewType(int position) {
        return position == _pills.size() ? NEW : EXISTING;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void updateAdapter(List<Pill> pills) {
        _pills = pills;
        this.notifyDataSetChanged();
    }

    public void clearConsumedPills() {
        State.getSingleton().clearConsumpedPills();
    }
    
    public void addConsumedPill(Pill pill) {
        State.getSingleton().addConsumedPill(pill);

        _consumptionSelectedListener.setDoneEnabled(State.getSingleton().getConsumptionPills().size() > 0);
    }

    public void removeConsumedPill(Pill pill) {
        State.getSingleton().removeConsumedPill(pill);

        _consumptionSelectedListener.setDoneEnabled(State.getSingleton().getConsumptionPills().size() > 0);
    }

    /*
    This is only used when pill selected first time and the pill is auto
    added to the conumption
     */
    public void addConsumedPillAtStart(int position) {
        Pill pill = _pills.get(position);
        State.getSingleton().addConsumedPillAtStart(pill);
    }

    /*
    This is only used when the pill is deselected to remove all instances
     */
    public void removeAllInstancesOfPill(int position) {
        Pill removePill = _pills.get(position);
        State.getSingleton().removeAllInstancesOfPill(removePill);
    }

    public List<Pill> getPillsConsumed() {
        return State.getSingleton().getConsumptionPills();
    }

    private class buttonClick implements View.OnClickListener {

        private boolean _add;
        private TextView _amount;
        private final View _row;
        private int _position;
        private AddConsumptionPillListAdapter _adapter;

        public buttonClick(boolean add, TextView amount, View row, int position, AddConsumptionPillListAdapter adapter) {
            _add = add;
            _amount = amount;
            _row = row;
            _position = position;
            _adapter = adapter;
        }

        @Override
        public void onClick(View view) {
            Pill pill = _pills.get(_position);
            Integer value = State.getSingleton().getOpenPills().get(pill);
            if(value == null) value = 0;

            int amount;
            if (_add) {
                amount = Integer.parseInt(_amount.getText().toString()) + 1;
                State.getSingleton().getOpenPills().put(pill, value + 1);
                _amount.setText(String.valueOf(amount));
                _adapter.addConsumedPill(pill);
            }
            else {
                amount = Integer.parseInt(_amount.getText().toString()) - 1;
                if (amount >= 0 && value > 0) {
                    State.getSingleton().getOpenPills().put(pill, value - 1);
                    _amount.setText(String.valueOf(amount));
                    _adapter.removeConsumedPill(pill);
                }
            }

            if(amount > 0){
                _row.setBackgroundColor(_context.getResources().getColor(R.color.highlight_blue));
            }
            else{
                _row.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    public interface IConsumptionSelected{
        void setDoneEnabled(boolean enabled);
    }
}

