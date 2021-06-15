package com.ptda.imiser.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

public class DespesasActivity extends AppCompatActivity {
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
    private double totalDespesa;
    private double despesa;
    private WebView webview;
    private Button gpsButton;
    private TextView gpsView1, gpsView2, gpsView3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        editTotal = findViewById(R.id.editTotalD);
        editCategory = findViewById(R.id.editCategoriaL);
        editDescription = findViewById(R.id.editDescricaoL);
        editDate = findViewById(R.id.editDataL);
        editDate.setText(DateUtilCustom.dateTodayCustom());
        dateTitle = findViewById(R.id.dateTitleD);
        categoryTitle = findViewById(R.id.categoryTitleD);
        descriptionTitle = findViewById(R.id.descriptionTitleD);

        webview = findViewById(R.id.webViewDespesas);
        gpsButton = findViewById(R.id.gpsButtonDespesas);
        gpsView1 = findViewById(R.id.gpsViewDespesas1);
        gpsView2 = findViewById(R.id.gpsViewDespesas2);
        gpsView3 = findViewById(R.id.gpsViewDespesas3);

        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    searchLocationGPS(v);
            }
        });

        getDespesaTotal();
    }

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

            ActivityCompat.requestPermissions(DespesasActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(DespesasActivity.this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            ActivityCompat.requestPermissions(DespesasActivity.this, new String[] {Manifest.permission.ACCESS_NETWORK_STATE}, 1);
            return;
        }

        LocationManager mLocManager  = (LocationManager) getSystemService(DespesasActivity.this.LOCATION_SERVICE);
        LocationListener mLocListener = new CustomLocationListener(DespesasActivity.this);

        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocListener);

        if (mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsView1.bringToFront();
            gpsView2.bringToFront();
            gpsView3.bringToFront();
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

    public void saveDespesa(View view) {
        if(validation()) {
            transaction = new Transaction();
            String chosenDate = editDate.getText().toString();
            double actualValue = Double.parseDouble(editTotal.getText().toString());
            transaction.setValue(actualValue);
            transaction.setCategory(editCategory.getText().toString());
            transaction.setDescription(editDescription.getText().toString());
            transaction.setDate(editDate.getText().toString());
            transaction.setType("levantamento");
            transaction.setLatitude(CustomLocationListener.latitude);
            transaction.setLongitude(CustomLocationListener.longitude);
            despesa = totalDespesa - actualValue;
            updateDespesa( despesa);
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
                        Toast.makeText(DespesasActivity.this, "Por favor introduza uma descrição!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    categoryTitle.setTextColor(getResources().getColor(R.color.colorError));
                    Toast.makeText(DespesasActivity.this, "Por favor introduza uma categoria!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                dateTitle.setTextColor(getResources().getColor(R.color.colorError));
                Toast.makeText(DespesasActivity.this, "Por favor introduza uma data!", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            editTotal.setHintTextColor(getResources().getColor(R.color.colorAccentReceita));
            Toast.makeText(DespesasActivity.this, "Por favor introduza um valor!", Toast.LENGTH_SHORT).show();
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
                totalDespesa = user.getLevantamentoTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateDespesa(double despesa) {
        String idUser = Base64Custom.encodeBase64Custom(auth.getCurrentUser().getEmail());
        DatabaseReference userRef = firebaseRef.child("usuarioB64")
                .child(idUser);
        userRef.child("levantamentoTotal").setValue(despesa);
    }
}