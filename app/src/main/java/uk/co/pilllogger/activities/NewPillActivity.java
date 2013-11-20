package uk.co.pilllogger.activities;

import uk.co.pilllogger.R;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.tasks.InsertPillTask;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

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

            new InsertPillTask(this, pill).execute();
    	}
    }
    
}
