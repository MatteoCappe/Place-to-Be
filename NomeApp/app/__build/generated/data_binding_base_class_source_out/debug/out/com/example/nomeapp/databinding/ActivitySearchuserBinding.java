// Generated by view binder compiler. Do not edit!
package com.example.nomeapp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.nomeapp.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivitySearchuserBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button SearchUserButton;

  @NonNull
  public final FragmentContainerView SearchUserFragment;

  @NonNull
  public final EditText searchUserName;

  private ActivitySearchuserBinding(@NonNull ConstraintLayout rootView,
      @NonNull Button SearchUserButton, @NonNull FragmentContainerView SearchUserFragment,
      @NonNull EditText searchUserName) {
    this.rootView = rootView;
    this.SearchUserButton = SearchUserButton;
    this.SearchUserFragment = SearchUserFragment;
    this.searchUserName = searchUserName;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivitySearchuserBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivitySearchuserBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_searchuser, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivitySearchuserBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.SearchUserButton;
      Button SearchUserButton = ViewBindings.findChildViewById(rootView, id);
      if (SearchUserButton == null) {
        break missingId;
      }

      id = R.id.SearchUserFragment;
      FragmentContainerView SearchUserFragment = ViewBindings.findChildViewById(rootView, id);
      if (SearchUserFragment == null) {
        break missingId;
      }

      id = R.id.searchUserName;
      EditText searchUserName = ViewBindings.findChildViewById(rootView, id);
      if (searchUserName == null) {
        break missingId;
      }

      return new ActivitySearchuserBinding((ConstraintLayout) rootView, SearchUserButton,
          SearchUserFragment, searchUserName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}