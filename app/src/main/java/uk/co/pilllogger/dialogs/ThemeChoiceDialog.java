package uk.co.pilllogger.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.MainActivity;

/**
 * Created by Alex on 26/04/2014
 * in uk.co.pilllogger.dialogs.
 */
public class ThemeChoiceDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.dialog_change_theme_title);
        builder.setMessage(R.string.dialog_change_theme)
                .setPositiveButton(R.string.dialog_change_theme_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // use new theme
                        Context context = activity;
                        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        defaultSharedPreferences.edit().putString(context.getString(R.string.pref_key_theme_list), context.getString(R.string.professionalTheme)).commit();

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_change_theme_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // use current theme
                        Context context = activity;
                        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        defaultSharedPreferences.edit().putString(context.getString(R.string.pref_key_theme_list), context.getString(R.string.rainbowTheme)).commit();
                        dialog.dismiss();

                        activity.finish();
                        Intent intent = new Intent(activity, activity.getClass());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
