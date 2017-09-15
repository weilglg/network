package com.dragon.finder;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.view.annotation.BindView;
import com.view.annotation.OnClick;
import com.view.api.ViewInjector;


public class MainActivity extends Activity {

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
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
    }

    @OnClick({R.id.tv,R.id.tv2})
    public void test(View view){
        switch (view.getId()) {
            case R.id.tv:
                Toast.makeText(this, "点击第一个按钮", Toast.LENGTH_LONG).show();
                break;
            case R.id.tv2:
                Toast.makeText(this, "点击第二个按钮", Toast.LENGTH_LONG).show();
                break;
        }

    }
}
