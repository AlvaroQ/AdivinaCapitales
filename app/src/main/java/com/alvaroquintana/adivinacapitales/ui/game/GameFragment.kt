package com.alvaroquintana.adivinacapitales.ui.game

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import com.alvaroquintana.adivinacapitales.R
import com.alvaroquintana.adivinacapitales.common.startActivity
import com.alvaroquintana.adivinacapitales.common.traslationAnimation
import com.alvaroquintana.adivinacapitales.common.traslationAnimationFadeIn
import com.alvaroquintana.adivinacapitales.databinding.GameFragmentBinding
import com.alvaroquintana.adivinacapitales.ui.result.ResultActivity
import com.alvaroquintana.adivinacapitales.utils.Constants.TYPE_GAME
import com.alvaroquintana.adivinacapitales.utils.Constants.POINTS
import com.alvaroquintana.adivinacapitales.utils.Constants.TOTAL_COUNTRIES
import com.alvaroquintana.adivinacapitales.utils.Constants.TypeGame
import com.alvaroquintana.adivinacapitales.utils.glideLoadBase64
import com.alvaroquintana.adivinacapitales.utils.glideLoadingGif
import com.alvaroquintana.adivinacapitales.utils.setSafeOnClickListener
import com.alvaroquintana.domain.Country
import kotlinx.coroutines.*
import org.koin.android.scope.lifecycleScope
import org.koin.android.viewmodel.scope.viewModel
import java.util.*
import java.util.concurrent.TimeUnit


class GameFragment : Fragment() {
    private val gameViewModel: GameViewModel by lifecycleScope.viewModel(this)
    private lateinit var binding: GameFragmentBinding

    lateinit var imageLoading: ImageView
    lateinit var imageQuiz: ImageView
    lateinit var textQuiz: TextView
    lateinit var btnOptionOne: TextView
    lateinit var btnOptionTwo: TextView
    lateinit var btnOptionThree: TextView
    lateinit var btnOptionFour: TextView

    private var life: Int = 3
    private var stage: Int = 1
    private var points: Int = 0
    lateinit var typeGame: Enum<TypeGame>

    companion object {
        fun newInstance() = GameFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = GameFragmentBinding.inflate(inflater)
        val root = binding.root

        typeGame = (activity?.intent?.getSerializableExtra(TYPE_GAME) as TypeGame?)!!

        imageLoading = root.findViewById(R.id.imagenLoading)
        imageQuiz = root.findViewById(R.id.imageQuiz)
        textQuiz = root.findViewById(R.id.textQuiz)
        btnOptionOne = root.findViewById(R.id.btnOptionOne)
        btnOptionTwo = root.findViewById(R.id.btnOptionTwo)
        btnOptionThree = root.findViewById(R.id.btnOptionThree)
        btnOptionFour = root.findViewById(R.id.btnOptionFour)

        btnOptionOne.setSafeOnClickListener {
            btnOptionOne.isSelected = !btnOptionOne.isSelected
            checkResponse()
        }

        btnOptionTwo.setSafeOnClickListener {
            btnOptionTwo.isSelected = !btnOptionTwo.isSelected
            checkResponse()
        }

        btnOptionThree.setSafeOnClickListener {
            btnOptionThree.isSelected = !btnOptionThree.isSelected
            checkResponse()
        }

        btnOptionFour.setSafeOnClickListener {
            btnOptionFour.isSelected = !btnOptionFour.isSelected
            checkResponse()
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameViewModel.navigation.observe(viewLifecycleOwner, Observer(::navigate))
        gameViewModel.progress.observe(viewLifecycleOwner, Observer(::updateProgress))
        gameViewModel.question.observe(viewLifecycleOwner, Observer(::drawQuestionQuiz))
        gameViewModel.responseOptions.observe(viewLifecycleOwner, Observer(::drawOptionsResponse))
    }

    private fun navigate(navigation: GameViewModel.Navigation?) {
        when (navigation) {
            is GameViewModel.Navigation.Result -> {
                activity?.startActivity<ResultActivity> { putExtra(POINTS, points) }
            }
        }
    }

    private fun updateProgress(model: GameViewModel.UiModel?) {
        if (model is GameViewModel.UiModel.Loading && model.show) {
            glideLoadingGif(activity as GameActivity, imageLoading)
            imageLoading.visibility = View.VISIBLE
            textQuiz.visibility = View.GONE
            imageQuiz.visibility = View.GONE

            btnOptionOne.isSelected = false
            btnOptionTwo.isSelected = false
            btnOptionThree.isSelected = false
            btnOptionFour.isSelected = false

            enableBtn(false)
        } else {
            imageLoading.visibility = View.GONE
            textQuiz.visibility = View.VISIBLE
            imageQuiz.visibility = View.VISIBLE

            enableBtn(true)
            (activity as GameActivity).writeStage(stage)
        }
    }

    private fun drawQuestionQuiz(country: Country) {
        when(typeGame) {
            TypeGame.BY_CONTRY -> textQuiz.text = Locale(getString(R.string.locale), country.alpha2Code!!).displayCountry
            TypeGame.BY_FLAG -> glideLoadBase64(activity as GameActivity, country.icon, imageQuiz)
        }
    }

    private fun drawOptionsResponse(optionsListByPos: MutableList<String>) {
        var delay = 150L
        if(stage == 1) {
            delay = 0L
            binding.containerButtons.traslationAnimationFadeIn()
        }
        else binding.containerButtons.traslationAnimation()

        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.MILLISECONDS.toMillis(delay))
            withContext(Dispatchers.Main) {
                btnOptionOne.text = optionsListByPos[0]
                btnOptionTwo.text = optionsListByPos[1]
                btnOptionThree.text = optionsListByPos[2]
                btnOptionFour.text = optionsListByPos[3]
            }
        }
    }

    private fun checkResponse() {
        enableBtn(false)
        stage += 1

        drawCorrectResponse(gameViewModel.getCountry()?.capital!!)
        nextScreen()
    }

    private fun deleteLife() {
        life--
        (activity as GameActivity).writeDeleteLife(life)
    }

    private fun drawCorrectResponse(capitalNameCorrect: String) {
        when {
            btnOptionOne.text == capitalNameCorrect -> {
                btnOptionOne.background =  ContextCompat.getDrawable(requireContext(), R.drawable.button_radius_correct)
                when {
                    btnOptionOne.isSelected -> {
                        soundSuccess()
                        points += 1
                    }
                    btnOptionTwo.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionTwo.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_radius_wrong)
                    }
                    btnOptionThree.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionThree.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_radius_wrong)
                    }
                    btnOptionFour.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionFour.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_radius_wrong)
                    }
                    else -> {
                        soundFail()
                        deleteLife()
                    }
                }
            }
            btnOptionTwo.text == capitalNameCorrect -> {
                btnOptionTwo.background =  ContextCompat.getDrawable(requireContext(), R.drawable.button_radius_correct)
                when {
                    btnOptionOne.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionOne.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_radius_wrong)
                    }
                    btnOptionTwo.isSelected -> {
                        soundSuccess()
                        points += 1
                    }
                    btnOptionThree.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionThree.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_radius_wrong)
                    }
                    btnOptionFour.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionFour.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_radius_wrong)
                    }
                    else -> {
                        soundFail()
                        deleteLife()
                    }
                }
            }
            btnOptionThree.text == capitalNameCorrect -> {
                btnOptionThree.background =  ContextCompat.getDrawable(requireContext(), R.drawable.button_radius_correct)
                when {
                    btnOptionOne.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionOne.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_radius_wrong)
                    }
                    btnOptionTwo.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionTwo.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_radius_wrong)
                    }
                    btnOptionThree.isSelected -> {
                        soundSuccess()
                        points += 1
                    }
                    btnOptionFour.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionFour.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_radius_wrong)
                    }
                    else -> {
                        soundFail()
                        deleteLife()
                    }
                }
            }
            btnOptionFour.text == capitalNameCorrect -> {
                btnOptionFour.background =  ContextCompat.getDrawable(requireContext(), R.drawable.button_radius_correct)
                when {
                    btnOptionOne.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionOne.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_radius_wrong)
                    }
                    btnOptionTwo.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionTwo.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_radius_wrong)
                    }
                    btnOptionThree.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionThree.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_radius_wrong)
                    }
                    btnOptionFour.isSelected -> {
                        soundSuccess()
                        points += 1
                    }
                    else -> {
                        soundFail()
                        deleteLife()
                    }
                }
            }
        }
    }

    private fun enableBtn(isEnable: Boolean) {
        btnOptionOne.isClickable = isEnable
        btnOptionTwo.isClickable = isEnable
        btnOptionThree.isClickable = isEnable
        btnOptionFour.isClickable = isEnable

        if(isEnable) {
            btnOptionOne.background = ContextCompat.getDrawable(requireContext(), R.drawable.selector_with_radius_button)
            btnOptionTwo.background = ContextCompat.getDrawable(requireContext(), R.drawable.selector_with_radius_button)
            btnOptionThree.background = ContextCompat.getDrawable(requireContext(), R.drawable.selector_with_radius_button)
            btnOptionFour.background = ContextCompat.getDrawable(requireContext(), R.drawable.selector_with_radius_button)
        }
    }

    private fun nextScreen() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.MILLISECONDS.toMillis(1000))
            withContext(Dispatchers.Main) {
                if(stage > TOTAL_COUNTRIES || life < 1) gameViewModel.navigateToResult(points.toString())
                else gameViewModel.generateNewStage()
            }
        }
    }

    private fun soundFail() {
        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sound", true)) {
            MediaPlayer.create(context, R.raw.fail).start()
        }
    }

    private fun soundSuccess() {
        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sound", true)) {
            MediaPlayer.create(context, R.raw.success).start()
        }
    }
}
