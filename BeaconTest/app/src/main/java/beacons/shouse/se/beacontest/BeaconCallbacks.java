package beacons.shouse.se.beacontest;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import se.injou.shopjoy.sdk.HistoryEntry;
import se.injou.shopjoy.sdk.Quest;
import se.injou.shopjoy.sdk.ShopJoyCallbacks;

/**
 * Created by ani on 2016-12-15.
 */

public class BeaconCallbacks extends ShopJoyCallbacks {

    @Override
    public void onCampaignTriggered(Context context, HistoryEntry entry) {
        String text = "onCampaignTriggered: " + entry.campaignTitle;
        Log.d("spx", text);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onQuestTriggered(Context context, Quest quest) {
        String text = "onQuestTriggered";
        Log.d("spx", text);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void postServiceStartup(Context context) {
        String text = "postServiceStartup";
        Log.d("spx", text);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

    }
    @Override
    public void enteredBeaconArea(Context context, String beaconID) {
        String text = "enteredBeaconArea: " + beaconID;
        Log.d("spx", text);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

    }

}
