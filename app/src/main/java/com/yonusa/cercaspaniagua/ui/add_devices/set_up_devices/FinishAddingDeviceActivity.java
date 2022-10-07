package com.yonusa.cercaspaniagua.ui.add_devices.set_up_devices;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.yonusa.cercaspaniagua.R;
import com.yonusa.cercaspaniagua.ui.cercas.Lista_cercas;

public class FinishAddingDeviceActivity extends AppCompatActivity {
    private final String FROM_FINISH_ADD = "com.yonusa.cercaspaniagua.ui.login.view.FROM_FINISH_ADD";
    Context context;
    Button btnFinish;
    LottieAnimationView loader,success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_step4);
        getSupportActionBar().hide();

        loader = findViewById(R.id.loader_step4);
        success = findViewById(R.id.succes_lottie);
        btnFinish = findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener(v -> toHomeScreen());
    }

    private void toHomeScreen() {
        btnFinish.setText("Espera...");
        runOnUiThread(()-> {
            try {
                Thread.sleep(2000);
                Intent i = new Intent(FinishAddingDeviceActivity.this, Lista_cercas.class);
                i.putExtra(FROM_FINISH_ADD,true);
                startActivity(i);
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }


}