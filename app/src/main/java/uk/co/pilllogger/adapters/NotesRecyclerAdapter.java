package uk.co.pilllogger.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;
import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.DialogActivity;
import uk.co.pilllogger.events.DecreaseConsumptionEvent;
import uk.co.pilllogger.events.DeleteConsumptionEvent;
import uk.co.pilllogger.events.IncreaseConsumptionEvent;
import uk.co.pilllogger.events.TakeConsumptionAgainEvent;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Note;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.views.ColourIndicator;

/**
 * Created by Alex on 25/08/2014
 * in uk.co.pilllogger.adapters.
 */
public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder> {

    private final List<Note> _notes;

    Context _context;

    public NotesRecyclerAdapter(List<Note> notes, Context context){
        _notes = notes;
        _context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notes_list_item, null);

        // create ViewHolder

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Note note = _notes.get(position);

        if (note == null) {
            return;
        }

        holder.title.setText(note.getTitle());
        holder.text.setText(note.getText());

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startDialog(note);
            }
        });
    }

    private void startDialog(Consumption consumption) {
        Intent intent = new Intent(_context, DialogActivity.class);
        intent.putExtra("DialogType", DialogActivity.DialogType.Consumption.ordinal());
        intent.putExtra("ConsumptionGroup", consumption.getGroup());
        intent.putExtra("PillId", consumption.getPillId());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return _notes.size();
    }

    public List<Note> getConsumptions() {
        return _notes;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.notes_list_title) public TextView title;
        @InjectView(R.id.notes_list_text) public TextView text;
        @InjectView(R.id.consumption_list_size) public TextView size;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        public void setOnClickListener(View.OnClickListener clickListener){
            itemView.setOnClickListener(clickListener);
        }
    }

}
