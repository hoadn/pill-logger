package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.pilllogger.services.IExportService;

/**
 * Created by Alex on 05/06/2014
 * in uk.co.pilllogger.fragments.
 */
public class ExportFragmentBase extends PillLoggerFragmentBase {

    protected IExportService _exportService;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initService();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initService(){
        Activity activity = getActivity();

        if (activity == null) {
            return;
        }

        if(!(activity instanceof IExportService)){
            throw new IllegalArgumentException("Activity must implement IExportService");
        }

        _exportService = (IExportService) activity;
    }
}
