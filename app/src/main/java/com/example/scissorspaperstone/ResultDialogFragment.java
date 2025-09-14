package com.example.scissorspaperstone;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ResultDialogFragment extends DialogFragment {
    public interface ResultDialogListener{
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    ResultDialogListener listener;

    public static ResultDialogFragment newInstance(String title){
        ResultDialogFragment fragment = new ResultDialogFragment();
        Bundle args = new Bundle();
        args.putString("dialog_title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);  // Запрещаем возможность закрывать окно нажатием мимо него
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle args = getArguments();
        String title = "";
        if (args != null){
            title = args.getString("dialog_title");
        }
        builder.setMessage(title)
                .setPositiveButton(R.string.restart, (dialog, id) ->
                        listener.onDialogPositiveClick(ResultDialogFragment.this))
                .setNegativeButton(R.string.close_game, (dialog, id) ->
                        listener.onDialogNegativeClick(ResultDialogFragment.this));
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ResultDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement NoticeDialogListener");
        }
    }
}
