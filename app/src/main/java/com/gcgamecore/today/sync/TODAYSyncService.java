package com.gcgamecore.today.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TODAYSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static TODAYSyncAdapter sEMonitorSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("EMonitorSyncService", "onCreate - EMonitorSyncService");
        synchronized (sSyncAdapterLock) {
            if (sEMonitorSyncAdapter == null) {
                sEMonitorSyncAdapter = new TODAYSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sEMonitorSyncAdapter.getSyncAdapterBinder();
    }
}