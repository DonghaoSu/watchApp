package com.example.jason.watchapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterDeviceActivity extends WearableActivity {

    final String BASE_URL = "http://ec2-54-201-123-10.us-west-2.compute.amazonaws.com:5000/get_id_from_device_key?device_key=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_device);

        final EditText deviceKeyInput = findViewById(R.id.deviceKey);
        Button button = findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deivceKey = deviceKeyInput.getText().toString();
                String callingURL = BASE_URL + deivceKey;
                RequestQueue requestQueue;
                requestQueue = Volley.newRequestQueue(getBaseContext());
                JsonObjectRequest objectRequest = new JsonObjectRequest(
                        callingURL,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("restapirequest", response.toString());
                                String userId = null;
                                try {
                                    userId = response.getString("user_id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Intent intent = new Intent(RegisterDeviceActivity.this, MainActivity.class);
                                intent.putExtra("userId", userId);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("restapirequest", error.toString());
                            }
                        }
                );
                requestQueue.add(objectRequest);
            }
        });
        // Enables Always-on
        setAmbientEnabled();
    }
}
