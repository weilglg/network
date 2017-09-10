package com.dragon.finder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.view.annotation.BindView;
import com.view.api.ViewInjector;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv)
    TextView tv1;
    @BindView(R.id.tv2)
    TextView tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewInjector.injectView(this);
        tv1.setText("成功TV1");
        tv2.setText("成功TV2");
    }
}
