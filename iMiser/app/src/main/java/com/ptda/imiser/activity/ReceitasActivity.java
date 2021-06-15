package com.ptda.imiser.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ptda.imiser.R;
import com.ptda.imiser.config.FireBaseConfig;
import com.ptda.imiser.helper.Base64Custom;
import com.ptda.imiser.helper.CustomLocationListener;
import com.ptda.imiser.helper.DateUtilCustom;
import com.ptda.imiser.model.Transaction;
import com.ptda.imiser.model.UserModel;

@RequiresApi(api = Build.VERSION_CODES.M)
public class ReceitasActivity extends AppCompatActivity {
    private EditText editTotal;
    private TextInputEditText editCategory;
    private TextInputEditText editDescription;
    private TextInputEditText editDate;
    private Transaction transaction;
    private TextView dateTitle;
    private TextView categoryTitle;
    private TextView descriptionTitle;
    private DatabaseReference firebaseRef = FireBaseConfig.getFirebaseDatabase();
    private FirebaseAuth auth = FireBaseConfig.getFireBaseAuth();
    private double totalReceita;
    private double receita;
    private WebView webview;

    private Switch gpsSwitch;
    private TextView gpsView1, gpsView2, gpsView3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);
        editTotal = findViewById(R.id.editTotalD);
        editCategory = findViewById(R.id.editCategoriaD);
        editDescription = findViewById(R.id.editDescricaoD);
        editDate = findViewById(R.id.editDataD);
        editDate.setText(DateUtilCustom.dateTodayCustom());
        dateTitle = findViewById(R.id.dateTitleD);
        categoryTitle = findViewById(R.id.categoryTitleD);
        descriptionTitle = findViewById(R.id.descriptionTitleD);
        webview = findViewById(R.id.webv);


        gpsSwitch = findViewById(R.id.gpsSwitch);
        gpsView1 = findViewById(R.id.gpsView1);
        gpsView2 = findViewById(R.id.gpsView2);
        gpsView3 = findViewById(R.id.gpsView3);

        gpsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gpsSwitch.isChecked()) {
                    searchLocationGPS(v);

                }
            }
        });

        getDespesaTotal();
    }

    // Go back to the app after searching in google maps
    @Override
    public void onBackPressed() {
        if(webview.canGoBack()) {
            webview.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public void searchLocationGPS(View v) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)   != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ReceitasActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(ReceitasActivity.this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            ActivityCompat.requestPermissions(ReceitasActivity.this, new String[] {Manifest.permission.ACCESS_NETWORK_STATE}, 1);
            return;
        }

        LocationManager mLocManager  = (LocationManager) getSystemService(ReceitasActivity.this.LOCATION_SERVICE);
        LocationListener mLocListener = new CustomLocationListener(ReceitasActivity.this);

        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocListener);

        if (mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsView1.setText(CustomLocationListener.country);
            gpsView2.setText(CustomLocationListener.city);
            gpsView3.setText(CustomLocationListener.address);
            this.showGoogleMaps(CustomLocationListener.latitude, CustomLocationListener.longitude);
        } else {
            gpsView1.setText("Permita o GPS!");
            gpsView2.setText("");
            gpsView3.setText("");
        }
    }

    public void showGoogleMaps(double latitude, double longitude) {
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl("https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }



    public void saveReceita(View view) {
        if(validation()) {
            transaction = new Transaction();
            String chosenDate = editDate.getText().toString();
            double actualValue = Double.parseDouble(editTotal.getText().toString());
            transaction.setValue(actualValue);
            transaction.setCategory(editCategory.getText().toString());
            transaction.setDescription(editDescription.getText().toString());
            transaction.setDate(editDate.getText().toString());
            transaction.setType("deposito");
            transaction.setLatitude(CustomLocationListener.latitude);
            transaction.setLongitude(CustomLocationListener.longitude);
            receita = totalReceita + actualValue;
            updateReceita(receita);
            transaction.save(chosenDate);
            finish();
        }
    }

    public Boolean validation() {
        String textTotal = editTotal.getText().toString();
        String textCategory = editCategory.getText().toString();
        String textDescription = editDescription.getText().toString();
        String textDate = editDate.getText().toString();

        if (!textTotal.isEmpty()) {
            if (!textDate.isEmpty()) {
                if (!textCategory.isEmpty()) {
                    if (!textDescription.isEmpty()) {
                        return true;
                    } else {
                        descriptionTitle.setTextColor(getResources().getColor(R.color.colorError));
                        Toast.makeText(ReceitasActivity.this, "Por favor introduza uma descrição!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    categoryTitle.setTextColor(getResources().getColor(R.color.colorError));
                    Toast.makeText(ReceitasActivity.this, "Por favor introduza uma categoria!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                dateTitle.setTextColor(getResources().getColor(R.color.colorError));
                Toast.makeText(ReceitasActivity.this, "Por favor introduza uma data!", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            editTotal.setHintTextColor(getResources().getColor(R.color.colorAccentReceita));
            Toast.makeText(ReceitasActivity.this, "Por favor introduza um valor!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void getDespesaTotal() {
        String idUser = Base64Custom.encodeBase64Custom(auth.getCurrentUser().getEmail());
        DatabaseReference userRef = firebaseRef.child("usuarioB64")
                .child(idUser);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                totalReceita = user.getDepositoTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateReceita(double receita) {
        String idUser = Base64Custom.encodeBase64Custom(auth.getCurrentUser().getEmail());
        DatabaseReference userRef = firebaseRef.child("usuarioB64")
                .child(idUser);
        userRef.child("depositoTotal").setValue(receita);
    }
}