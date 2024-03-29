package com.ptda.imiser.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.ptda.imiser.R;
import com.ptda.imiser.adapter.AdapterTransaction;
import com.ptda.imiser.config.FireBaseConfig;
import com.ptda.imiser.helper.Base64Custom;
import com.ptda.imiser.model.Transaction;
import com.ptda.imiser.model.UserModel;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainAppActivity extends AppCompatActivity {
    private TextView textUserName;
    private TextView textBalance;
    private MaterialCalendarView calendarView;
    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBottom;
    private Animation toBottom;
    private FloatingActionButton receitasBtn;
    private FloatingActionButton despesasBtn;
    private FloatingActionButton menuBtn;
    private TextView despesaView;
    private TextView receitaView;
    private TextView textCity;
    private TextView textCountry;
    private Boolean clicked = false;
    private FirebaseAuth auth = FireBaseConfig.getFireBaseAuth();
    private DatabaseReference firebaseRef = FireBaseConfig.getFirebaseDatabase();
    private DatabaseReference userRef;
    private DatabaseReference transactionRef;
    private double totalDeposito = 0.0;
    private double totalLevantamento = 0.0;
    private double totalUser = 0.0;
    private ValueEventListener valueEventListenerUser;
    private ValueEventListener valueEventListenerTransactions;
    private RecyclerView recyclerTransaction;
    private AdapterTransaction adapterRecycler;
    private List<Transaction> transactionList = new ArrayList<>();
    private Transaction transaction;
    private String monthYear;
    private TextView levantamentoText;
    private TextView depositoText;
    private double latitude = 0.00;
    private double longitude = 0.00;
    private WebView webViewMenu;
    private static Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("iMiser");
        setSupportActionBar(toolbar);
        textUserName = findViewById(R.id.textUserName);
        textBalance = findViewById(R.id.textBalance);
        calendarView = findViewById(R.id.calendarView);
        menuBtn = findViewById(R.id.menuBtn);
        receitasBtn = findViewById(R.id.receitasBtn);
        despesasBtn = findViewById(R.id.despesasBtn);
        despesaView = findViewById(R.id.levantamentoTextViewMenu);
        receitaView = findViewById(R.id.depositoTextViewMenu);
        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);
        levantamentoText = findViewById(R.id.levantamentoTextViewMenu);
        depositoText = findViewById(R.id.depositoTextViewMenu);
        textCity = findViewById(R.id.textCity);
        textCountry = findViewById(R.id.textCountry);
        levantamentoText.bringToFront();
        depositoText.bringToFront();
        recyclerTransaction = findViewById(R.id.recyclerTransaction);
        webViewMenu = findViewById(R.id.webViewMenu);
        configureCalendarView();
        swipe();
        adapterRecycler = new AdapterTransaction(transactionList, getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerTransaction.setLayoutManager(layoutManager);
        recyclerTransaction.setHasFixedSize(true);
        recyclerTransaction.setAdapter(adapterRecycler);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMenuButtonClicked();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        getReport();
        getTransactions();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userRef.removeEventListener(valueEventListenerUser);
        transactionRef.removeEventListener(valueEventListenerTransactions);
    }

    public void onMenuButtonClicked() {
        setVisibility(clicked);
        setAnimation(clicked);
        if(!clicked) {
            clicked = true;
        } else {
            clicked = false;
        }
    }

    public void setVisibility(Boolean clicked) {
        if(!clicked) {
            receitasBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startReceitas();
                }
            });
            despesasBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startDespesas();
                }
            });
            receitasBtn.setVisibility(View.VISIBLE);
            despesasBtn.setVisibility(View.VISIBLE);
            receitaView.setVisibility(View.VISIBLE);
            despesaView.setVisibility(View.VISIBLE);
        } else {
            receitasBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
            despesasBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
            receitasBtn.setVisibility(View.INVISIBLE);
            despesasBtn.setVisibility(View.INVISIBLE);
            receitaView.setVisibility(View.INVISIBLE);
            despesaView.setVisibility(View.INVISIBLE);
        }
    }

    public void setAnimation(Boolean clicked) {
        if(!clicked) {
            receitasBtn.startAnimation(fromBottom);
            despesasBtn.startAnimation(fromBottom);
            receitaView.startAnimation(fromBottom);
            despesaView.startAnimation(fromBottom);
            menuBtn.startAnimation(rotateOpen);
        } else {
            receitasBtn.startAnimation(toBottom);
            despesasBtn.startAnimation(toBottom);
            receitaView.startAnimation(toBottom);
            despesaView.startAnimation(toBottom);
            menuBtn.startAnimation(rotateClose);
        }
    }

    public void startReceitas() {
        startActivity(new Intent(this, ReceitasActivity.class));
    }

    public void startDespesas() {
        startActivity(new Intent(this, DespesasActivity.class));
    }

    public void configureCalendarView() {
        CharSequence months[] = {
                                "Janeiro",
                                "Fevereiro",
                                "Março",
                                "Abril",
                                "Maio",
                                "Junho",
                                "Julho",
                                "Agosto",
                                "Setembro",
                                "Outubro",
                                "Novembro",
                                "Dezembro"};
        calendarView.setTitleMonths(months);
        CalendarDay actualDate = calendarView.getCurrentDate();
        String monthSelected = String.format("%02d", actualDate.getMonth() + 1);
        monthYear = String.valueOf(monthSelected + "" + actualDate.getYear());
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String monthSelected = String.format("%02d", date.getMonth() + 1);
                monthYear = String.valueOf(monthSelected + "" + date.getYear());
                transactionRef.removeEventListener(valueEventListenerTransactions);
                getTransactions();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuLogout:
                auth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getTransactions() {
        String idUser = Base64Custom.encodeBase64Custom(auth.getCurrentUser().getEmail());
        transactionRef = firebaseRef
                .child("movimento")
                .child(idUser)
                .child(monthYear);

        valueEventListenerTransactions = transactionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactionList.clear();
                for (DataSnapshot dados: dataSnapshot.getChildren() ){
                    Transaction transaction = dados.getValue(Transaction.class);
                    transaction.setKey(dados.getKey()); // Get Automatic fireBase ID for generated objects
                    transactionList.add(transaction);
                }
                adapterRecycler.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void getReport() {
        String idUser = Base64Custom.encodeBase64Custom(auth.getCurrentUser().getEmail());
        userRef = firebaseRef.child("usuarioB64")
                            .child(idUser);
        valueEventListenerUser = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                totalLevantamento = user.getLevantamentoTotal();
                totalDeposito = user.getDepositoTotal();
                totalUser = totalLevantamento + totalDeposito;
                textUserName.setText(user.getName());
                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                textBalance.setText(decimalFormat.format(totalUser) + " €");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void swipe() {
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                // On click give the map location
                locationTransaction(viewHolder);
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                removeTransaction(viewHolder);
            }
        };
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerTransaction);
    }

    public void removeTransaction(RecyclerView.ViewHolder viewHolder) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Excluir Transação");
        alertDialog.setMessage("Têm certeza que deseja remover esta transação?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get item from viewHolder
                int position = viewHolder.getAdapterPosition();
                transaction = transactionList.get(position);
                String idUser = Base64Custom.encodeBase64Custom(auth.getCurrentUser().getEmail());
                transactionRef = firebaseRef
                        .child("movimento")
                        .child(idUser)
                        .child(monthYear);
                transactionRef.child(transaction.getKey()).removeValue();   // Remove object
                adapterRecycler.notifyItemRemoved(position);
                updateValue();
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainAppActivity.this,
                        "Cancelado",
                        Toast.LENGTH_SHORT).show();
                adapterRecycler.notifyDataSetChanged();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public void locationTransaction(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        transaction = transactionList.get(position);
        String idUser = Base64Custom.encodeBase64Custom(auth.getCurrentUser().getEmail());

        firebaseRef
                .child("movimento")
                .child(idUser)
                .child(monthYear)
                .child(transaction.getKey())
                .child("latitude")
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful() && task.getResult().getValue() != null) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    latitude = (double) task.getResult().getValue();
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });

        firebaseRef
                .child("movimento")
                .child(idUser)
                .child(monthYear)
                .child(transaction.getKey())
                .child("longitude")
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful() && task.getResult().getValue() != null) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    longitude = (double) task.getResult().getValue();
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });

        showGoogleMaps(latitude, longitude);

    }

    public void showGoogleMaps(double latitude, double longitude) {
        webViewMenu.setWebViewClient(new WebViewClient());
        webViewMenu.loadUrl("https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude);
        WebSettings webSettings = webViewMenu.getSettings();
        webSettings.setJavaScriptEnabled(true);

        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());


        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                textCountry.setText("País: " + addresses.get(0).getCountryName());
                textCity.setText("Cidade: " + addresses.get(0).getLocality());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateValue(){
        String idUser = Base64Custom.encodeBase64Custom(auth.getCurrentUser().getEmail());
        userRef = firebaseRef.child("usuarioB64")
                .child(idUser);
        Log.i("TESTE", transaction.getType());
        if (transaction.getType().equals("deposito")) {
            totalDeposito = totalDeposito - transaction.getValue();
            userRef.child("depositoTotal").setValue(totalDeposito);
        } else {
            totalLevantamento = totalLevantamento + transaction.getValue();
            userRef.child(("levantamentoTotal")).setValue(totalLevantamento);
        }
    }
}