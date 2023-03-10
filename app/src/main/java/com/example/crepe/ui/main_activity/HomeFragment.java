package com.example.crepe.ui.main_activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.crepe.R;
import com.example.crepe.database.DatabaseManager;
import com.example.crepe.database.Ride;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private DatabaseManager dbManager;
    private List<Ride> rideList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        dbManager = new DatabaseManager(this.getActivity());

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Green Hub");
        try {
            initCollectorList();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void initCollectorList() throws PackageManager.NameNotFoundException {
        rideList = dbManager.getAllRides();
//        Toast.makeText(this.getActivity(), "Collector number: " + rideList.size(), Toast.LENGTH_LONG).show();
        //get all installed apps
        Map<String, Drawable> apps = getAppImage();
        CollectorCardConstraintLayoutBuilder builder = new CollectorCardConstraintLayoutBuilder(getActivity(), homeFragmentRefreshCollectorListRunnable,apps);

        LinearLayout fragmentInnerLinearLayout = getView().findViewById(R.id.fragment_home_inner_linear_layout);
        fragmentInnerLinearLayout.removeAllViews();
//        for (Ride ride : rideList) {
        for (int i = rideList.size()-1;i>=0;i--) {
                Ride ride = rideList.get(i);
                ConstraintLayout collectorCardView = builder.build(ride, fragmentInnerLinearLayout, "cardLayout");
                // if the cardView is not null, meaning the collector is not in deleted status
                if (collectorCardView != null) {
                    collectorCardView.setId(View.generateViewId());

                    // Toast.makeText(this.getActivity(), fragmentInnerConstraintLayout.toString(), Toast.LENGTH_LONG).show();
                    fragmentInnerLinearLayout.addView(collectorCardView);
                }
        }
    }

    Runnable homeFragmentRefreshCollectorListRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                initCollectorList();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    };


    public Map<String, Drawable> getAppImage() throws PackageManager.NameNotFoundException {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        // get list of all the apps installed
        List<ResolveInfo> ril = getContext().getPackageManager().queryIntentActivities(mainIntent, 0);
//        List<String> componentList = new ArrayList<String>();
        String name = null;
        Drawable image = null;
        String packageName = "com.example.crepe";


        // get size of ril and create a list
        Map<String, Drawable> apps = new HashMap<String, Drawable>();
        for (ResolveInfo ri : ril) {
            if (ri.activityInfo != null) {
                // get package
                Resources res = getContext().getPackageManager().getResourcesForApplication(ri.activityInfo.applicationInfo);
                // if activity label res is found
                if (ri.activityInfo.labelRes != 0) {
                    name = res.getString(ri.activityInfo.labelRes);
                } else {
                    name = ri.activityInfo.applicationInfo.loadLabel(
                            getContext().getPackageManager()).toString();

                }
                packageName = ri.activityInfo.packageName;
                image = getContext().getPackageManager().getApplicationIcon(packageName);
                apps.put(name,image);
            }
        }
        return apps;
    }




}
