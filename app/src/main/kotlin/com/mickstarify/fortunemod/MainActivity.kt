// Copyright (c) All Rights Reserved
// Michael Johnston 2015
// Released Under GPLv3
/*
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.mickstarify.fortunemod

import android.content.Intent
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.ShareActionProvider
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.ViewGroup

import com.mickstarify.fortunemod.Database.FortuneDB

import java.util.HashMap


class MainActivity : AppCompatActivity() {

    private var mDemoCollectionPagerAdapter: QuoteCollectionPagerAdapter? = null

    /**
     * The [android.support.v4.viewo.ViewPager] that will display the object collection.
     */
    internal var mViewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myFortuneDB = FortuneDB(this)

        mDemoCollectionPagerAdapter = QuoteCollectionPagerAdapter(supportFragmentManager)
        mViewPager = findViewById(R.id.quote_pager) as ViewPager
        mViewPager.adapter = mDemoCollectionPagerAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate menu resource file.
        //        getMenuInflater().inflate(R.menu.share_menu, menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        //        getMenuInflater().inflate(R.menu.share_menu, menu);

        // Locate MenuItem with ShareActionProvider
        val item = menu.findItem(R.id.menu_item_share)

        // Fetch and store ShareActionProvider
        mShareActionProvider = MenuItemCompat.getActionProvider(item) as ShareActionProvider

        return true
    }

    fun updateShareIntent() {
        val i = mViewPager.currentItem
        val quote = QuoteCollectionPagerAdapter.quotes[i]
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, quote.quote)
        setShareIntent(intent)
    }

    // Call to update the share intent
    fun setShareIntent(shareIntent: Intent) {
        if (mShareActionProvider != null) {
            mShareActionProvider!!.setShareIntent(shareIntent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            return true
        } else if (id == R.id.menu_item_share) {
            this.updateShareIntent()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun setCategory(category: String, isOffensive: Boolean) {
        this.setTitle(String.format("FortuneMod/%s%s", category, if (isOffensive) "-off" else ""))
    }

    /**
     * A [android.support.v4.app.FragmentStatePagerAdapter] that returns a fragment
     * representing an object in the collection.
     */
    class QuoteCollectionPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(i: Int): Fragment {
            val fragment = QuoteFragment()
            val args = Bundle()

            if (!quotes.containsKey(i)) {
                quotes.put(i, myFortuneDB.randomQuote)
            }
            val quote = quotes[i]
            args.putString("quote", quote.quote)
            fragment.arguments = args

            currentItem = i

            return fragment
        }

        override fun getCount(): Int {
            // For this contrived example, we have a 100-object collection.
            return 100
        }

        override fun getPageTitle(position: Int): CharSequence {
            try {
                getItem(position)
                val quote = quotes[position]
                return String.format("%s%s", quote.category, if (quote.isOffensive) "-off" else "")
            } catch (e: Exception) {
                return "error"
            }

        }

        companion object {
            var quotes: MutableMap<Int, Quote> = HashMap()
            var currentItem: Int = 0
        }
    }

    class QuoteFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater!!.inflate(R.layout.fragment_quote_object, container, false)
            val args = arguments
            (rootView.findViewById(R.id.lbl_quote) as TextView).text = args.getString("quote")!! + "\n\n"
            return rootView
        }
    }

    companion object {

        var myFortuneDB: FortuneDB
        private var mShareActionProvider: ShareActionProvider? = null
    }
}
