package com.example.xiao.fcapplication;

import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.xiao.fcapplication.FunctionCtl.FunctionControl;

public class MainActivity extends AppCompatActivity {
    Button startconnect;
    Button startVR;
    FunctionControl m_FcCtl = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_FcCtl = FunctionControl.getInstance(getApplicationContext());
        startconnect = (Button)findViewById(R.id.button);
        startVR = (Button)findViewById(R.id.button2);
        startconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_FcCtl.onStart();
            }
        });
        startVR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    m_FcCtl.startVR();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
