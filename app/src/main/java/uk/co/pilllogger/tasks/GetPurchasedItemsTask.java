package uk.co.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;

import com.android.vending.billing.IInAppBillingService;

import java.util.ArrayList;
import java.util.List;

import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.PillRepository;
import uk.co.pilllogger.services.BillingServiceConnection;
import uk.co.pilllogger.state.Feature;
import uk.co.pilllogger.state.State;

/**
 * Created by Alex on 07/03/14.
 */
public class GetPurchasedItemsTask extends AsyncTask<Void, Void, List<Feature.InAppId>>{

    private static final String TAG = "GetPurchasedItemsTask";
    Context _context;
    private final BillingServiceConnection _billingServiceConnection;
    ITaskComplete _listener;

    public GetPurchasedItemsTask(Context context, BillingServiceConnection billingServiceConnection, ITaskComplete listener) {
        _context = context;
        _billingServiceConnection = billingServiceConnection;
        _listener = listener;
    }
    @Override
    protected List<Feature.InAppId> doInBackground(Void... voids) {
        List<Feature.InAppId> features = new ArrayList<Feature.InAppId>();

        IInAppBillingService billingService = _billingServiceConnection.getBillingService();

        if (billingService == null) {
            return features;
        }

        try {
            Bundle ownedItems = billingService.getPurchases(3, _context.getPackageName(), "inapp", null);

            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String> signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE");
                String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");

                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String purchaseData = purchaseDataList.get(i);
                    String signature = signatureList.get(i);
                    String sku = ownedSkus.get(i);

                    features.add(Enum.valueOf(Feature.InAppId.class, sku));
                }
            }

        } catch (RemoteException e) {
            e.printStackTrace();
            Logger.e(TAG, "Exception when trying to get purchased messages");
        }

        return features;
    }

    @Override
    protected void onPostExecute(List<Feature.InAppId> features) {
        _listener.purchasedItemsReceived(features);
        List<Feature.InAppId> enabledFeatures = State.getSingleton().getEnabledFeatures();
        for (Feature.InAppId feature : features) {
            enabledFeatures.add(feature);
        }

    }

    public interface ITaskComplete{
        public void purchasedItemsReceived(List<Feature.InAppId> features);
    }
}
