// Generated by view binder compiler. Do not edit!
package com.example.nomeapp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.nomeapp.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityRegisterBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final EditText Name;

  @NonNull
  public final EditText Surname;

  @NonNull
  public final Button registerButton;

  @NonNull
  public final TextView switchToLogin;

  @NonNull
  public final EditText userEmail;

  @NonNull
  public final EditText userName;

  @NonNull
  public final EditText userPassword;

  private ActivityRegisterBinding(@NonNull ConstraintLayout rootView, @NonNull EditText Name,
      @NonNull EditText Surname, @NonNull Button registerButton, @NonNull TextView switchToLogin,
      @NonNull EditText userEmail, @NonNull EditText userName, @NonNull EditText userPassword) {
    this.rootView = rootView;
    this.Name = Name;
    this.Surname = Surname;
    this.registerButton = registerButton;
    this.switchToLogin = switchToLogin;
    this.userEmail = userEmail;
    this.userName = userName;
    this.userPassword = userPassword;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityRegisterBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityRegisterBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_register, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityRegisterBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.Name;
      EditText Name = ViewBindings.findChildViewById(rootView, id);
      if (Name == null) {
        break missingId;
      }

      id = R.id.Surname;
      EditText Surname = ViewBindings.findChildViewById(rootView, id);
      if (Surname == null) {
        break missingId;
      }

      id = R.id.registerButton;
      Button registerButton = ViewBindings.findChildViewById(rootView, id);
      if (registerButton == null) {
        break missingId;
      }

      id = R.id.switchToLogin;
      TextView switchToLogin = ViewBindings.findChildViewById(rootView, id);
      if (switchToLogin == null) {
        break missingId;
      }

      id = R.id.userEmail;
      EditText userEmail = ViewBindings.findChildViewById(rootView, id);
      if (userEmail == null) {
        break missingId;
      }

      id = R.id.userName;
      EditText userName = ViewBindings.findChildViewById(rootView, id);
      if (userName == null) {
        break missingId;
      }

      id = R.id.userPassword;
      EditText userPassword = ViewBindings.findChildViewById(rootView, id);
      if (userPassword == null) {
        break missingId;
      }

      return new ActivityRegisterBinding((ConstraintLayout) rootView, Name, Surname, registerButton,
          switchToLogin, userEmail, userName, userPassword);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}