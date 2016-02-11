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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fortuneDB = FortuneDB(applicationContext)
        val quoteFragmentAdapter = QuoteFragmentPagerAdapter(supportFragmentManager, fortuneDB)

        val viewPager : ViewPager = findViewById(R.id.quote_pager) as ViewPager
        viewPager.adapter = quoteFragmentAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
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
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun setCategory(category: String, isOffensive: Boolean) {
        this.setTitle(String.format("FortuneMod/%s%s", category, if (isOffensive) "-off" else ""))
    }

    companion object {
        lateinit var fortuneDB : FortuneDB;
    }
}
