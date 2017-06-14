package com.dkanada.openapk.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dkanada.openapk.utils.ActionUtils;
import com.dkanada.openapk.utils.AppDbUtils;
import com.dkanada.openapk.async.ClearDataAsync;
import com.dkanada.openapk.models.AppInfo;
import com.dkanada.openapk.App;
import com.dkanada.openapk.R;
import com.dkanada.openapk.async.RemoveCacheAsync;
import com.dkanada.openapk.utils.AppPreferences;
import com.dkanada.openapk.utils.DialogUtils;
import com.dkanada.openapk.utils.InterfaceUtils;
import com.dkanada.openapk.views.ButtonIconView;
import com.dkanada.openapk.views.ButtonView;

import java.text.SimpleDateFormat;

public class AppActivity extends ThemeActivity {
    private AppPreferences appPreferences;
    private AppDbUtils appDbUtils;
    private Context context;
    private MenuItem favorite;
    private AppInfo appInfo;
    private int UNINSTALL_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appPreferences = App.getAppPreferences();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        context = this;

        appDbUtils = new AppDbUtils(context);

        getInitialConfiguration();
        setInitialConfiguration();
        setScreenElements();
    }

    private void setInitialConfiguration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(InterfaceUtils.darker(appPreferences.getPrimaryColor(), 0.8));
            toolbar.setBackgroundColor(appPreferences.getPrimaryColor());
            if (appPreferences.getNavigationColor()) {
                getWindow().setNavigationBarColor(appPreferences.getPrimaryColor());
            }
        }
    }

    private void setScreenElements() {
        TextView header = (TextView) findViewById(R.id.header);
        ImageView icon = (ImageView) findViewById(R.id.app_icon);
        TextView name = (TextView) findViewById(R.id.app_name);

        header.setBackgroundColor(appPreferences.getPrimaryColor());
        icon.setImageDrawable(appInfo.getIcon());
        name.setText(appInfo.getName());

        ImageView open = (ImageView) findViewById(R.id.open);
        ImageView extract = (ImageView) findViewById(R.id.extract);
        ImageView uninstall = (ImageView) findViewById(R.id.uninstall);
        ImageView share = (ImageView) findViewById(R.id.share);
        ImageView settings = (ImageView) findViewById(R.id.settings);

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.open(context, appInfo);
            }
        });
        extract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.extract(context, appInfo);
            }
        });
        uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appInfo.getSystem()) {
                    ActionUtils.open(context, appInfo);
                } else {

                }
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.share(context, appInfo);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.settings(context, appInfo);
            }
        });

        RelativeLayout information = (RelativeLayout) findViewById(R.id.information_layout);
        TextView apkText = (TextView) findViewById(R.id.app_apk_text);
        TextView versionText = (TextView) findViewById(R.id.app_version_text);
        TextView sizeText = (TextView) findViewById(R.id.app_size_text);
        TextView cacheSizeText = (TextView) findViewById(R.id.app_cache_size_text);
        TextView dataFolderText = (TextView) findViewById(R.id.app_data_folder_text);
        TextView installText = (TextView) findViewById(R.id.app_install_text);
        TextView updateText = (TextView) findViewById(R.id.app_update_text);

        apkText.setText(appInfo.getAPK());
        versionText.setText(appInfo.getVersion());
        sizeText.setText(R.string.development_layout);
        cacheSizeText.setText(R.string.development_layout);
        dataFolderText.setText(appInfo.getData());
        PackageManager packageManager = getPackageManager();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        try {
            installText.setText(formatter.format(packageManager.getPackageInfo(appInfo.getAPK(), 0).firstInstallTime));
            updateText.setText(formatter.format(packageManager.getPackageInfo(appInfo.getAPK(), 0).lastUpdateTime));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (appPreferences.getTheme().equals("1")) {
            for (int i = 0; i < information.getChildCount(); i += 2) {
                information.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.grey_light));
            }
        } else {
            for (int i = 0; i < information.getChildCount(); i += 2) {
                information.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.grey_dark));
            }
        }

        LinearLayout buttons = (LinearLayout) findViewById(R.id.buttons);
        ButtonView removeCache = new ButtonView(context, getString(R.string.action_remove_cache), "test", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = DialogUtils.showTitleContentWithProgress(context
                        , getResources().getString(R.string.dialog_cache_progress)
                        , getResources().getString(R.string.dialog_cache_progress_description));
                new RemoveCacheAsync(context, dialog, appInfo).execute();
            }
        });

        buttons.addView(removeCache);

        /*CardView cache = (CardView) findViewById(R.id.remove_cache);
        CardView data = (CardView) findViewById(R.id.clear_data);

        cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog dialog = DialogUtils.showTitleContentWithProgress(context
                        , getResources().getString(R.string.dialog_cache_progress)
                        , getResources().getString(R.string.dialog_cache_progress_description));
                new RemoveCacheAsync(context, dialog, appInfo).execute();
            }
        });
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog dialog = DialogUtils.showTitleContentWithProgress(context
                        , getResources().getString(R.string.dialog_clear_data_progress)
                        , getResources().getString(R.string.dialog_clear_data_progress_description));
                new ClearDataAsync(context, dialog, appInfo).execute();
            }
        });*/
    }

    protected void updateHideButton(final RelativeLayout hide) {
        InterfaceUtils.updateAppHiddenIcon(context, hide, appDbUtils.checkAppInfo(appInfo, 3));
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.hide(context, appInfo);
                InterfaceUtils.updateAppHiddenIcon(context, hide, appDbUtils.checkAppInfo(appInfo, 3));
            }
        });
    }

    protected void updateDisableButton(final RelativeLayout disable) {
        InterfaceUtils.updateAppDisabledIcon(context, disable, appDbUtils.checkAppInfo(appInfo, 4));
        disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.disable(context, appInfo);
                InterfaceUtils.updateAppDisabledIcon(context, disable, appDbUtils.checkAppInfo(appInfo, 4));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UNINSTALL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.i("App", appInfo.getAPK() + "OK");
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            } else if (resultCode == RESULT_CANCELED) {
                Log.i("App", appInfo.getAPK() + "CANCEL");
            }
        }
    }

    private void getInitialConfiguration() {
        String appName = getIntent().getStringExtra("app_name");
        String appApk = getIntent().getStringExtra("app_apk");
        String appVersion = getIntent().getStringExtra("app_version");
        String appSource = getIntent().getStringExtra("app_source");
        String appData = getIntent().getStringExtra("app_data");
        Boolean appIsSystem = getIntent().getExtras().getBoolean("app_isSystem");
        Boolean appIsFavorite = getIntent().getExtras().getBoolean("app_isFavorite");
        Boolean appIsHidden = getIntent().getExtras().getBoolean("app_isHidden");
        Boolean appIsDisabled = getIntent().getExtras().getBoolean("app_isDisabled");

        Bitmap bitmap = getIntent().getParcelableExtra("app_icon");
        Drawable appIcon = new BitmapDrawable(getResources(), bitmap);
        appInfo = new AppInfo(appName, appApk, appVersion, appSource, appData, appIsSystem, appIsFavorite, appIsHidden, appIsDisabled, appIcon);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        favorite = menu.findItem(R.id.action_favorite);
        InterfaceUtils.updateAppFavoriteIcon(context, favorite, appDbUtils.checkAppInfo(appInfo, 2));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;
            case R.id.action_favorite:
                ActionUtils.favorite(context, appInfo);
                InterfaceUtils.updateAppFavoriteIcon(context, favorite, appDbUtils.checkAppInfo(appInfo, 2));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
