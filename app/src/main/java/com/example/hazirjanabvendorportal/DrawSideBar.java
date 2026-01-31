package com.example.hazirjanabvendorportal;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.MenuItemCompat;
import com.google.android.material.badge.BadgeDrawable;


public class DrawSideBar {

    Activity activity;
    private DrawerLayout SideBarDrawer;
    private ImageView SideBarMenu;
    private NavigationView NavigationDrawer1;
    private NavigationView NavigationDrawer2;
    private NotificationBadge notificationBadge;

    public DrawSideBar(Activity activity) {
        this.activity = activity;
        notificationBadge = NotificationBadge.getInstance(activity);
    }

    public void setup(Activity activity) {
        SideBarDrawer = activity.findViewById(R.id.SideBarDrawer);
        SideBarMenu = activity.findViewById(R.id.ivSideBar);

        if (SideBarMenu != null) {
            SideBarMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DrawSideBar", "SideBarMenu clicked");
                    if (SideBarDrawer != null) {
                        if (SideBarDrawer.isDrawerOpen(GravityCompat.START))
                            SideBarDrawer.closeDrawer(GravityCompat.START);
                        else
                            SideBarDrawer.openDrawer(GravityCompat.START);
                    }
                }
            });
        }

        NavigationDrawer1 = activity.findViewById(R.id.NavigationDrawerMain);
        if (NavigationDrawer1 != null) {
            NavigationDrawer1.setItemIconTintList(null);

            MenuItem headerSettingsItem = NavigationDrawer1.getMenu().findItem(R.id.main_item);
            if (headerSettingsItem != null) {
                SpannableString spannableString1 = new SpannableString(headerSettingsItem.getTitle());
                spannableString1.setSpan(new AbsoluteSizeSpan(24, true), 0, spannableString1.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                headerSettingsItem.setTitle(spannableString1);
            }

            Menu menu = NavigationDrawer1.getMenu();
            MenuItem menuItem;
            handleMenuItem(menu.getItem(0), activity.getClass());

            // Loop through each item in the menu
            for (int i = 0; i < menu.size(); i++) {
                menuItem = menu.getItem(i);
                // Get the item ID
                int itemId = menuItem.getItemId();

            }

            NavigationDrawer1.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.iSupport) {
                        Intent intent = new Intent(activity, Activity_Support.class);
                        activity.startActivity(intent);
                        activity.finish();
                        return true;
                    } else if (item.getItemId() == R.id.iHome) {
                        Intent intent = new Intent(activity, Activity_RequestsList.class);
                        activity.startActivity(intent);
                        activity.finish();
                        return true;
                    } else if (item.getItemId() == R.id.iAcceptedServices) {
                        Intent intent = new Intent(activity, Activity_AcceptedBookingsList.class);
                        activity.startActivity(intent);
                        activity.finish();
                        return true;
                    } else if (item.getItemId() == R.id.iCompletedServices) {
                        Intent intent = new Intent(activity, Activity_CompletedOrdersList.class);
                        activity.startActivity(intent);
                        activity.finish();
                        return true;
                    } else if (item.getItemId() == R.id.iRewards) {
//                        Intent intent = new Intent(activity, Activity_Rewards.class);
//                        activity.startActivity(intent);
                        Toast.makeText(activity, "Rewards Clciked!", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                }
            });
        }

        NavigationDrawer2 = activity.findViewById(R.id.NavigationDrawerSettings);
        if (NavigationDrawer2 != null) {
            NavigationDrawer2.setItemIconTintList(null);

            MenuItem headerSettingsItem = NavigationDrawer2.getMenu().findItem(R.id.main_settings_item);
            if (headerSettingsItem != null) {
                SpannableString spannableString1 = new SpannableString(headerSettingsItem.getTitle());
                spannableString1.setSpan(new AbsoluteSizeSpan(24, true), 0, spannableString1.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                headerSettingsItem.setTitle(spannableString1);
            }

            NavigationDrawer2.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.iLogout) {
                        Intent intent = new Intent(activity, Activity_Login.class);
                        activity.startActivity(intent);
                        activity.finish();
                        return true;
                    }
                    return false;
                }
            });
        }

        ImageView btnBackArrow = activity.findViewById(R.id.back_arrow);
        if (btnBackArrow != null) {
            btnBackArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SideBarDrawer != null) {
                        SideBarDrawer.closeDrawer(GravityCompat.START);
                    }
                }
            });
        }
    }

    // Method to get badge count for each item (You need to implement this according to your logic)
    private int getBadgeCountForItem(int itemId) {
        // Implement your logic to get badge count for each item
        // For demonstration, returning a hardcoded value
        if (itemId == R.id.iSupport) {
            return notificationBadge.getCounter("Support");
        } else if (itemId == R.id.iHome) {
            return notificationBadge.getCounter("Home");
        } else if (itemId == R.id.iAcceptedServices) {
            return notificationBadge.getCounter("Accepted_services");
        } else if (itemId == R.id.iCompletedServices) {
            return notificationBadge.getCounter("Completed_Services");
        } else if (itemId == R.id.iRewards) {
            return notificationBadge.getCounter("Rewards");
        }
        return -1;
    }

    private void handleMenuItem(MenuItem menuItem, Class<?> currentActivityClass) {
        // Check if the current item has a submenu
        if (menuItem.hasSubMenu()) {
            // If it has a submenu, iterate over its items recursively
            Menu subMenu = menuItem.getSubMenu();
            for (int j = 0; j < subMenu.size(); j++) {
                MenuItem subMenuItem = subMenu.getItem(j);
                // Handle the submenu item
                handleMenuItem(subMenuItem, currentActivityClass);
            }
        } else {
            Log.d("DrawSideBar", "handleMenuItem: " + menuItem.getTitle());
            // Here you can set the badge count or text based on your notification count
            // Here you can set the badge count or text based on your notification count
            int notificationCount = getBadgeCountForItem(menuItem.getItemId());
            Log.d("DrawSideBar", "Notification count for " + menuItem.getTitle() + " is " + notificationCount);

            // Inflate custom layout for action view
            View actionView = LayoutInflater.from(activity).inflate(R.layout.template_menu_layout, null);

            TextView badgeView = actionView.findViewById(R.id.menu_badge);

            // Set badge count
            if (notificationCount > 0) {
                badgeView.setVisibility(View.VISIBLE);
                badgeView.setText(String.valueOf(notificationCount));
            } else {
                badgeView.setVisibility(View.GONE);
            }

            // Check if the current activity is the "Home" activity
            Log.d("DrawSideBar", "Current activity class: " + currentActivityClass.getSimpleName());
            Log.d("DrawSideBar", "Menu item title: " + menuItem.getTitle());

            String currentActivityName = currentActivityClass.getSimpleName();
            switch (currentActivityName) {
                case "Activity_RequestsList":
                    currentActivityName = "Home";
                    break;
                case "Activity_AcceptedBookingsList":
                    currentActivityName = "Accepted Services";
                    break;
                case "Activity_CompletedOrdersList":
                    currentActivityName = "Completed Services";
                    break;
                case "Activity_Support":
                    currentActivityName = "Support";
                    break;
            }

            if (currentActivityClass != null && currentActivityName.equals(menuItem.getTitle())) {
                // Hide the menu item
                Log.d("DrawSideBar", "Hiding menu item: " + menuItem.getTitle());
                menuItem.setVisible(false);
            }

            // Set action view to menu item
            menuItem.setActionView(actionView);
        }
    }


}
