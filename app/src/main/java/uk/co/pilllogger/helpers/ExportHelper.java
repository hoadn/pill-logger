package uk.co.pilllogger.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.pilllogger.models.Consumption;

/**
 * Created by nick on 22/05/14.
 */
public class ExportHelper {
    private static final String TAG = "ExportHelper";
    private static final String FILE_NAME = "Pill_Logger_Export.csv";
    private Context _context;
    private static ExportHelper _instance;

    private ExportHelper(Context context) {
        _context = context;
    }

    public static ExportHelper getSingleton(Context context) {
        if (_instance == null)
            return new ExportHelper(context);
        return _instance;
    }

    public void exportToCsv(List<Consumption> consumptions, Date startDate, Date endDate) {
        exportToCsv(filterConsumptions(startDate, endDate, consumptions));
    }

    public void exportToCsv(List<Consumption> consumptions) {
        File folder = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS);



        if (!folder.exists())
            folder.mkdir();

        final String filename = folder.toString() + "/" + FILE_NAME;

        try {
            FileWriter fileWriter = new FileWriter(filename);

            fileWriter.append("Medication, ");
            fileWriter.append("Dosage, ");
            fileWriter.append("Total Dosage, ");
            fileWriter.append("Units, ");
            fileWriter.append("Quantity, ");
            fileWriter.append("Date, ");
            fileWriter.append('\n');

            for (Consumption consumption : consumptions) {
                if (consumption.getPill() != null) {
                    fileWriter.append(consumption.getPill().getName() + ", ");
                    fileWriter.append(String.valueOf(consumption.getPill().getSize()) + ", ");
                    fileWriter.append(String.valueOf(consumption.getQuantity() * consumption.getPill().getSize()) + ", ");
                    fileWriter.append(consumption.getPill().getUnits() + ", ");
                    fileWriter.append(String.valueOf(consumption.getQuantity()) + ", ");
                    fileWriter.append(DateHelper.formatDateAndTime(_context, consumption.getDate()).toString() + ", ");
                    fileWriter.append('\n');
                }
            }
            fileWriter.flush();
            fileWriter.close();
            Toast.makeText(_context, FILE_NAME + " has been sucessfully created on your SD card", Toast.LENGTH_LONG).show();

            Intent emailIntent = new Intent(
                    android.content.Intent.ACTION_SEND);

            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                    new String[] { "nicholas.allen88@gmail.com" });
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                    "Pill Logger Export");
            Uri uri = Uri.parse(filename);
            if (uri != null)
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            emailIntent
                    .putExtra(android.content.Intent.EXTRA_TEXT, "Here's your pill logger export");
            _context.startActivity(Intent.createChooser(emailIntent,
                    "Sending email..."));
        }
        catch (IOException e) {
            Logger.e(TAG, "IO Execption with FileWriter " + e.getMessage());
        }
    }

    private List<Consumption> filterConsumptions(Date startDate, Date endDate, List<Consumption> consumptions) {
        List<Consumption> filteredConsumptions = new ArrayList<Consumption>();
        for (Consumption consumption : consumptions) {
            if (consumption.getDate().compareTo(startDate) > 0 && consumption.getDate().compareTo(endDate) < 0)
                filteredConsumptions.add(consumption);
        }
        return filteredConsumptions;
    }
}
