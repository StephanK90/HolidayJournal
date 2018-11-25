package com.holidayjournal.ui.auth.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.holidayjournal.R;
import com.holidayjournal.ui.base.BaseActivity;
import com.holidayjournal.ui.holidays.HolidayActivity;

import butterknife.BindView;

public class RegisterActivity extends BaseActivity implements RegisterView, View.OnClickListener {

    @BindView(R.id.register_email)
    EditText mEmail;

    @BindView(R.id.register_pw)
    EditText mPass;

    @BindView(R.id.register_btn)
    Button mRegister;

    private FirebaseAuth mAuth;
    private RegisterPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        mPresenter = new RegisterPresenter(this);

        mRegister.setOnClickListener(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_register;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_btn:
                mPresenter.validate(mEmail.getText().toString().trim(), mPass.getText().toString().trim());
                break;
        }
    }

    @Override
    public void onValidateSuccess(String email, String password) {
        registerNewUser(email, password);
    }

    @Override
    public void onValidateError(String message) {
        showToast(message);
    }

    private void registerNewUser(String email, String pw) {
        mAuth.createUserWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            registerSuccessful();

                        } else {
                            showToast("Failed to startRegister.");
                        }
                    }
                });
    }

    private void registerSuccessful() {
        showToast("Register successful!");
        Intent intent = new Intent(this, HolidayActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
