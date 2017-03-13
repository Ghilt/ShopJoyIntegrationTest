package beacons.shouse.se.databinding;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.design.widget.Snackbar;
import android.view.View;

import beacons.shouse.se.beacontest.BR;
import beacons.shouse.se.beacontest.ScanUIListener;

/**
 * Created by ani on 2017-03-13.
 */

public class ScanUI extends BaseObservable {

    private ScanUIListener listener;
    private String scanText;
    private String beaconBody;
    //    public final ObservableField<String> testObservableField = new ObservableField<>();
    private String shopJoyStatus;

    public ScanUI(ScanUIListener listener, String scanText, String beaconBody, String shopJoyStatus) {
        this.listener = listener;
        this.scanText = scanText;
        this.beaconBody = beaconBody;
        this.shopJoyStatus = shopJoyStatus;
//        this.shopJoyStatus.set(shopJoyStatus);
    }

    @Bindable
    public String getScanText() {
        return scanText;
    }

    public void setScanText(String scanText) {
        this.scanText = scanText;
        notifyPropertyChanged(BR.scanText);

    }

//    public String getShopJoyStatus() {
//        return testObservableField.get();
//    }
//
//    public void setShopJoyStatus(String shopJoyStatus) {
//        this.testObservableField.set(shopJoyStatus);
//    }


    @Bindable
    public String getShopJoyStatus() {
        return shopJoyStatus;
    }

    public void setShopJoyStatus(String shopJoyStatus) {
        this.shopJoyStatus = shopJoyStatus;
        notifyPropertyChanged(BR.shopJoyStatus);

    }

    public void onScanLeDeviceClicked(View view){
        listener.scanLeDevice(true);
        Snackbar.make(view, "Started scanning", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void onScanLeShopJoyClicked(View view){
        listener.startMonitoringShopjoy();
        Snackbar.make(view, "Start Monitoring Shopjoy", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @Bindable
    public String getBeaconBody() {
        return beaconBody;
    }

    public void setBeaconBody(String beaconBody) {
        this.beaconBody = beaconBody;
        notifyPropertyChanged(BR.beaconBody);

    }
}
