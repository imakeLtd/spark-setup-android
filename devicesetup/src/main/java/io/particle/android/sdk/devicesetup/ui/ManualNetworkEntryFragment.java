package io.particle.android.sdk.devicesetup.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.Set;

import javax.inject.Inject;

import androidx.navigation.Navigation;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import io.particle.android.sdk.devicesetup.ParticleDeviceSetupLibrary;
import io.particle.android.sdk.devicesetup.R;
import io.particle.android.sdk.devicesetup.R2;
import io.particle.android.sdk.devicesetup.commands.CommandClientFactory;
import io.particle.android.sdk.devicesetup.commands.ScanApCommand;
import io.particle.android.sdk.devicesetup.commands.data.WifiSecurity;
import io.particle.android.sdk.devicesetup.loaders.ScanApCommandLoader;
import io.particle.android.sdk.devicesetup.model.ScanAPCommandResult;
import io.particle.android.sdk.di.ApModule;
import io.particle.android.sdk.ui.BaseFragment;
import io.particle.android.sdk.utils.SEGAnalytics;
import io.particle.android.sdk.utils.SSID;
import io.particle.android.sdk.utils.WifiFacade;
import io.particle.android.sdk.utils.ui.ParticleUi;
import io.particle.android.sdk.utils.ui.Ui;

public class ManualNetworkEntryFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<Set<ScanAPCommandResult>> {

    public static final String EXTRA_SOFT_AP = "EXTRA_SOFT_AP";

    @Inject protected WifiFacade wifiFacade;
    @Inject protected CommandClientFactory commandClientFactory;
    private SSID softApSSID;
    protected Integer wifiSecurityType = WifiSecurity.WPA2_AES_PSK.asInt();

    @OnCheckedChanged(R2.id.network_requires_password)
    protected void onSecureCheckedChange(boolean isChecked) {
        if (isChecked) {
            SEGAnalytics.track("Device Setup: Selected secured network");
            wifiSecurityType = WifiSecurity.WPA2_AES_PSK.asInt();
        } else {
            SEGAnalytics.track("Device Setup: Selected open network");
            wifiSecurityType = WifiSecurity.OPEN.asInt();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ParticleDeviceSetupLibrary.getInstance().getApplicationComponent().activityComponentBuilder()
                .apModule(new ApModule()).build().inject(this);
        SEGAnalytics.screen("Device Setup: Manual network entry screen");
        softApSSID = getArguments().getParcelable(EXTRA_SOFT_AP);

        View view = inflater.inflate(R.layout.activity_manual_network_entry, container, true);
        ButterKnife.bind(this, view);
        ParticleUi.enableBrandLogoInverseVisibilityAgainstSoftKeyboard(getActivity());
        return view;
    }

    public void onConnectClicked(View view) {
        String ssid = Ui.getText(this, R.id.network_name, true);
        ScanApCommand.Scan scan = new ScanApCommand.Scan(ssid, wifiSecurityType, 0);

        CheckBox requiresPassword = Ui.findView(this, R.id.network_requires_password);
        if (requiresPassword.isChecked()) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(PasswordEntryFragment.EXTRA_SOFT_AP_SSID, softApSSID);
            bundle.putString(PasswordEntryFragment.EXTRA_NETWORK_TO_CONFIGURE, ParticleDeviceSetupLibrary.getInstance()
                    .getApplicationComponent().getGson().toJson(scan));
            Navigation.findNavController(view).navigate(R.id.action_manualNetworkEntryFragment_to_passwordEntryFragment, bundle);
        } else {
            Bundle bundle = new Bundle();
            bundle.putParcelable(ConnectingFragment.EXTRA_SOFT_AP_SSID, softApSSID);
            bundle.putString(ConnectingFragment.EXTRA_NETWORK_TO_CONFIGURE, ParticleDeviceSetupLibrary.getInstance()
                    .getApplicationComponent().getGson().toJson(scan));
            Navigation.findNavController(view).navigate(R.id.action_manualNetworkEntryFragment_to_connectingFragment, bundle);
        }
    }

    public void onCancelClicked(View view) {
        //finish();
    }

    // FIXME: loader not currently used, see note in onLoadFinished()
    @Override
    public Loader<Set<ScanAPCommandResult>> onCreateLoader(int id, Bundle args) {
        return new ScanApCommandLoader(getContext(), commandClientFactory.newClientUsingDefaultsForDevices(wifiFacade, softApSSID));
    }

    @Override
    public void onLoadFinished(Loader<Set<ScanAPCommandResult>> loader, Set<ScanAPCommandResult> data) {
        // FIXME: perform process described here?:
        // https://github.com/spark/mobile-sdk-ios/issues/56
    }

    @Override
    public void onLoaderReset(Loader<Set<ScanAPCommandResult>> loader) {
        // no-op
    }
}
