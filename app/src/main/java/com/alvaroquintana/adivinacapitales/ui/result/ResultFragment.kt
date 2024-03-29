package com.alvaroquintana.adivinacapitales.ui.result

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import com.alvaroquintana.adivinacapitales.BuildConfig
import com.alvaroquintana.adivinacapitales.R
import com.alvaroquintana.adivinacapitales.common.startActivity
import com.alvaroquintana.adivinacapitales.databinding.DialogSaveRecordBinding
import com.alvaroquintana.adivinacapitales.databinding.ResultFragmentBinding
import com.alvaroquintana.adivinacapitales.ui.ranking.RankingActivity
import com.alvaroquintana.adivinacapitales.utils.Constants.POINTS
import com.alvaroquintana.adivinacapitales.utils.glideLoadingGif
import com.alvaroquintana.adivinacapitales.utils.log
import com.alvaroquintana.adivinacapitales.utils.setSafeOnClickListener
import com.alvaroquintana.domain.App
import com.alvaroquintana.domain.User
import org.koin.androidx.viewmodel.ext.android.viewModel


class ResultFragment : Fragment() {
    private lateinit var binding: ResultFragmentBinding
    private val resultViewModel: ResultViewModel by viewModel()
    private var gamePoints = 0

    companion object {
        fun newInstance() = ResultFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = ResultFragmentBinding.inflate(inflater)
        val root = binding.root

        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sound", true)) {
            MediaPlayer.create(context, R.raw.game_over).start()
        }
        gamePoints = activity?.intent?.extras?.getInt(POINTS)!!

        val textResult: TextView = root.findViewById(R.id.textResult)
        textResult.text = resources.getString(R.string.result, gamePoints)

        resultViewModel.getPersonalRecord(gamePoints, requireContext())
        resultViewModel.setPersonalRecordOnServer(gamePoints)

        val btnContinue: TextView = root.findViewById(R.id.btnContinue)
        btnContinue.setSafeOnClickListener { resultViewModel.navigateToGame() }

        val btnShare: TextView = root.findViewById(R.id.btnShare)
        btnShare.setSafeOnClickListener { resultViewModel.navigateToShare(gamePoints) }

        val btnRate: TextView = root.findViewById(R.id.btnRate)
        btnRate.setSafeOnClickListener { resultViewModel.navigateToRate() }

        val btnRanking: TextView = root.findViewById(R.id.btnRanking)
        btnRanking.setSafeOnClickListener { resultViewModel.navigateToRanking() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resultViewModel.navigation.observe(viewLifecycleOwner, Observer(::navigate))
        resultViewModel.progress.observe(viewLifecycleOwner, Observer(::updateProgress))
        resultViewModel.list.observe(viewLifecycleOwner, Observer(::fillAppList))
        resultViewModel.personalRecord.observe(viewLifecycleOwner, Observer(::fillPersonalRecord))
        resultViewModel.worldRecord.observe(viewLifecycleOwner, Observer(::fillWorldRecord))
    }

    private fun fillWorldRecord(recordWorldPoints: String) {
        binding.textWorldRecord.text = resources.getString(R.string.world_record, recordWorldPoints)
    }

    private fun fillPersonalRecord(points: String) {
        binding.textPersonalRecord.text = resources.getString(R.string.personal_record, points)
    }

    private fun fillAppList(appList: MutableList<App>) {
        binding.recyclerviewOtherApps.adapter = AppListAdapter(
            activity as ResultActivity,
            appList,
            resultViewModel::onAppClicked
        )
    }

    private fun updateProgress(model: ResultViewModel.UiModel?) {
        if (model is ResultViewModel.UiModel.Loading && model.show) {
            glideLoadingGif(activity as ResultActivity, binding.imagenLoading)
            binding.imagenLoading.visibility = View.VISIBLE
        } else {
            binding.imagenLoading.visibility = View.GONE
        }
    }

    private fun navigate(navigation: ResultViewModel.Navigation) {
        when (navigation) {
            ResultViewModel.Navigation.Rate -> rateApp()
            ResultViewModel.Navigation.Game -> activity?.finishAfterTransition()
            ResultViewModel.Navigation.Ranking -> activity?.startActivity<RankingActivity> {}
            is ResultViewModel.Navigation.Share -> shareApp(navigation.points)
            is ResultViewModel.Navigation.Open -> openAppOnPlayStore(navigation.url)
            is ResultViewModel.Navigation.Dialog -> showEnterNameDialog(navigation.points)
        }
    }

    private fun openAppOnPlayStore(appPackageName: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appPackageName)))
        } catch (notFoundException: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        }
    }

    private fun shareApp(points: Int) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            var shareMessage = resources.getString(R.string.share_message, points)
            shareMessage =
                """
                ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                """.trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, getString(R.string.choose_one)))
        } catch (e: Exception) {
            log(getString(R.string.share), e.toString())
        }
    }

    private fun rateApp() {
        val uri: Uri = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")))
        }
    }

    private fun showEnterNameDialog(points: String) {
        Dialog(requireContext()).apply {
            val binding = DialogSaveRecordBinding.inflate(layoutInflater)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(binding.root)
            binding.btnSubmit.setSafeOnClickListener {
                val name = binding.editTextWorldRecord.text.toString().ifEmpty { "Anonymous" }
                resultViewModel.saveTopScore(User(name, points, points.toInt()))
                dismiss()
            }
            show()
        }
    }
}
