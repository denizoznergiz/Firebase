package com.example.lenovo.firebaseuygulama.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lenovo.firebaseuygulama.MainActivity;
import com.example.lenovo.firebaseuygulama.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.exception.ConversionException;

import java.util.List;

import static com.example.lenovo.firebaseuygulama.R.*;

public class GirisYap extends AppCompatActivity implements Validator.ValidationListener {

    EditText etEposta;
    EditText etSifre;

    @NotEmpty(messageResId = R.string.bu_alan_zorunlu)
    @Email(messageResId = R.string.gecersiz_eposta)
    TextInputLayout etEpostaLayout;

    @NotEmpty(messageResId = R.string.bu_alan_zorunlu)
    @Password(min = 6, messageResId = R.string.min_6_chars)

    Validator validator;
    boolean isValed;
    FirebaseAuth auth;

    TextInputLayout etSifreLayout;

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            validator.validate();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_giris_yap);

        validator = new Validator(this);
        validator.setValidationListener(this);

        etEposta = findViewById(id.et_eposta);
        etSifre = findViewById(id.et_sifre);

        etEposta.addTextChangedListener(watcher);
        etSifre.addTextChangedListener(watcher);

        etEpostaLayout = findViewById(id.et_eposta_layout);
        etSifreLayout = findViewById(id.et_sifre_layout);
        validator.registerAdapter(TextInputLayout.class, new ViewDataAdapter<TextInputLayout, String>() {
            @Override
            public String getData(TextInputLayout view) throws ConversionException {
                return view.getEditText().getText().toString();
            }
        });

        validator.setViewValidatedAction(new Validator.ViewValidatedAction() {
            @Override
            public void onAllRulesPassed(View view) {
                if (view instanceof TextInputLayout) {
                    ((TextInputLayout) view).setError("");
                }
                ;
            }
        });

    }


    public void GirisYap(View view) {
        String eposta = etEposta.getText().toString().trim();
        String sifre = etSifre.getText().toString().trim();
        if (isValed) {
            auth = FirebaseAuth.getInstance();

            auth.signInWithEmailAndPassword(eposta, sifre).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ToastMesajGoster(getString(string.hata)+ e.getMessage());

                }
            });
        } else {
            validator.validate();
        }

    }

    @Override
    public void onValidationSucceeded() {
        isValed = true;

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

        isValed = false;

        for (ValidationError error : errors) {
            View view = error.getView();
            String mesaj = error.getCollatedErrorMessage(this);

            if (view instanceof TextInputLayout) {
                ((TextInputLayout) view).setError(mesaj);
            }
        }

    }

    public void Kaydol(View view) {

        String eposta = etEposta.getText().toString().trim();
        String sifre = etSifre.getText().toString().trim();
        if (isValed == true) {

            auth = FirebaseAuth.getInstance();

            auth.createUserWithEmailAndPassword(eposta, sifre).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        ToastMesajGoster(getString(string.kayitbasarili));
                        Temizle();

                    }
                }


            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ToastMesajGoster(getString(string.hata) + e.getMessage());
                }
            });
        } else {
            //hata mesajı veya validator.validate();
            validator.validate();
        }
    }

    private void Temizle() {
        etEposta.setText("");
        etSifre.setText("");
        etEposta.requestFocus();
        etSifreLayout.setError("");
    }

    private void ToastMesajGoster(String mesaj) {
        Toast.makeText(getApplicationContext(), mesaj, Toast.LENGTH_LONG).show();
    }
}
