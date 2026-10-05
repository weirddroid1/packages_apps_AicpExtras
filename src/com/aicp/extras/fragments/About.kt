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

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemProperties
import androidx.preference.Preference
import com.aicp.extras.BaseSettingsFragment
import com.aicp.extras.Constants
import com.aicp.extras.HiddenAnimActivity
import com.aicp.extras.PreferenceMultiClickHandler
import com.aicp.extras.R
import com.aicp.extras.utils.Util

class About : BaseSettingsFragment() {

    private lateinit var mDeviceMaintainer: Preference
    private lateinit var mAicpVersion: Preference
    private lateinit var mBuildDate: Preference
    private var mAicpOTA: Preference? = null

    companion object {
        private const val PROPERTY_MAINTAINER = "ro.aicp.maintainer"
        private const val PREF_DEVICE_MAINTAINER = "device_maintainer"
        private const val PROPERTY_AICP_VERSION = "ro.aicp.version"
        private const val PREF_AICP_VERSION = "aicp_version"
        private const val PROPERTY_BUILD_DATE = "ro.build.date"
        private const val PREF_BUILD_DATE = "build_date"
        private const val PREF_AICP_LOGO = "aicp_logo"
        private const val PREF_AICP_OTA = "aicp_ota"
        private const val PREF_LOG_IT = "log_it"

        private val INTENT_OTA = Intent().setComponent(
            ComponentName(
                Constants.AICP_OTA_PACKAGE,
                Constants.AICP_OTA_ACTIVITY
            )
        )
    }

    override fun getPreferenceResource(): Int = R.xml.about

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pm: PackageManager = requireActivity().packageManager

        mDeviceMaintainer = findPreference(PREF_DEVICE_MAINTAINER)!!
        mDeviceMaintainer.summary = Build.MODEL

        mAicpVersion = findPreference(PREF_AICP_VERSION)!!
        mAicpVersion.summary = SystemProperties.get(PROPERTY_AICP_VERSION, "")

        mBuildDate = findPreference(PREF_BUILD_DATE)!!
        mBuildDate.summary = SystemProperties.get(PROPERTY_BUILD_DATE, "")

        val aicpLogo = findPreference<Preference>(PREF_AICP_LOGO)!!
        aicpLogo.onPreferenceClickListener = PreferenceMultiClickHandler(
            Runnable { startActivity(Intent(activity, HiddenAnimActivity::class.java)) },
            5,
            500
        )

        mAicpOTA = findPreference(PREF_AICP_OTA)
        if (!Util.isPackageEnabled(Constants.AICP_OTA_PACKAGE, pm)) {
            mAicpOTA?.let { pref -> pref.parent?.removePreference(pref) }
        }

        val logIt = findPreference<Preference>(PREF_LOG_IT)
        Util.requireRoot(requireActivity(), logIt)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return when (preference) {
            mDeviceMaintainer -> {
                showMaintainerDialog()
                true
            }
            mAicpOTA -> {
                startActivity(INTENT_OTA)
                true
            }
            else -> super.onPreferenceTreeClick(preference)
        }
    }

    private fun showMaintainerDialog() {
        try {
            val maintainer = SystemProperties.get(
                PROPERTY_MAINTAINER,
                resources.getString(R.string.device_maintainer_default)
            )
            val title = if (maintainer.contains(",") || maintainer.contains("&")) {
                resources.getString(R.string.device_maintainers_dialog)
            } else {
                resources.getString(R.string.device_maintainer_dialog)
            }
            val maintainers = maintainer
                .replace(" , ", "\n")
                .replace(", ", "\n")
                .replace(",", "\n")
                .replace(" & ", "\n")
                .replace("& ", "\n")
                .replace("&", "\n")

            AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(maintainers)
                .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
                .show()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }
}
