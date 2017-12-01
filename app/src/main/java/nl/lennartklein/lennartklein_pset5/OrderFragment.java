package nl.lennartklein.lennartklein_pset5;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class OrderFragment extends DialogFragment {

    String URL_order = "https://resto.mprog.nl/order";
    RestoDatabase db;
    RestoAdapter ra;
    ListView lv;
    TextView tv;
    Button buttonClear;
    Button buttonPlace;
    Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order, container, false);

        lv = v.findViewById(R.id.list_order);
        tv = v.findViewById(R.id.error_order);
        buttonClear = v.findViewById(R.id.order_clear);
        buttonPlace = v.findViewById(R.id.order_place);
        buttonClear.setOnClickListener(new ClearOrder());
        buttonPlace.setOnClickListener(new PlaceOrder());

        return v;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        // Get instance of the database
        db = RestoDatabase.getInstance(getActivity().getApplicationContext());

        // Fill the list with the database contents
        Cursor data = db.selectAll();
        if (data.getCount() > 0) {
            ra = new RestoAdapter(getActivity().getApplicationContext(), R.layout.row_dish, data);
            lv.setAdapter(ra);
            lv.setOnItemLongClickListener(new LongClickListener());
            tv.setVisibility(View.GONE);
        }
        else {
            tv.setVisibility(View.VISIBLE);
        }
    }

    private class ClearOrder implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            db.clear();
            Toast.makeText(mContext, "Order cleared", Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

    private class PlaceOrder implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // Create JSONobject to post
            JSONObject postData = null;
            JSONArray dishes = new JSONArray();

            // Loop through all orders
            Cursor data = db.selectAll();
            for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {

                // Get the data of this dish
                int dish_id = data.getInt(data.getColumnIndex("dish_id"));
                int amount = data.getInt(data.getColumnIndex("amount"));

                for (int i = 0; i < amount; i++) {
                    dishes.put(dish_id);
                }
            }

            try {
                if (dishes.length() > 0) {
                    // Crate a JSONobject of the dishes
                    postData = new JSONObject("{\"menuIds\":" + dishes.toString() + "}");

                    // Post the data
                    JsonObjectRequest request = new JsonObjectRequest(URL_order, postData,
                            response -> {

                                // Get response
                                String preparationTime;
                                try {
                                    preparationTime = response.getString("preparation_time");

                                    if (preparationTime != null) {
                                        // Show dialog with response
                                        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                                        alert.setTitle(R.string.success_order_title);

                                        String message = "Thank you for placing your order. You can pick it up in about ";
                                               message += preparationTime;
                                               message += " minutes.";
                                        alert.setMessage(message);

                                        alert.setPositiveButton("Ok", (dialog, i) -> {});

                                        alert.show();

                                    } else {
                                        Toast.makeText(mContext, R.string.error_network, Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(mContext, R.string.error_network, Toast.LENGTH_SHORT).show();
                                }
                            },
                            error -> Toast.makeText(mContext, R.string.error_network, Toast.LENGTH_SHORT).show()
                    );
                    RequestQueue queue = Volley.newRequestQueue(mContext);
                    queue.add(request);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Clear the order from the database
            db.clear();

            // Close the order dialog
            dismiss();
        }
    }

    private class LongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, final long l) {

            // Set up dialog
            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            alert.setTitle(R.string.delete_dish);

            // Create a delete button
            alert.setPositiveButton(R.string.label_delete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    // Delete this item from the database
                    db.delete(l);

                    // Fill the list with the database contents
                    Cursor data = db.selectAll();
                    if (data.getCount() > 0) {
                        ra.swapCursor(db.selectAll());

                        // Hide placeholder text
                        tv.setVisibility(View.GONE);
                    }
                    else {
                        // Show placeholder text
                        tv.setVisibility(View.VISIBLE);
                    }
                }
            });

            // Create a cancel button
            alert.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            alert.show();
            return true;
        }
    }


}
