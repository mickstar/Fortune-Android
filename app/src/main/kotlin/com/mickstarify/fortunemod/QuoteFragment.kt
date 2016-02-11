package com.mickstarify.fortunemod

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [QuoteFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [QuoteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QuoteFragment : Fragment() {

    // TODO: Rename and change types of parameters

    private var mListener: OnFragmentInteractionListener? = null
    lateinit public var quote : Quote;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            quote = arguments.getParcelable<Quote>(ARG_QUOTE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView : View = inflater!!.inflate(R.layout.fragment_quote, container, false)
        val quoteTextView : TextView = rootView.findViewById(R.id.lbl_quote) as TextView
        quoteTextView.text = "${quote.quote}\n"
        return rootView
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context as OnFragmentInteractionListener?
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
//        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        private val ARG_QUOTE = "quote"

        fun newInstance(quote: Quote): QuoteFragment {
            val fragment = QuoteFragment()
            fragment.quote = quote
            val args = Bundle()
            args.putParcelable(ARG_QUOTE, quote)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
