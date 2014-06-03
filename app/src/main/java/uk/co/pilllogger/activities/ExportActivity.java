package uk.co.pilllogger.activities;

import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.fragments.ExportMainFragment;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.tasks.GetPillsTask;

/**
 * Created by Alex on 22/05/2014
 * in uk.co.pilllogger.activities.
 */
public class ExportActivity extends PillLoggerActivityBase implements GetPillsTask.ITaskComplete {

    private List<Pill> _pillsList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_export);

        new GetPillsTask(this, this).execute();
        Display display = getWindowManager().getDefaultDisplay();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        int width = size.x;

        Typeface roboto = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Light.ttf");
        TextView exportTitle = (TextView)findViewById(R.id.export_title);
        TextView exportSubtitle = (TextView) findViewById(R.id.export_sub_title);
        exportTitle.setTypeface(roboto);
        exportSubtitle.setTypeface(roboto);

        View container = findViewById(R.id.export_container);

        if(container != null) {
            ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
            layoutParams.width = (int) (width * 0.9);
        }

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.export_container, new ExportMainFragment())
                    .commit();
        }

    }

    @Override
    public void pillsReceived(List<Pill> pills) {
        if (pills != null)
            _pillsList = pills;
    }

    public List<Pill> getPillsList() {
        return _pillsList;
    }
}
