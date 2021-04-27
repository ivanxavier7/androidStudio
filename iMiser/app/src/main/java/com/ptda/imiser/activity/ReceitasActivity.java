package com.ptda.imiser.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import com.ptda.imiser.helper.DateUtilCustom;
import com.ptda.imiser.model.Transaction;
import com.ptda.imiser.model.UserModel;

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

        getDespesaTotal();
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