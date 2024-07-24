package com.bity.icpkotlinkit.presentation.send

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bity.icpkotlinkit.R
import org.koin.androidx.navigation.koinNavGraphViewModel

class SendFragment: Fragment() {

    private val args: SendFragmentArgs by navArgs()
    private val model: SendViewModel by koinNavGraphViewModel(R.id.sendFragment)
}