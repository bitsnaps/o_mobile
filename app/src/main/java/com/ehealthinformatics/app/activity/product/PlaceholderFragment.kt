package com.ehealthinformatics.app.activity.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

import com.ehealthinformatics.R

import java.util.ArrayList

class PlaceholderFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View
        if (arguments!!.getInt(ARG_SECTION_NUMBER) == 1) {
            rootView = inflater.inflate(R.layout.layout_product_basic, container, false)
        } else {
            rootView = inflater.inflate(R.layout.layout_product_stock, container, false)
        }
        return rootView
    }

    interface ControlListener {
        fun initControlListeners()
    }

    class SectionsPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    companion object {
        private val ARG_SECTION_NUMBER = "section_number"

        fun newInstance(view: Int): PlaceholderFragment {
            val fragment = PlaceholderFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, view)
            fragment.arguments = args
            return fragment
        }
    }
}