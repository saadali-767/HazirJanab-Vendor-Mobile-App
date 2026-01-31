package com.example.hazirjanabvendorportal;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.hazirjanabvendorportal.Fragments.ProductDetails;
import com.example.hazirjanabvendorportal.Fragments.ServiceDetails;

public class AdapterViewPager extends FragmentStateAdapter {
    public AdapterViewPager(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new ServiceDetails();
            case 1:
                return new ProductDetails();
            default:
                    return new ServiceDetails();
        }
    }


    @Override
    public int getItemCount() {
        return 2;
    }
}
