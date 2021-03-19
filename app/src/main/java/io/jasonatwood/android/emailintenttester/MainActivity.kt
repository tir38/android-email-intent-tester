package io.jasonatwood.android.emailintenttester

import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Bundle
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity() {

    private var generatedIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        copyAssets(applicationContext)

        setContentView(R.layout.activity_main)

        findViewById<MaterialCheckBox>(R.id.extra_email_checkbox)
            .setOnCheckedChangeListener { _, isChecked ->
                findViewById<MaterialCheckBox>(R.id.extra_email_in_array_checkbox).isEnabled =
                    isChecked
            }

        findViewById<MaterialCheckBox>(R.id.extra_cc_checkbox)
            .setOnCheckedChangeListener { _, isChecked ->
                findViewById<MaterialCheckBox>(R.id.extra_cc_in_array_checkbox).isEnabled =
                    isChecked
            }

        findViewById<MaterialCheckBox>(R.id.extra_bcc_checkbox)
            .setOnCheckedChangeListener { _, isChecked ->
                findViewById<MaterialCheckBox>(R.id.extra_bcc_in_array_checkbox).isEnabled =
                    isChecked
            }

        findViewById<MaterialButton>(R.id.generate_button).setOnClickListener {
            generatedIntent = buildIntentFromSettings()
            findViewById<MaterialTextView>(R.id.output_textview).text =
                intentAsReadableString(generatedIntent)
        }

        findViewById<MaterialButton>(R.id.fire_intent_button).setOnClickListener {
            try {
                generatedIntent?.let { startActivity(it) }
            } catch (exception: Exception) {
                Snackbar
                    .make(
                        findViewById(R.id.coordinator_layout),
                        exception.message!!,
                        Snackbar.LENGTH_LONG
                    )
                    .setBackgroundTint(ContextCompat.getColor(this, R.color.red))
                    .show()
            }
        }
    }

    private fun intentAsReadableString(intent: Intent?): String {
        if (intent == null) {
            return "null"
        }
        return StringBuilder()
            .appendLine(intent.toString())
            .appendLine(bundleToString(intent.extras))
            .toString()
    }

    private fun bundleToString(extras: Bundle?): String {
        if (extras == null) {
            return ""
        }

        val strBuilder = StringBuilder()
        strBuilder.appendLine("...with extras:")
        for (key in extras.keySet()) {
            strBuilder.appendLine(key + " : " + extras.get(key).toString())
        }

        return strBuilder.toString()
    }

    private fun buildIntentFromSettings(): Intent {
        val action: String? = getActionFromRadioGroup()
        val intent = setTypeOrDataFromRadioGroup(Intent(action))
        return putExtras(intent)
    }

    private fun getActionFromRadioGroup(): String? {
        findViewById<RadioGroup>(R.id.action_radio_group).run {
            return when (checkedRadioButtonId) {
                R.id.action_send_radio_button ->
                    ACTION_SEND
                R.id.action_send_to_radio_button ->
                    ACTION_SENDTO
                R.id.action_send_multiple_radio_button ->
                    ACTION_SEND_MULTIPLE
                else ->
                    null
            }
        }
    }

    private fun setTypeOrDataFromRadioGroup(intent: Intent): Intent {
        findViewById<RadioGroup>(R.id.type_radio_group).run {
            when (checkedRadioButtonId) {
                R.id.type_text_plain_radio_button ->
                    intent.type = "text/plain"
                R.id.type_asterisk_radio_button ->
                    intent.type = "*/*"
                R.id.data_mailto_radio_button ->
                    intent.data = Uri.parse("mailto:")
                else -> {
                    // do nothing
                }
            }
            return intent
        }
    }

    private fun putExtras(initialIntent: Intent): Intent {
        if (findViewById<MaterialCheckBox>(R.id.extra_email_checkbox).isChecked) {
            val email = "bob@example.com"
            val emailValue =
                if (findViewById<MaterialCheckBox>(R.id.extra_email_in_array_checkbox).isChecked) {
                    arrayOf(email)
                } else {
                    email
                }
            initialIntent.putExtra(EXTRA_EMAIL, emailValue)
        }

        if (findViewById<MaterialCheckBox>(R.id.extra_cc_checkbox).isChecked) {
            val email = "alice@example.com"
            val emailValue =
                if (findViewById<MaterialCheckBox>(R.id.extra_cc_in_array_checkbox).isChecked) {
                    arrayOf(email)
                } else {
                    email
                }
            initialIntent.putExtra(EXTRA_CC, emailValue)
        }

        if (findViewById<MaterialCheckBox>(R.id.extra_bcc_checkbox).isChecked) {
            val email = "carl@example.com"
            val emailValue =
                if (findViewById<MaterialCheckBox>(R.id.extra_bcc_in_array_checkbox).isChecked) {
                    arrayOf(email)
                } else {
                    email
                }
            initialIntent.putExtra(EXTRA_BCC, emailValue)
        }

        if (findViewById<MaterialCheckBox>(R.id.extra_subject_checkbox).isChecked) {
            initialIntent.putExtra(EXTRA_SUBJECT, "This is a test subject...")
        }

        if (findViewById<MaterialCheckBox>(R.id.extra_text_checkbox).isChecked) {
            initialIntent.putExtra(EXTRA_TEXT, "This is the body of the message...")
        }
        if (findViewById<MaterialCheckBox>(R.id.extra_stream_checkbox).isChecked) {
            initialIntent.putExtra(EXTRA_STREAM, generateUriFromImage(applicationContext))
            initialIntent.addFlags(
                FLAG_GRANT_READ_URI_PERMISSION or FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }

        return initialIntent
    }
}