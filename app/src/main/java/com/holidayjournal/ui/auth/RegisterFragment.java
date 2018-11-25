package com.holidayjournal.ui.auth;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.holidayjournal.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RegisterFragment extends Fragment implements View.OnClickListener, RegisterFragmentView {

    @BindView(R.id.register_email)
    EditText mEmail;

    @BindView(R.id.register_pw)
    EditText mPass;

    @BindView(R.id.register_btn)
    Button mRegister;

    private Unbinder unbinder;
    private RegisterFragmentPresenter mPresenter;
    private RegisterListener mCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter = new RegisterFragmentPresenter(this);

        mRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_btn:
                mPresenter.validateCredentials(mEmail.getText().toString().trim(), mPass.getText().toString().trim());
                break;
        }
    }

    @Override
    public void onValidateSuccess(String email, String password) {
        mCallback.registerUser(email, password);
    }

    @Override
    public void onError(String message) {
        mCallback.registerError(message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (RegisterListener) context;
    }

    public interface RegisterListener {

        void registerUser(String email, String password);

        void registerError(String message);
    }
}
