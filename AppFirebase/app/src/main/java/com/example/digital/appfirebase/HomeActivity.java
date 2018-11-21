package com.example.digital.appfirebase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "Home Activity";
    private FirebaseAuth firebaseAuth;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageUser;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TextView nometext = findViewById(R.id.id_textview);
        firebaseAuth = FirebaseAuth.getInstance();

        nometext.setText(firebaseAuth.getCurrentUser().getDisplayName());

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("usuario/"+firebaseAuth.getUid());


        final TextView textohome = findViewById(R.id.id_text_firebase);




        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                Log.d(TAG, "Value is: " + usuario);

                textohome.setText("Peso"+usuario.getPeso()+"\nAltura"+usuario.getAltura());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        Button botaosalvar = findViewById(R.id.bt_salvar);
        botaosalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarUsuario();
            }
        });

        imageUser = findViewById(R.id.image_user_id);

        Button buttonsalvar = findViewById(R.id.bt_salvar);
        botaosalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        storageRef.child(firebaseAuth.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageUser);

                            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


    }
    private void salvarUsuario(){
        EditText pesoedit = findViewById(R.id.id_peso);
        EditText alturaedit = findViewById(R.id.id_altura);

        Usuario usuario = new Usuario();

        usuario.setPeso(Double.parseDouble(pesoedit.getText().toString()));
        usuario.setAltura(Double.parseDouble(alturaedit.getText().toString()));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("usuario/"+firebaseAuth.getUid());

        myRef.setValue(usuario);

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageUser.setImageBitmap(imageBitmap);

            // Get the data from an ImageView as bytes
            imageUser.setDrawingCacheEnabled(true);
            imageUser.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imageUser.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] databyte = baos.toByteArray();

            UploadTask uploadTask = storageRef.child(firebaseAuth.getUid()).putBytes(databyte);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });
        }
    }



}
