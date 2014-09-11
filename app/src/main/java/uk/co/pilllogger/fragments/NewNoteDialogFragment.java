package uk.co.pilllogger.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.path.android.jobqueue.JobManager;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.UnitAdapter;
import uk.co.pilllogger.jobs.InsertPillJob;
import uk.co.pilllogger.jobs.UpdatePillJob;
import uk.co.pilllogger.models.Note;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.views.ColourIndicator;

public class NewNoteDialogFragment extends InfoDialogFragment {

    @InjectView(R.id.new_note_text)
    EditText _newNoteName;

    @InjectView(R.id.new_note_title)
    TextView _newNoteTitle;

    @InjectView(R.id.new_note_title_text)
    TextView _newNoteTitleText;

    @Inject
    Provider<Pill> _pillProvider;

    Pill _newPill;
    @Inject JobManager _jobManager;

    Note _note;

    public NewNoteDialogFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public NewNoteDialogFragment(Pill pill){
        super(pill);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_new_note;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Activity activity = getActivity();

        if(activity == null){
            return null;
        }

        _newPill = _pillProvider.get();

        View view = inflater.inflate(R.layout.fragment_new_note, container, false);

        ButterKnife.inject(this, view);

        setTypeface();

        View doneLayout = view.findViewById(R.id.new_note_done_layout);
        doneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                _pill.setName(editPillName.getText().toString());
//
//                String pillSize = editPillSize.getText().toString();
//                float size = pillSize.trim().length() > 0 ? Float.valueOf(pillSize) : 0;
//                _pill.setSize(size);
//                _pill.setUnits(spinner.getSelectedItem().toString());
//                InputMethodManager im = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//                im.hideSoftInputFromWindow(editPillName.getWindowToken(), 0);

//                int delayMillis = getResources().getInteger(R.integer.slide_duration);
//                view.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        _jobManager.addJobInBackground(new UpdatePillJob(_pill));
//                    }
//                }, delayMillis);

                getActivity().getFragmentManager().popBackStack();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.reset(this);
    }



    private void setTypeface() {
        Typeface tf = State.getSingleton().getRobotoTypeface();

        _newNoteName.setTypeface(tf);
        _newNoteTitle.setTypeface(tf);
        _newNoteTitleText.setTypeface(tf);
    }


//    @OnClick(R.id.new_pill_done_container)
//    public void submit(){
//        _newPill.setName(String.valueOf(_newPillName.getText()));
//        String pillSize = _newPillSize.getText().toString();
//        float size = pillSize.trim().length() > 0 ? Float.valueOf(pillSize) : 0;
//        _newPill.setSize(size);
//        _newPill.setUnits(_unitsSpinner.getSelectedItem().toString());
//        Activity activity = getActivity();
//        InputMethodManager im = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//        im.hideSoftInputFromWindow(_newPillName.getWindowToken(), 0);
//
//        if (_newPill.getName().equals("") == false) {
//            _jobManager.addJobInBackground(new InsertPillJob(_newPill));
//        }
//
//        activity.finish();
//    }
}
