package uk.co.pilllogger.fragments;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import uk.co.pilllogger.R;

/**
 * Created by Alex on 01/06/2014
 * in uk.co.pilllogger.fragments.
 */
public class ExportMainFragment extends PillLoggerFragmentBase {
    private Button _pillSelector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export_main, container, false);

        if(view != null){
            _pillSelector = (Button) view.findViewById(R.id.export_select_pills);
            _pillSelector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExportSelectPillsFragment selectPillsFragment = new ExportSelectPillsFragment();
                    FragmentManager fm = ExportMainFragment.this.getActivity().getFragmentManager();
                    fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                    .replace(R.id.export_container, selectPillsFragment)
                    .addToBackStack(null)
                    .commit();
                }
            });

        }

        return view;
    }
}
