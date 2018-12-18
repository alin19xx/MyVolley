package com.stucom.planets;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.stucom.planets.model.Planet;
import com.stucom.planets.model.APIResponse;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadPlanets();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        downloadPlanets();
    }

    final static String URL = "https://api.flx.cat/planets/planet";

    public void downloadPlanets() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String json = response.toString();
                        Gson gson = new Gson();
                        Type typeToken = new TypeToken<APIResponse<List<Planet>>>() {}.getType();
                        APIResponse<List<Planet>> apiResponse = gson.fromJson(json, typeToken);
                        List<Planet> planets = apiResponse.getData();
                        PlanetsAdapter adapter = new PlanetsAdapter(planets);
                        recyclerView.setAdapter(adapter);

                        String message = "Downloaded " + planets.size() + " planets\n";
                        for (Planet planet : planets) {
                            message += planet.getName() + ":" + planet.getImage() + "\n";
                        }
                        textView.setText(message);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = error.toString();
                        NetworkResponse response = error.networkResponse;
                        if (response != null) {
                            message = response.statusCode + " " + message;
                        }
                        textView.setText("ERROR " + message);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
        MyVolley.getInstance(this).add(request);
    }

    class PlanetsViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        TextView textViewDwarf;
        ImageView imageView;

        PlanetsViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewDwarf = itemView.findViewById(R.id.textViewDwarf);
        }
    }

    class PlanetsAdapter extends RecyclerView.Adapter<PlanetsViewHolder> {

        private List<Planet> planets;

        PlanetsAdapter(List<Planet> planets) {
            super();
            this.planets = planets;
        }

        @NonNull @Override
        public PlanetsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.list_item, parent, false);
            return new PlanetsViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull PlanetsViewHolder viewHolder, int position) {
            Planet planet = planets.get(position);
            viewHolder.textView.setText(planet.getName());
            String dwarf = planet.isDwarf() ? "Nan" : "Normal";
            viewHolder.textViewDwarf.setText(dwarf);
            Picasso.get().load(planet.getImage()).into(viewHolder.imageView);
        }
        @Override
        public int getItemCount() {
            return planets.size();
        }
    }
}
