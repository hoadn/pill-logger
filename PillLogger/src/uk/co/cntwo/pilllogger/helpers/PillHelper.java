package uk.co.cntwo.pilllogger.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
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
        DatabaseHelper dbh = new DatabaseHelper(context);
        return dbh.getAllPills();
	}
	
	public static void addPill(Context context, Pill pill){
		DatabaseHelper dbh = new DatabaseHelper(context);
        dbh.insertPill(pill);
	}

}
