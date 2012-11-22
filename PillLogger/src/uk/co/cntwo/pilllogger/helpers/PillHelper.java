package uk.co.cntwo.pilllogger.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import uk.co.cntwo.pilllogger.models.*;

public class PillHelper {
	public static List<Pill> getPills(Context context){
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File file = new File(path, "pills");
		try{
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream is = new ObjectInputStream(fis);
			List<Pill> pills = (List<Pill>)is.readObject();
			is.close();
			
			return pills;
		}
		catch(IOException e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
			//TODO: Error handling
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void addPill(Context context, Pill pill){
		List<Pill> pills = getPills(context);
		if(pills != null){
			pills.add(pill);
			savePills(context, pills);
		}else{
			Toast.makeText(context, "Pills was null =(", Toast.LENGTH_LONG).show();
		}
	}
	
	private static void savePills(Context context, List<Pill> pills){
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File file = new File(path, "pills");
		try{
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(pills);
			os.close();
		}
		catch(IOException e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
			//TODO: Error handling
		}
	}
}
