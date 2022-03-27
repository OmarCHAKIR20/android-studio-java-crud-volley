package com.example.lastmission;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lastmission.beans.Etudiant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AddEtudiant extends Activity implements View.OnClickListener {
    private EditText nom;
    private EditText prenom;
    private Spinner ville;
    private RadioButton m;
    private RadioButton f;
    private Button add , browse;
    private Button listEtudiant;
    RequestQueue requestQueue;
    String encodeImageString;
    ImageView img;
    Bitmap bitmap;
    String insertUrl = "http://192.168.1.37//projetAndroid/controller/createEtudiant.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_etudiant);
        nom = (EditText) findViewById(R.id.nom);
        prenom = (EditText) findViewById(R.id.prenom);
        ville = (Spinner) findViewById(R.id.ville);
        add = (Button) findViewById(R.id.add);
        browse = (Button) findViewById(R.id.browse);
        listEtudiant = (Button) findViewById(R.id.listEtudiant);
        m = (RadioButton) findViewById(R.id.m);
        f = (RadioButton) findViewById(R.id.f);
        img = findViewById(R.id.image);
        add.setOnClickListener(this);
        listEtudiant.setOnClickListener(this);
        browse.setOnClickListener(this);

    }
    @Override
        public void onClick(View v) {
        Log.d("ok", "ok");

            if (v == add) {
                requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest request = new StringRequest(Request.Method.POST,
                        insertUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        img.setImageResource(R.drawable.ic_launcher_foreground);


                        Type type = new TypeToken<Collection<Etudiant>>(){}.getType();
                        Collection<Etudiant> etudiants = new Gson().fromJson(response, type);

                        for(Etudiant e : etudiants){
                            Log.d("ds", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddEtudiant.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        String sexe = "";
                        if (m.isChecked())
                            sexe = "homme";
                        else
                            sexe = "femme";
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("nom", nom.getText().toString());
                        params.put("prenom", prenom.getText().toString());
                        params.put("ville", ville.getSelectedItem().toString());
                        params.put("sexe", sexe);
                        params.put("image",encodeImageString);
                        return params;
                    }
                };
                requestQueue.add(request);
            }

            if (v == browse){
                Dexter.withActivity(AddEtudiant.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response)
                            {

                                Intent intent=new Intent(Intent.ACTION_PICK);
                                intent.setType("image/");

                                startActivityForResult(Intent.createChooser(intent,"Browse Image"),1);
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
            if (v == listEtudiant){
                Intent intent = new Intent(AddEtudiant.this, com.example.lastmission.ListEtudiant.class);
                startActivity(intent);
                finish();
            }


    }
    private void encodeBitmapImage(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] bytesofimage=byteArrayOutputStream.toByteArray();
        encodeImageString= Base64.encodeToString(bytesofimage, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            Uri filePath=data.getData();
            try{
                InputStream inputStream=getContentResolver().openInputStream(filePath);
                bitmap= BitmapFactory.decodeStream(inputStream);
                img.setImageBitmap(bitmap);
                encodeBitmapImage(bitmap);
            }catch (Exception ex){

            }
        }

    }
}

