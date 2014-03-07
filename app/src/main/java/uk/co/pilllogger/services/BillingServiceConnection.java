package uk.co.pilllogger.services;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.android.vending.billing.IInAppBillingService;

/**
 * Created by Alex on 07/03/14.
 */
public class BillingServiceConnection implements ServiceConnection {

    private IInAppBillingService _billingService;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        _billingService = IInAppBillingService.Stub.asInterface(service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        _billingService = null;
    }

    public IInAppBillingService getBillingService() {
        return _billingService;
    }

    public void setBillingService(IInAppBillingService billingService) {
        _billingService = billingService;
    }
}
