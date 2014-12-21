package com.mickstarify.fortunemod;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mickstarify.fortunemod.Database.FortuneDB;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    public static FortuneDB myFortuneDB;
    private static ShareActionProvider mShareActionProvider;

    private QuoteCollectionPagerAdapter mDemoCollectionPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will display the object collection.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myFortuneDB = new FortuneDB(this);

        mDemoCollectionPagerAdapter = new QuoteCollectionPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.quote_pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
//        getMenuInflater().inflate(R.menu.share_menu, menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        getMenuInflater().inflate(R.menu.share_menu, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        return true;
    }

    public void updateShareIntent(){
        int i = mViewPager.getCurrentItem();
        Quote quote = QuoteCollectionPagerAdapter.quotes.get(i);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, quote.quote);
        setShareIntent(intent);
    }

    // Call to update the share intent
    public void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent (this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.menu_item_share) {
            this.updateShareIntent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setCategory(String category, boolean isOffensive){
        this.setTitle (String.format ("FortuneMod/%s%s", category, (isOffensive ? "-off":"")));
    }

    /**
     * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
     * representing an object in the collection.
     */
    public static class QuoteCollectionPagerAdapter extends FragmentStatePagerAdapter {
        public static Map<Integer, Quote> quotes = new HashMap<>();
        public static int currentItem;
        public QuoteCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new QuoteFragment();
            Bundle args = new Bundle();

            if (!quotes.containsKey(i)){
                quotes.put(i,myFortuneDB.getRandomQuote());
            }
            Quote quote = quotes.get(i);
            args.putString("quote", quote.quote);
            fragment.setArguments(args);

            currentItem = i;

            return fragment;
        }

        @Override
        public int getCount() {
            // For this contrived example, we have a 100-object collection.
            return 100;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            try {
                getItem(position);
                Quote quote = quotes.get(position);
                return String.format("%s%s", quote.category, (quote.isOffensive) ? "-off" : "");
            }
            catch(Exception e){
                return "error";
            }
        }
    }

    public static class QuoteFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_quote_object, container, false);
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(R.id.lbl_quote)).setText(
                    args.getString("quote")+"\n\n");
            return rootView;
        }
    }
}
