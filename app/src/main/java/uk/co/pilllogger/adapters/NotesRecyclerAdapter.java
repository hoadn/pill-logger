package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hugo.weaving.DebugLog;
import timber.log.Timber;
import uk.co.pilllogger.R;
import uk.co.pilllogger.events.CreatedNoteEvent;
import uk.co.pilllogger.events.DeleteNoteEvent;
import uk.co.pilllogger.events.UpdatedNoteEvent;
import uk.co.pilllogger.fragments.NoteFragment;
import uk.co.pilllogger.jobs.DeleteNoteJob;
import uk.co.pilllogger.models.Note;
import uk.co.pilllogger.state.State;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder> {

    private final List<Note> _notes;

    Activity _activity;


    JobManager _jobManager;
    private RecyclerView _listView;

    public NotesRecyclerAdapter(List<Note> notes, Activity activity, JobManager jobManager, RecyclerView listView){
        _notes = new ArrayList(notes);
        _activity = activity;
        _jobManager = jobManager;
        _listView = listView;
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

        holder.setDeleteClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteNote(note);
            }
        });

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNoteEditFragment(note);
            }
        });
    }

    public List<Note> getNotes() {
        return _notes;
    }

    private void deleteNote(Note note) {
        _jobManager.addJob(new DeleteNoteJob(note));
    }

    private void startNoteEditFragment(Note note) {
        NoteFragment fragment = new NoteFragment(note);
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

    @Subscribe @DebugLog
    public void noteDeleted(DeleteNoteEvent event) {
        Note note = event.getNote();
        if (note == null) {
            return;
        }

        int i = 0;
        int toRemove = -1;
        for (Note n : _notes) {
            if (note.getId() == n.getId()) {
                toRemove = i;
            }
            i++;
        }

        if (toRemove != -1) {
            _notes.remove(note);
            notifyItemRemoved(toRemove);
        }
    }

    @Subscribe
    @DebugLog
    public void noteAdded(CreatedNoteEvent event){
        Note note = event.getNote();

        if(note == null){
            return;
        }

        DateTime whenAdded = new DateTime(note.getDate());

        int i = 0;
        int indexOf = 0;
        for(Note n : _notes) {
            DateTime noteDate = new DateTime(n.getDate());

            if (noteDate.isBefore(whenAdded)) {
                indexOf = i;
                break;
            }

            ++i;
        }

        Timber.d("Note was added. Adding to list");
        _listView.scrollToPosition(0);
        if (!_notes.contains(note)) {
            _notes.add(indexOf, note);
            notifyItemInserted(indexOf);
        }

    }

    @Subscribe
    @DebugLog
    public void noteUpdated(UpdatedNoteEvent event) {
        Note note = event.getNote();
        int indexChanged = -1;
        int i = 0;
        for (Note n : _notes) {
            if (n.getId() == note.getId()) {
                n.updateFromNote(note);
                indexChanged = i;
            }
            i++;
        }
        if (indexChanged != -1) {
            notifyItemChanged(indexChanged);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        @InjectView(R.id.notes_list_title) public TextView title;
        @InjectView(R.id.notes_list_text) public TextView text;
        @InjectView(R.id.notes_list_delete) public View delete;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        public void setOnClickListener(View.OnClickListener clickListener){
            itemView.setOnClickListener(clickListener);
        }

        public void setDeleteClickListener(View.OnClickListener clickListener){
            delete.setOnClickListener(clickListener);
        }
    }



}
