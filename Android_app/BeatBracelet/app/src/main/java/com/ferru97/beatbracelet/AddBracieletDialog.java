package com.ferru97.beatbracelet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


import androidx.fragment.app.DialogFragment;

import com.ferru97.beatbracelet.data.API;
import com.ferru97.beatbracelet.utils.HTTPRequest;
import com.ferru97.beatbracelet.utils.HTTPResponseHandler;

import java.util.HashMap;
import java.util.Map;

public class AddBracieletDialog extends DialogFragment  {
    private View customView;
    private HTTPResponseHandler resHandler;

    public AddBracieletDialog(View customView, HTTPResponseHandler resHandler){
        this.customView = customView;
        this.resHandler = resHandler;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add A Beat-Bracelet")
                .setView(customView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        EditText bid = customView.findViewById(R.id.idB);
                        EditText psw = customView.findViewById(R.id.pswB);
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("uid", API.client_id);
                        params.put("bid", bid.getText().toString());
                        params.put("psw", psw.getText().toString());
                        HTTPRequest.POST_Request("add_bracelet",getActivity(), API.add_bracelet ,(HashMap<String, String>) params,resHandler);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void showAlert(String msg){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}
