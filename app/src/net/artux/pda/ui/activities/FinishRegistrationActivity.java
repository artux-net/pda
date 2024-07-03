package net.artux.pda.ui.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import net.artux.pda.R;
import net.artux.pda.databinding.ActivityFinishRegistrationBinding;

public class FinishRegistrationActivity extends AppCompatActivity {

    ActivityFinishRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFinishRegistrationBinding.inflate(getLayoutInflater());

        binding.registrationEndDesc.setText(getString(R.string.registrationEndDesc, getIntent().getStringExtra("email")));
        binding.endBtn.setOnClickListener(view -> finish());
        setContentView(binding.getRoot());
    }
}