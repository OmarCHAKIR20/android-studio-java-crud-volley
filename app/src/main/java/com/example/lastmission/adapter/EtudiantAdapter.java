package com.example.lastmission.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.lastmission.AddEtudiant;
import com.example.lastmission.R;
import com.example.lastmission.beans.Etudiant;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class EtudiantAdapter extends RecyclerView.Adapter<EtudiantAdapter.ViewHolder> {
    //create variable
    private ArrayList<Etudiant>modelArrayList;
    private Context context;
    //generate constructor

    String updateUrl = "http://192.168.1.37//projetAndroid/controller/updateEtudiant.php";
    private static String deleteEtudiant = "http://192.168.1.37//projetAndroid/controller/deleteEtudiant.php";

    public EtudiantAdapter(ArrayList<Etudiant> modelArrayList, Context context) {
        this.modelArrayList = modelArrayList;
        this.context = context;
    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //layout inflater

        View view= LayoutInflater.from ( context ).inflate (R.layout.etudiant_item,parent,false );
        final ViewHolder holder = new ViewHolder(view);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View popup = LayoutInflater.from(context).inflate(R.layout.edit_etudiant_layout , null , false);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(popup);

                final TextView idEdit = popup.findViewById(R.id.idEdit);
                final TextView edit_name = popup.findViewById(R.id.edit_nom);
                final TextView edit_prenom = popup.findViewById(R.id.edit_prenom);
                final ImageView edit_image = popup.findViewById(R.id.edit_image);

                final Button editButton = popup.findViewById(R.id.something);
                final Button deleteButton = popup.findViewById(R.id.delete);
                final Button uploadImage = popup.findViewById(R.id.upload);

                Spinner ville = (Spinner) popup.findViewById(R.id.Edit_ville);
                RadioButton m  = (RadioButton) popup.findViewById(R.id.m);
                RadioButton f  = (RadioButton) popup.findViewById(R.id.f);
                edit_name.setText(((TextView)v.findViewById(R.id.lname)).getText().toString());
                edit_prenom.setText(((TextView)v.findViewById(R.id.fname)).getText().toString());

                Bitmap bitmap = ((BitmapDrawable)((ImageView)view
                        .findViewById(R.id.image)).getDrawable()).getBitmap();
                edit_image.setImageBitmap(bitmap);

              //  edit_image.setImageResource(R.drawable.ic_launcher_foreground);



                idEdit.setText(((TextView)v.findViewById(R.id.idTxt)).getText().toString());

                // Set Title and Message:
                builder.setTitle("Modification").setMessage("");

                builder.setCancelable(true);
                uploadImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dexter.withActivity((Activity) context)
                                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse response)
                                    {

                                        Intent intent=new Intent(Intent.ACTION_PICK);
                                        intent.setType("image/");
                                        Activity origin = (Activity)context;
                                        origin.startActivityForResult(Intent.createChooser(intent,"Browse Image"), 1);

                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse response) {

                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                        token.continuePermissionRequest();
                                    }
                                }).check();
                    }
                });
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, deleteEtudiant , new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                               // Toast.makeText(context, "nice  "+ response, Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                               // Toast.makeText(context, "error "+error, Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Nullable
                            @Override
                            protected HashMap<String, String> getParams() throws AuthFailureError {

                                HashMap<String , String> params = new HashMap<>();
                                params.put("id" ,  idEdit.getText().toString());

                                return  params;
                            }
                        };
                        RequestQueue queue = Volley.newRequestQueue(context);
                        queue.add(stringRequest);
                    }
                });

                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                               // Toast.makeText(context, "nice  "+response, Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Toast.makeText(context, "error "+error, Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Nullable
                            @Override
                            protected HashMap<String, String> getParams() throws AuthFailureError {
                                String sexe = "";
                                if (m.isChecked()) {
                                    sexe = "homme";
                                    m.setChecked(true);
                                } else{
                                    f.setChecked(true);
                                    sexe = "femme";
                                }
                                        HashMap<String , String> params = new HashMap<>();
                                             params.put("id" ,  idEdit.getText().toString());
                                            params.put("nom" ,  edit_name.getText().toString());
                                            params.put("prenom" ,  edit_prenom.getText().toString());
                                              params.put("ville" , ville.getSelectedItem().toString() );
                                                 params.put("sexe" , sexe );
                                                 params.put("upload" , BitMapToString(bitmap));
                                return  params;
                            }
                        };
                        RequestQueue queue = Volley.newRequestQueue(context);
                        queue.add(stringRequest);



                    }
                });
                // Create AlertDialog:
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //get position
        Etudiant model=modelArrayList.get ( position );
        Glide.with(holder.profil.getContext())
                .load("http://192.168.1.37/projet/images/" + model.getImage())
                .into(holder.profil);
        holder.id.setText (model.getId ()+ "" );
        holder.fname.setText ( model.getNom ()  );
        holder.lname.setText (model.getPrenom ()+"\n"   );
        holder.residence.setText (model.getVille() );

        holder.sexe.setText(model.getSexe());



    }

    @Override
    public int getItemCount() {
        return modelArrayList.size ();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView id,fname,lname , residence , sexe ;
        ImageView profil;
        Button delete;


        public ViewHolder(@NonNull View itemView) {
            super ( itemView );
            id=itemView.findViewById ( R.id.idTxt );
            profil = itemView.findViewById(R.id.image);
            fname=itemView.findViewById ( R.id.fname );
            lname=itemView.findViewById ( R.id.lname );
           residence = itemView.findViewById(R.id.residence);
             sexe = itemView.findViewById(R.id.sexe);
           delete = itemView.findViewById(R.id.delete);

        }
    }
}
