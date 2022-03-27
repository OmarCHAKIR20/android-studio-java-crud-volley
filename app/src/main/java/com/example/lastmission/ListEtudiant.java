package com.example.lastmission;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.lastmission.adapter.EtudiantAdapter;
import com.example.lastmission.beans.Etudiant;
import com.example.lastmission.adapter.EtudiantAdapter;
import com.example.lastmission.beans.Etudiant;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListEtudiant extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Etudiant> etudiants ;
    private static String base = "http://192.168.1.37//projetAndroid/controller/loadEtudiant.php";

    EtudiantAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_etudiant);
        SwipeRefreshLayout refreshMotion = findViewById(R.id.refresh);
        etudiants = new ArrayList<>();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        extractEtudiant();

        refreshMotion.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                etudiants.clear();
                extractEtudiant();

                    adapter.notifyDataSetChanged();
                refreshMotion.setRefreshing(false);


            }
        });

    }




    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END , ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT ) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            try {
                final int position = viewHolder.getAdapterPosition();
                //final Etudiant item = adapter.removeItem(position);
                Snackbar snackbar = Snackbar.make(viewHolder.itemView, "Item " + (direction == ItemTouchHelper.RIGHT ? "deleted" : "archived") + ".", Snackbar.LENGTH_LONG);
                snackbar.setAction(android.R.string.cancel, new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        try {

                        } catch(Exception e) {
                            Log.e("MainActivity", e.getMessage());
                        }
                    }
                });

                snackbar.show();
            } catch(Exception e) {
                Log.e("MainActivity", e.getMessage());
            }
        }
    };

    private void extractEtudiant(){
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest( Request.Method.POST , base , null , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0 ; i< response.length() ; i++){
                    try {
                        JSONObject etudiantObject = response.getJSONObject(i);
                        Etudiant etudiant = new Etudiant();
                        etudiant.setId(etudiantObject.getInt("id"));
                        etudiant.setNom(etudiantObject.getString("nom").toString());
                        etudiant.setPrenom(etudiantObject.getString("prenom").toString());
                        etudiant.setVille(etudiantObject.getString("ville").toString());
                        etudiant.setSexe(etudiantObject.getString("sexe").toString());
                        etudiant.setImage(etudiantObject.getString("upload").toString());

                        etudiants.add(etudiant);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                recyclerView = findViewById(R.id.rv);
                adapter = new EtudiantAdapter((ArrayList<Etudiant>) etudiants, ListEtudiant.this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(ListEtudiant.this));


            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("hey" ,"on error : "+ error.getMessage());
            }
        });
        queue.add(jsonArrayRequest);
    }


}
