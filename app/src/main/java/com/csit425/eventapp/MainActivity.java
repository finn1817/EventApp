package com.csit425.eventapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> eventList;
    private TextView emptyText; // Added to show message if no events
    private static final String API_URL = "https://your-real-api.com/events"; // Replace with a real API

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        emptyText = findViewById(R.id.emptyText); // Get reference to TextView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventList = new ArrayList<>();
        adapter = new EventAdapter(eventList);
        recyclerView.setAdapter(adapter);

        fetchEvents();
    }

    private void fetchEvents() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, API_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() == 0) {
                            emptyText.setVisibility(TextView.VISIBLE); // Show "No Events Available"
                            recyclerView.setVisibility(RecyclerView.GONE);
                            return;
                        }

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject eventObj = response.getJSONObject(i);
                                String title = eventObj.getString("title");
                                String date = eventObj.getString("date");
                                eventList.add(new Event(title, date));
                            }
                            adapter.notifyDataSetChanged();
                            emptyText.setVisibility(TextView.GONE);
                            recyclerView.setVisibility(RecyclerView.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "JSON Parsing Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                emptyText.setText("Failed to load events");
                emptyText.setVisibility(TextView.VISIBLE);
                recyclerView.setVisibility(RecyclerView.GONE);
                Toast.makeText(MainActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(request);
    }
}
