package uk.co.cntwo.pilllogger.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.os.Environment;

import uk.co.cntwo.pilllogger.models.*;

public class PillHelper {
	private static List<Pill> _pills;
	private static Map<UUID, Pill> _pillMap;
	
	@SuppressWarnings("unchecked")
	public static List<Pill> getPills(Context context){
		if(_pills != null)
			return _pills;
		
		File file = getPillsFile();
		try{
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream is = new ObjectInputStream(fis);
			List<Pill> pills = (List<Pill>)is.readObject();
			is.close();
			
			_pills = pills;
			
			_pillMap = new HashMap<UUID, Pill>();
			
			for(Pill pill : pills){
				_pillMap.put(pill.getId(), pill);
			}
			
			return pills;
		}
		catch(IOException e){
			ErrorHelper.logError(context, e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			ErrorHelper.logError(context, e.getMessage(), e);
		}
		return null;
	}
	
	public static Map<UUID, Pill> getPillMap(Context context){
		if(_pillMap != null)
			return _pillMap;
		
		getPills(context);
		
		return _pillMap;
	}
	
	public static void addPill(Context context, Pill pill){
		List<Pill> pills = getPills(context);
		if(pills != null){
			pills.add(pill);
			savePills(context, pills);
		}else{
			ErrorHelper.logError(context, "Pills collection was null");
		}
	}
	
	private static void savePills(Context context, List<Pill> pills){
		File file = getPillsFile();
		try{
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(pills);
			os.close();
		}
		catch(IOException e){
			ErrorHelper.logError(context, e.getMessage(), e);
		}
	}
	
	private static File getPillsFile(){
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		return new File(path, "pills");
	}
}
