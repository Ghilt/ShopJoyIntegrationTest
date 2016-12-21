package beacons.shouse.se.beacontest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import se.injou.shopjoy.sdk.HistoryEntry;
import se.injou.shopjoy.sdk.ShopJoySDK;

public class MainActivity extends AppCompatActivity {

    private static String API_KEY = "ENTER API KEY HERE";

    private static final long SCAN_PERIOD = 10000;
    private ShopJoySDK beaconManager;
    private BluetoothAdapter mBluetoothAdapter;

    private Handler mHandler;
    private TextView scanView;
    private TextView shopJoyView;
    private boolean permissionsGranted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanView = (TextView) findViewById(R.id.textViewScan);
        shopJoyView = (TextView) findViewById(R.id.textViewBottom);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int permitted = PackageManager.PERMISSION_GRANTED;
        boolean needPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            needPermission = (checkSelfPermission(Manifest.permission.INTERNET) != permitted);
            needPermission = needPermission || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != permitted;
            needPermission = needPermission || checkSelfPermission(Manifest.permission.BLUETOOTH) != permitted;
            needPermission = needPermission || checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != permitted;
        } else {
            needPermission = false;
            permissionsGranted = true;
        }

        if (needPermission) {
            Log.d("spx", "Need to accept permissions");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, 1010);
        } else {
            permissionsGranted = true;
            setupBLE();
        }

    }

    private void setupBLE() {
        Log.d("spx", "Setting up BLE");
        initLeScanning();
        initShopJoy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        permissionsGranted = true;
        for (int result : grantResults) {
            if (result != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                permissionsGranted = false;
                break;
            }
        }

        if (permissionsGranted) {
            setupBLE();
        } else {
            shopJoyView.setText("Permissions not granted\nrestart app");
        }

    }

    private void initShopJoy() {
        beaconManager = new ShopJoySDK();
        beaconManager.setShopJoySettings(this, API_KEY, true, "", "");
        beaconManager.setCallback(new ShopJoySDK.TriggeredCampaignCallback() {
            @Override
            public void triggeredCampaign(HistoryEntry h, int triggerRange) {
                Log.d("spx", "TriggeredCampaign from active app: " + h.campaignTitle);
                shopJoyView.setText("Campaign title: " + h.campaignTitle);
            }
        });
        Log.i("spx", "register shopjoy.");
        ShopJoySDK.register(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.scanShopjoyButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMonitoringShopjoy();
                Snackbar.make(view, "Start Monitoring Shopjoy", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

        startMonitoringShopjoy();
    }

    private void startMonitoringShopjoy() {
        Log.d("spx", "Start monitoring shopjoy");
        beaconManager.startMonitoring();
    }

    @Override
    protected void onPause() {
        if (permissionsGranted) {
            Log.i("spx", "Unregister shopjoy.");
            ShopJoySDK.unregister(this);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (permissionsGranted) {
            Log.i("spx", "register shopjoy.");
            ShopJoySDK.register(this);
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        ;

        if (id == R.id.action_start_ble) {
            scanLeDevice(true);
            return true;
        } else if (id == R.id.action_stop_ble) {
            scanLeDevice(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scanView.setText(device.getName() + "  \t  " + device.getAddress());
                            TextView beacons = (TextView) findViewById(R.id.textViewBeacon);
                            beacons.setText((device.getName() == null ? "Unnamed" : device.getName()) + "  \t  " + device.getAddress() + "\n" + beacons.getText());
                        }
                    });
                }
            };

    private void initLeScanning() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanLeDevice(true);
                Snackbar.make(view, "Started scanning", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 10);
        }

        mHandler = new Handler();
    }

    private void scanLeDevice(final boolean enable) {

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    scanView.setText("Stopped");
                }
            }, SCAN_PERIOD);

            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            scanView.setText("Stopped");
        }
    }

}
