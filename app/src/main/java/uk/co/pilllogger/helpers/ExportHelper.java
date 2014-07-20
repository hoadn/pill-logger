package uk.co.pilllogger.helpers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;
import uk.co.pilllogger.R;
import uk.co.pilllogger.models.Consumption;

/**
 * Created by nick on 22/05/14.
 */
public class ExportHelper {
    private static final String TAG = "ExportHelper";
    private static final String FILE_NAME = "Pill_Logger_Export.csv";
    char NEW_LINE = '\n';
    private Activity _context;
    private static ExportHelper _instance;

    private ExportHelper(Activity context) {
        _context = context;
    }

    public static ExportHelper getSingleton(Activity activity) {
        if (_instance == null)
            return new ExportHelper(activity);
        return _instance;
    }

    public void exportToCsv(List<Consumption> consumptions, Date startDate, Date endDate) {
        exportToCsv(filterConsumptions(startDate, endDate, consumptions));
    }

    public void exportToCsv(List<Consumption> consumptions) {

        String folderName = "export/";
        File folder = new File(_context.getCacheDir(), folderName);

        if(!folder.exists())
            folder.mkdir();

        File file = new File(_context.getCacheDir(), folderName + FILE_NAME);

        try {
            FileWriter fileWriter = new FileWriter(file);

            fileWriter.append("Medication, ");
            fileWriter.append("Dosage, ");
            fileWriter.append("Total Dosage, ");
            fileWriter.append("Units, ");
            fileWriter.append("Quantity, ");
            fileWriter.append("Date, ");
            fileWriter.append(NEW_LINE);

            for (Consumption consumption : consumptions) {
                if (consumption.getPill() != null) {
                    fileWriter.append(consumption.getPill().getName()).append(", ");
                    fileWriter.append(String.valueOf(consumption.getPill().getSize())).append(", ");
                    fileWriter.append(String.valueOf(consumption.getQuantity() * consumption.getPill().getSize())).append(", ");
                    fileWriter.append(consumption.getPill().getUnits()).append(", ");
                    fileWriter.append(String.valueOf(consumption.getQuantity())).append(", ");
                    fileWriter.append(DateHelper.formatDateAndTime(_context, consumption.getDate())).append(", ");
                    fileWriter.append(NEW_LINE);
                }
            }
            fileWriter.flush();
            fileWriter.close();

            final Uri uri = FileProvider.getUriForFile(_context, "uk.co.pilllogger.fileprovider", file);

            final Intent intent = ShareCompat.IntentBuilder.from(_context)
                    .setType("text/csv")
                    .setSubject(_context.getString(R.string.share_export_subject))
                    .setStream(uri)
                    .setChooserTitle(_context.getString(R.string.share_export_chooser_title))
                    .createChooserIntent()
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            _context.startActivity(intent);
        }
        catch (IOException e) {
            Timber.e("IO Execption with FileWriter " + e.getMessage());
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
