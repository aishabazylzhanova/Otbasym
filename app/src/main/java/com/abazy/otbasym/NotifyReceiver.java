/*  This BroadcastReceiver has a double function:
	- Receive intent from Notifier to create notifications
	- Receive ACTION_BOOT_COMPLETED after reboot to restore notifications saved in settings.json
*/

package com.abazy.otbasym;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotifyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// Set again alarms after reboot
		if( Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ) {

			new Notifier(context, null, 0, Notifier.What.REBOOT);

		} else { // Create notification

			Intent notifyIntent = new Intent(context, TreesActivity.class)
					.putExtra(Notifier.TREE_ID_KEY, intent.getIntExtra("treeId", 0))
					.putExtra(Notifier.INDI_ID_KEY, intent.getStringExtra("indiId"))
					.putExtra(Notifier.NOTIFY_ID_KEY, intent.getIntExtra("id", 1));
			PendingIntent pendingIntent = PendingIntent.getActivity(context, intent.getIntExtra("id", 1),
					notifyIntent, PendingIntent.FLAG_IMMUTABLE);


		}
	}
}
