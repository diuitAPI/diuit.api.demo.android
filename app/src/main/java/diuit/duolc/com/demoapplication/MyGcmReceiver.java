package diuit.duolc.com.demoapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class MyGcmReceiver extends WakefulBroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            // TODO: 8/18/16  
        }
        else {
            // TODO: 8/18/16  
        }
    }
}
