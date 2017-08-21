package com.braingalore.gcm_client.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.braingalore.gcm_client.R;
import com.braingalore.gcm_client.Utils.Constants;
import com.braingalore.gcm_client.Utils.DialogUtils;
import com.braingalore.gcm_client.Utils.NetworkUtils;

public class RegisterActivity extends Activity {

    private EditText nameEditText;
    private EditText emailEditText;
    private Button registerButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (Constants.SERVER_URL == null || Constants.GOOGLE_SENDER_ID == null || Constants.SERVER_URL.length() == 0
                || Constants.GOOGLE_SENDER_ID.length() == 0) {
            DialogUtils.showAlertDialog(RegisterActivity.this, "Configuration Error",
                    "Please check your server configurations to proceed");
            return;
        }

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        registerButton = findViewById(R.id.registerButton);

        // Click event on Register button
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isValidEmail(emailEditText.getText())) {
                    String name = nameEditText.getText().toString();
                    String email = emailEditText.getText().toString();

                    // Check if user filled the form
                    if (name.trim().length() > 0) {
                        // Launch Main Activity
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.putExtra(Constants.PARAM_NAME, name);
                        i.putExtra(Constants.PARAM_EMAIL, email);
                        startActivity(i);
                        finish();
                    } else {
                        DialogUtils.showAlertDialog(RegisterActivity.this, "Registration Error", "Please enter a valid name to proceed with the registration.");
                    }
                } else {
                    DialogUtils.showAlertDialog(RegisterActivity.this, "Registration Error", "Please enter a valid email address to proceed with the registration.");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!NetworkUtils.isNetworkAvailable(this)) {
            DialogUtils.showAlertDialog(RegisterActivity.this,
                    "Network Error",
                    "Please connect to working Internet connection");
        }
    }

    /**
     * Verifies if a valid email address is entered
     *
     * @param target
     * @return
     */
    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
