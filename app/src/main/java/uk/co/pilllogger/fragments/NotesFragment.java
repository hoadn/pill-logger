package uk.co.pilllogger.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.DialogActivity;
import uk.co.pilllogger.adapters.NotesRecyclerAdapter;
import uk.co.pilllogger.decorators.DividerItemDecoration;
import uk.co.pilllogger.models.Note;
import uk.co.pilllogger.models.Pill;

/**
 * Created by Nick on 08/09/2014.
 */
public class NotesFragment extends PillLoggerFragmentBase {

    @Inject
    Context _context;

    @InjectView(R.id.notes_title)
    public TextView _notesTitle;

    @InjectView(R.id.add_note)
    public ImageView _addNote;

    private RecyclerView _listView;
    private Pill _pill;
    private NotesRecyclerAdapter _adapter;
    private List<Note> _notes;

    @SuppressLint("ValidFragment")
    public NotesFragment(Pill pill) {
        _pill = pill;
        _notes = new ArrayList<Note>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, null, false);

        ButterKnife.inject(this, view);

        _listView = (RecyclerView) view.findViewById(R.id.notes_list);
        _listView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(_context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        _listView.setLayoutManager(layoutManager);

        for (int i = 0; i < 10; i++) {
            Note note = new Note(_pill);
            note.setTitle(String.valueOf(i));
            note.setText("This is the first note about this pill");
            _notes.add(note);
        }


        _adapter = new NotesRecyclerAdapter(_notes, _context);
        _listView.setAdapter(_adapter);

        _addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(_context, DialogActivity.class);
                intent.putExtra("DialogType", DialogActivity.DialogType.Note.ordinal());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _context.startActivity(intent);
            }
        });
        return view;
    }
}
