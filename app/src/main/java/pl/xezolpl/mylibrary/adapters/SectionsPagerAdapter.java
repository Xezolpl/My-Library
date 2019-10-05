package pl.xezolpl.mylibrary.adapters;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import pl.xezolpl.mylibrary.models.Status;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        addFragment(new BooksTabFragment(context, Status.NEUTRAL),"All books");
        addFragment(new BooksTabFragment(context, Status.WANT_TO_READ),"Want to read books");
        addFragment(new BooksTabFragment(context, Status.CURRENTLY_READING),"Currently reading books");
        addFragment(new BooksTabFragment(context, Status.ALREADY_READ),"Already read books");
    }

    public void addFragment(Fragment fragment, String title){
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public Fragment getItem(int i) {
        return mFragmentList.get(i);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
