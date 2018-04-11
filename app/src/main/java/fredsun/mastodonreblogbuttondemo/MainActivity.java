
package fredsun.mastodonreblogbuttondemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RotateButtonView.SparkEventListener {
    private RotateButtonView rotateButtonView;
    private Button btn_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rotateButtonView = (RotateButtonView) findViewById(R.id.rotateButtonView);
        rotateButtonView.setEventListener(this);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                rotateButtonView.startMove();
                break;
        }
    }

    @Override
    public void onFingerUp(boolean flag) {
        Log.i("flag", "flag="+flag);
    }
}
