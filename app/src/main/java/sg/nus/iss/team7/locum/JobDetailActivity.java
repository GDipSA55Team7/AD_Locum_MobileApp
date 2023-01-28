package sg.nus.iss.team7.locum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class JobDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        Intent intent = getIntent();
        int itemPos = intent.getIntExtra("itemPos", 0);

        TextView textView = findViewById(R.id.textView);
        textView.setText("job detail " + itemPos);
    }
}