package com.fako.rulet

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.fako.rulet.databinding.ActivityMainBinding
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isAnimationRunning = false
    private var selectedChipGroup1: String? = null
    private var selectedChipGroup2: String? = null
    private val colors = listOf(
        "Yeşil", "Yeşil","Yeşil",
        "Kırmızı", "Kırmızı", "Kırmızı", "Kırmızı", "Kırmızı", "Kırmızı", "Kırmızı", "Kırmızı", "Kırmızı", "Kırmızı",
        "Siyah", "Siyah", "Siyah", "Siyah", "Siyah", "Siyah", "Siyah", "Siyah", "Siyah", "Siyah",
    )
    private val colorHistory = mutableListOf<String>()
    private lateinit var historyTextViews: List<TextView>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.koyukırmızı)

        historyTextViews = listOf(
            binding.textView4, binding.textView5, binding.textView6,
            binding.textView7, binding.textView8, binding.textView9,
            binding.textView10, binding.textView11
        )

        setupStartButton()
        setupChipListeners()
    }

    private fun setupChipListeners() {
        val group1Chips = listOf(
            binding.chip0,
            binding.chip5,
            binding.chip10,
            binding.chip50,
            binding.chip100
        )


        val group2Chips = listOf(
            binding.chipY to "Yeşil",
            binding.chipK to "Kırmızı",
            binding.chipS to "Siyah"
        )


        group1Chips.forEach { chip ->
            chip.setOnClickListener {
                selectedChipGroup1 = chip.text.toString()
                updateChipState(chip)

                group1Chips.forEach { otherChip ->
                    if (otherChip != chip) {
                        otherChip.isChecked = false
                        otherChip.setChipBackgroundColorResource(R.color.black)

                    }
                }
            }
        }

        group2Chips.forEach { (chip, color) ->
            chip.setOnClickListener {
                selectedChipGroup2 = if (chip.isChecked) color else null
                updateChipState(chip)
            }
        }

    }

    private fun setupStartButton() {
        binding.butonBasla.setOnClickListener {
            if (!isAnimationRunning) {
                startAnimation()
            }
        }
    }

    private fun startAnimation() {
        isAnimationRunning = true
        binding.lottieAnimationView.playAnimation()

        val randomColor = colors.random()

        CoroutineScope(Dispatchers.Main).launch {
            delay(5000)

            binding.lottieAnimationView.cancelAnimation()
            isAnimationRunning = false

            binding.textSonuc.setText(randomColor)
            shiftAndAddColorToHistory(randomColor)


            if (selectedChipGroup2 == randomColor) {
                updateKasa(randomColor)
                Snackbar.make(binding.root, "Tebrikler! Renk eşleşti.", Snackbar.LENGTH_LONG).show()
            } else {
                deductFromKasa()
                Snackbar.make(binding.root, "Eşleşme yok. Oyun tekrar başlıyor.", Snackbar.LENGTH_LONG).show()
            }

            delay(1500)
            resetSelections()
        }
    }

    private fun updateKasa(randomColor: String) {

        val chipValue = when (selectedChipGroup1) {
            "0" -> 0
            "5" -> 5
            "10" -> 10
            "50" -> 50
            "100" -> 100
            else -> 0
        }


        var kasaValue = binding.textKasa.text.toString().toInt()



        when (selectedChipGroup2) {
            "Yeşil" -> kasaValue += chipValue * 4
            "Kırmızı", "Siyah" -> kasaValue += chipValue * 2
        }

        binding.textKasa.setText(kasaValue.toString())

        if (kasaValue <= 0) {
            Snackbar.make(binding.root, "Kaybettin!", Snackbar.LENGTH_LONG).show()
            binding.textKasa.setText("100")
        }

    }

    private fun deductFromKasa() {
        val chipValue = when (selectedChipGroup1) {
            "0" -> 0
            "5" -> 5
            "10" -> 10
            "50" -> 50
            "100" -> 100
            else -> 0
        }

        var kasaValue = binding.textKasa.text.toString().toInt()
        kasaValue -= chipValue

        binding.textKasa.setText(kasaValue.toString())

        if (kasaValue <= 0) {
            Snackbar.make(binding.root, "Kaybettin! Kasa 100", Snackbar.LENGTH_LONG).show()
            binding.textKasa.setText("100")
        }
    }
    private fun shiftAndAddColorToHistory(newColor: String) {
        if (colorHistory.size >= 8) {
            colorHistory.removeAt(colorHistory.size - 1) // En eski rengi çıkar
        }

        colorHistory.add(0, newColor) // Yeni rengi en başa ekle

        colorHistory.forEachIndexed { index, color ->
            if (index < historyTextViews.size) {
                historyTextViews[index].text = color
            }
        }
    }

    private fun resetChipColors() {
        listOf(binding.chip0, binding.chip5, binding.chip10, binding.chip50, binding.chip100).forEach { chip ->
            chip.isChecked = false
            chip.setChipBackgroundColorResource(R.color.black)
        }
        listOf(binding.chipY, binding.chipK, binding.chipS).forEach { chip ->
            chip.isChecked = false
            updateChipState(chip)
        }
    }

    private fun resetSelections() {
        selectedChipGroup2 = null
        resetChipColors()
        binding.textSonuc.setText("") //
    }

    private fun updateChipState(chip: Chip) {

        if (chip.isChecked) {
            chip.setChipBackgroundColorResource(R.color.chipColor)
        } else {
            val defaultColor = when (chip) {
                binding.chipY -> R.color.yesil
                binding.chipK -> R.color.kırmızı
                binding.chipS -> R.color.black
                else -> R.color.chip
            }
            chip.setChipBackgroundColorResource(defaultColor)
        }
    }
}