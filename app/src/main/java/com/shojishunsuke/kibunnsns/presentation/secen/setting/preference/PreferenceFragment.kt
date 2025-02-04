package com.shojishunsuke.kibunnsns.presentation.secen.setting.preference

import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.shojishunsuke.kibunnsns.R

class PreferenceFragment : PreferenceFragmentCompat() {

    companion object {
        private const val RC_SIGN_IN: Int = 123
        private const val RESULT_OK: Int = -1
    }

    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)

        val authPreference = findPreference("authenticate")
        authPreference.setOnPreferenceClickListener {
            startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                    listOf(
                        AuthUI.IdpConfig.GoogleBuilder().build(),
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        AuthUI.IdpConfig.PhoneBuilder().build()
                    )
                ).build(), RC_SIGN_IN
            )
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == RESULT_OK) {
                fireBaseAuthWithGoogle(response!!.idpToken!!)
            }
        }
    }

    private fun fireBaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        user?.linkWithCredential(credential)?.addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
            } else {
            }
        }
    }
}