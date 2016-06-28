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
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.MenuItemCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.ShareActionProvider
import android.view.*
import android.widget.TextView
import com.mickstarify.fortunemod.Database.FortuneDB
import java.util.*


class MainActivity : AppCompatActivity(),QuoteFragment.OnFragmentInteractionListener {
    //lateinit var fortuneDB : FortuneDB;
    var firstQuoteShow : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fortuneDB = FortuneDB(applicationContext)
        val quoteFragmentAdapter = QuoteFragmentPagerAdapter(supportFragmentManager, fortuneDB)

        val viewPager : ViewPager = findViewById(R.id.quote_pager) as ViewPager
        viewPager.adapter = quoteFragmentAdapter

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(position: Int) {
                setQuote((quoteFragmentAdapter.getItem(position) as QuoteFragment).quote.quote)
            }

        })

        firstQuoteShow = (quoteFragmentAdapter.getItem(0) as QuoteFragment).quote.quote
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        this.invalidateOptionsMenu() // This gets done in order to reinitialize shareActionProvider
        super.onRestoreInstanceState(savedInstanceState)
    }

    lateinit var shareActionProvider : ShareActionProvider

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val shareItem : MenuItem = menu.findItem(R.id.menu_item_share)
        shareActionProvider = MenuItemCompat.getActionProvider(shareItem) as ShareActionProvider
        setQuote(firstQuoteShow)
        return true
    }

    fun setQuote (quote : String) : Unit{
        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("text/plain")
        intent.putExtra(android.content.Intent.EXTRA_TEXT, quote)
        shareActionProvider.setShareIntent(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            return true
        } else if (id == R.id.menu_item_share) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        lateinit var fortuneDB : FortuneDB;

    }
}
