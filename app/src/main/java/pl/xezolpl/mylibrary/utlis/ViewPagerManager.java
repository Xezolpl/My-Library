package pl.xezolpl.mylibrary.utlis;

import android.content.Context;

import androidx.viewpager.widget.ViewPager;

import pl.xezolpl.mylibrary.adapters.BooksRecViewAdapter;
import pl.xezolpl.mylibrary.adapters.BooksTabFragment;
import pl.xezolpl.mylibrary.adapters.SectionsPagerAdapter;
import pl.xezolpl.mylibrary.models.Status;

public class ViewPagerManager {

    private Context context;

    private SectionsPagerAdapter sectionsPagerAdapter;

    private BooksRecViewAdapter recViewAdapter;

    private BooksTabFragment allBooksFragment, wantToReadBooksFragment,
            currReadingBooksFragment, alreadyReadBooksFragment;

    public ViewPagerManager(Context context, SectionsPagerAdapter sectionsPagerAdapter) {
        this.context = context;
        this.sectionsPagerAdapter = sectionsPagerAdapter;
    }

    private void initTabFragments() {
        allBooksFragment = new BooksTabFragment(context, Status.NEUTRAL);
        wantToReadBooksFragment = new BooksTabFragment(context, Status.WANT_TO_READ);
        currReadingBooksFragment = new BooksTabFragment(context, Status.CURRENTLY_READING);
        alreadyReadBooksFragment = new BooksTabFragment(context, Status.ALREADY_READ);
    }

    private void initVariables() {
        recViewAdapter = allBooksFragment.getAdapter();
    }

    private void setUpViewPager(final ViewPager viewPager) {
        sectionsPagerAdapter.addFragment(allBooksFragment, "All books");
        sectionsPagerAdapter.addFragment(wantToReadBooksFragment, "Want to read books");
        sectionsPagerAdapter.addFragment(currReadingBooksFragment, "Currently reading books");
        sectionsPagerAdapter.addFragment(alreadyReadBooksFragment, "Already read books");
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (viewPager.getCurrentItem()) {
                    case 0: {
                        recViewAdapter = allBooksFragment.getAdapter();
                        break;
                    }
                    case 1: {
                        recViewAdapter = wantToReadBooksFragment.getAdapter();
                        break;
                    }
                    case 2: {
                        recViewAdapter = currReadingBooksFragment.getAdapter();
                        break;
                    }
                    case 3: {
                        recViewAdapter = alreadyReadBooksFragment.getAdapter();
                        break;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
