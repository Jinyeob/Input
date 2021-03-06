package com.jinyeob.carinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView numTextView;
    private TextView teamTextView;
    private TextView nameTextView;
    private TextView dateTextView;
    private TextView tempTextView;

    private String NUMBER = "";
    private String TEAM = "";
    private String NAME = "";
    private String URL = "http://192.168.35.239:3000";

    private String today = "";

    private String sendingString = "";
    private Double TEMP = 36.5;

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numTextView = findViewById(R.id.textview_num);
        nameTextView = findViewById(R.id.textview_name);
        teamTextView = findViewById(R.id.textview_team);
        dateTextView = findViewById(R.id.textview_date);

        tempTextView=findViewById(R.id.editTextTemp);

        btn = findViewById(R.id.button);

        Intent loginIntent = getIntent();
        NUMBER = loginIntent.getStringExtra("num");
        TEAM = loginIntent.getStringExtra("team");
        NAME = loginIntent.getStringExtra("name");
        URL = loginIntent.getStringExtra("url");

        setText_();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendingString = today + "/" + "일산교회/" + NUMBER + "/" + TEAM + "/" + NAME + "/";
                String tempString=tempTextView.getText().toString();
                if(tempString.equals("")) {
                    Toast.makeText(MainActivity.this, "온도를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    TEMP = Double.parseDouble(tempString);
                    if (TEMP < 35 || TEMP > 39) {
                        Toast.makeText(MainActivity.this, "온도를 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        sendingString += TEMP + "\r\n";
                        request();
                        sendingString = today + "/" + "일산교회/" + NUMBER + "/" + TEAM + "/" + NAME + "/";
                        tempTextView.setText("");
                    }
                }
            }
        });
    }

    public void request() {
        JSONObject testjson = new JSONObject();
        try {
            testjson.put("id", sendingString);
            //testjson.put("password", NAME);

            String jsonString = testjson.toString();

            final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, testjson,

                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());

                                String resultId = jsonObject.getString("approve_id");

                                //리스폰스검사
                                if (resultId.equals("ok")) {

                                } else {
                                    //로그인에 실패했을 경우
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                   // Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "옵션 - Setting - URL 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            }
            );
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void setText_() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat weekdayFormat = new SimpleDateFormat("YYYY_MM_dd_HH:mm", Locale.getDefault());
        today = weekdayFormat.format(currentTime);

        numTextView.setText(NUMBER);
        teamTextView.setText(TEAM);
        nameTextView.setText(NAME);

        dateTextView.setText(today);
    }

    @Override
    protected void onResume() {
        NUMBER = SaveSharedPreference.getUserNum(this);
        TEAM = SaveSharedPreference.getUserTeam(this);
        NAME = SaveSharedPreference.getUserName(this);
        URL = SaveSharedPreference.getUrl(this);

        setText_();

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
                /*
            case R.id.menu2:
                break;
            case R.id.menu3:
                break;
                */
        }

        return super.onOptionsItemSelected(item);
    }
}