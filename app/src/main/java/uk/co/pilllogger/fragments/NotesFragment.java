package uk.co.pilllogger.fragments;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.NotesRecyclerAdapter;
import uk.co.pilllogger.models.Note;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;

/**
 * Created by Nick on 08/09/2014.
 */
public class NotesFragment extends PillLoggerFragmentBase {

    @Inject
    Context _context;

    @InjectView(R.id.notes_title)
    public TextView _notesTitle;

    @InjectView(R.id.notes_add_layout)
    public View _addNote;

    @InjectView(R.id.notes_done_layout)
    public View _notesDone;

    private RecyclerView _listView;
    private Pill _pill;
    private NotesRecyclerAdapter _adapter;
    private List<Note> _notes;

    @SuppressLint("ValidFragment")
    public NotesFragment(Pill pill) {
        _pill = pill;
        _notes = new ArrayList<Note>();
    }

    public NotesFragment () {

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
                NewNoteDialogFragment fragment = new NewNoteDialogFragment(_pill);
                FragmentManager fm = NotesFragment.this.getActivity().getFragmentManager();
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                        .replace(R.id.export_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        _notesDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });

        setTypeface();
        return view;
    }

    public void setTypeface() {
        Typeface typeface = State.getSingleton().getRobotoTypeface();
        _notesTitle.setTypeface(typeface);

    }
}
