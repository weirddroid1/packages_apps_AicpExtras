/*
 * Copyright (C) 2017-2026 AICP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aicp.extras.fragments

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import androidx.preference.Preference
import com.aicp.extras.BaseSettingsFragment
import com.aicp.extras.R
import com.aicp.gear.preference.LongClickablePreference
import com.plattysoft.leonids.ParticleSystem
import java.util.Random

class Dashboard : BaseSettingsFragment() {

    companion object {
        private const val PREF_AICP_LOGO = "aicp_logo"
    }

    private lateinit var aicpLogo: LongClickablePreference

    private val random = Random()
    private var logoClickCount = 0

    override fun getPreferenceResource(): Int = R.xml.dashboard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        aicpLogo = findPreference(PREF_AICP_LOGO)!!

        setupLogoClick()
        setupLogoLongClick()
    }

    private fun setupLogoClick() {
        aicpLogo.setOnPreferenceClickListener {

            val firstRandom = random.nextInt(91)
            val secondRandom = random.nextInt(91) + 90
            val thirdRandom = random.nextInt(181)

            val star: Drawable =
                requireContext().getDrawable(R.drawable.star_white_border)!!

            val randomColor = Color.rgb(
                Color.red(random.nextInt(0xFFFFFF)),
                Color.green(random.nextInt(0xFFFFFF)),
                Color.blue(random.nextInt(0xFFFFFF))
            )
            star.setTint(randomColor)

            val ps = ParticleSystem(requireActivity(), 100, star, 3000)
            ps.setScaleRange(0.7f, 1.3f)
            ps.setSpeedRange(0.1f, 0.25f)
            ps.setAcceleration(0.0001f, thirdRandom)
            ps.setRotationSpeedRange(firstRandom.toFloat(), secondRandom.toFloat())
            ps.setFadeOut(200, AccelerateInterpolator())
            ps.oneShot(view, 100)

            aicpLogo.setLongClickBurst(2000 / ((++logoClickCount) % 5 + 1))
            true
        }
    }

    private fun setupLogoLongClick() {
        aicpLogo.setOnLongClickListener(
            R.id.logo_view,
            1000
        ) {

            val firstRandom = random.nextInt(91)
            val secondRandom = random.nextInt(91) + 90
            val thirdRandom = random.nextInt(181)

            val star: Drawable =
                requireContext().getDrawable(R.drawable.star_alternative)!!

            val ps = ParticleSystem(requireActivity(), 100, star, 3000)
            ps.setScaleRange(0.7f, 1.3f)
            ps.setSpeedRange(0.1f, 0.25f)
            ps.setAcceleration(0.0001f, thirdRandom)
            ps.setRotationSpeedRange(firstRandom.toFloat(), secondRandom.toFloat())
            ps.setFadeOut(1000, AccelerateInterpolator())
            ps.oneShot(view, 100)
            true
        }
    }
}

