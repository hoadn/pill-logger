package uk.co.pilllogger.services;

import java.util.List;
import java.util.Map;

import uk.co.pilllogger.models.ExportSettings;
import uk.co.pilllogger.models.Pill;

/**
 * Created by Alex on 05/06/2014
 * in uk.co.pilllogger.services.
 */
public interface IExportService {
    ExportSettings getExportSettings();

    List<Pill> getAllPills();

    Map<Integer, Integer> getMaxDosages();
}
