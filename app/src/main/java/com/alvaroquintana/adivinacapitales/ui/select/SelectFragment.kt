package com.alvaroquintana.adivinacapitales.ui.select

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.alvaroquintana.adivinacapitales.R
import com.alvaroquintana.adivinacapitales.common.startActivity
import com.alvaroquintana.adivinacapitales.databinding.SelectFragmentBinding
import androidx.lifecycle.Observer
import com.alvaroquintana.adivinacapitales.security.IntegrityManager
import com.alvaroquintana.adivinacapitales.ui.game.GameActivity
import com.alvaroquintana.adivinacapitales.ui.info.InfoActivity
import com.alvaroquintana.adivinacapitales.ui.settings.SettingsActivity
import com.alvaroquintana.adivinacapitales.utils.Constants
import com.alvaroquintana.adivinacapitales.utils.setSafeOnClickListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import org.koin.android.scope.lifecycleScope
import org.koin.android.viewmodel.scope.viewModel

class SelectFragment : Fragment() {
    private lateinit var binding: SelectFragmentBinding
    private val selectViewModel: SelectViewModel by lifecycleScope.viewModel(this)

    companion object {
        fun newInstance() = SelectFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SelectFragmentBinding.inflate(inflater)
        val root = binding.root

        val btnStartByFlag: CardView = root.findViewById(R.id.btnStartByFlag)
        btnStartByFlag.setSafeOnClickListener {
            selectViewModel.navigateToGameByFlag()
        }

        val btnStartByCountry: CardView = root.findViewById(R.id.btnStartByCountry)
        btnStartByCountry.setSafeOnClickListener {
            selectViewModel.navigateToGameByCountry()
        }

        val btnSettings: CardView = root.findViewById(R.id.btnSettings)
        btnSettings.setSafeOnClickListener {
            selectViewModel.navigateToSettings()
        }

        val btnLearn: CardView = root.findViewById(R.id.btnLearn)
        btnLearn.setSafeOnClickListener {
            selectViewModel.navigateToLearn()
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectViewModel.navigation.observe(viewLifecycleOwner, Observer(::navigate))

        val integrityManager = IntegrityManager(requireContext(), selectViewModel)
        integrityManager.integrityToken()
    }

    private fun navigate(navigation: SelectViewModel.Navigation?) {
        when (navigation) {
            SelectViewModel.Navigation.Settings -> activity?.startActivity<SettingsActivity> { }
            SelectViewModel.Navigation.Learn -> activity?.startActivity<InfoActivity> { }
            SelectViewModel.Navigation.GameByFlag -> {
                activity?.startActivity<GameActivity> { putExtra(Constants.TYPE_GAME, Constants.TypeGame.BY_FLAG) }
            }
            SelectViewModel.Navigation.GameByCountry -> {
                activity?.startActivity<GameActivity> { putExtra(Constants.TYPE_GAME, Constants.TypeGame.BY_CONTRY) }
            }
        }
    }
}
