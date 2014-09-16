package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.DialogActivity;
import uk.co.pilllogger.fragments.NewNoteFragment;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Note;
import uk.co.pilllogger.state.State;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder> {

    private final List<Note> _notes;

    Activity _activity;

    public NotesRecyclerAdapter(List<Note> notes, Activity activity){
        _notes = notes;
        _activity = activity;
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
        holder.text.setTypeface(State.getSingleton().getRobotoTypeface());
        holder.title.setTypeface(State.getSingleton().getRobotoTypeface());

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNoteEditFragment(note);
            }
        });
    }

    private void startNoteEditFragment(Note note) {
        NewNoteFragment fragment = new NewNoteFragment(note);
        FragmentManager fm = _activity.getFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                .replace(R.id.export_container, fragment)
                .addToBackStack(null)
                .commit();
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

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        public void setOnClickListener(View.OnClickListener clickListener){
            itemView.setOnClickListener(clickListener);
        }
    }

}
