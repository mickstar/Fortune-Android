package com.mickstarify.fortunemod

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.mickstarify.fortunemod.Database.FortuneDB
import java.util.*

/**
 * Created by michael on 12/02/16.
 */

public class QuoteFragmentPagerAdapter(val fragmentManager: FragmentManager, val fortuneDB: FortuneDB) :
        FragmentPagerAdapter(fragmentManager) {

    lateinit var quotes: MutableList<QuoteFragment>

    init {
        quotes = LinkedList<QuoteFragment>()
    }

    override fun getItem(position: Int): Fragment? {
        if (position < quotes.size) {
            return quotes[position]
        }
        while (position >= quotes.size) {
            quotes.add(QuoteFragment.newInstance(fortuneDB.randomQuote))
        }
        return quotes[position]
    }

    override fun getCount(): Int {
        return 10000
    }

    override fun getPageTitle(position: Int): CharSequence {
        val quoteFragment = getItem (position) as QuoteFragment
        val quote = quoteFragment.quote
        if (quote.isOffensive) {
            return "${quote.category}-off"
        }
        return "${quote.category}"
    }

}