package uk.co.cntwo.pilllogger.activities;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.R.layout;
import uk.co.cntwo.pilllogger.R.menu;
import uk.co.cntwo.pilllogger.helpers.PillHelper;
import uk.co.cntwo.pilllogger.models.Pill;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewPillActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpill);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_newpill, menu);
        return true;
    }
    
    public void addPill(View view){
    	Pill pill = new Pill();
    	
    	String name = ((EditText)findViewById(R.id.name)).getText().toString();
    	EditText sizeText = (EditText)findViewById(R.id.size);
    	
    	if(sizeText != null){
	    	int size = Integer.parseInt(sizeText.getText().toString());
	    	pill.setName(name);
	    	pill.setSize(size);
	    	
	    	PillHelper.addPill(getBaseContext(), pill);    
    	}
    }
    
}
