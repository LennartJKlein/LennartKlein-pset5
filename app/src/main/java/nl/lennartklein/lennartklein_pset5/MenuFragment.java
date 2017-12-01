package nl.lennartklein.lennartklein_pset5;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MenuFragment extends ListFragment {

    RestoDatabase db;
    String URL_menu = "https://resto.mprog.nl/menu";
    Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set global variable
        mContext = getActivity().getApplicationContext();

        // Get chosen category from bundle
        Bundle arguments = this.getArguments();
        String category = arguments.getString("category");

        // Set this category in header
        TextView previous_title = getActivity().findViewById(R.id.previous_title);
        TextView divider = getActivity().findViewById(R.id.divider);
        TextView heading = getActivity().findViewById(R.id.title_dishes_text);

        String niceCategory = Character.toUpperCase(category.charAt(0)) + category.substring(1);
        heading.setText(niceCategory);
        heading.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);

        // Create back button from title
        previous_title.setOnClickListener(new CloseFragment());

        // Fetch categories from API and adapt them to a list
        StringRequest request = new StringRequest(URL_menu,
                response -> {
                    JSONObject responseObject;
                    JSONArray responseArray;

                    try {
                        // Get JSON object and array of data
                        responseObject = new JSONObject(response);
                        responseArray = responseObject.getJSONArray("items");
                        ArrayList<String> menu = new ArrayList<>();

                        // Add every found string (of this category) to the ArrayList
                        for (int i = 0; i < responseArray.length(); i++) {
                            JSONObject dish = responseArray.getJSONObject(i);
                            String dish_category = dish.getString("category");

                            // If this dish has the right category
                            if (Objects.equals(dish_category, category)) {
                                String dish_name = dish.getString("name");
                                menu.add(dish_name);
                            }
                        }

                        // Use an adapter and the ArrayList to feed the list
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, menu);
                        this.setListAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getActivity(), R.string.error_network, Toast.LENGTH_SHORT).show());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // Get the database instance
        db = RestoDatabase.getInstance(getActivity().getApplicationContext());
        String name = (String) l.getItemAtPosition(position);

        // Fetch categories from API and adapt them to the database
        StringRequest request = new StringRequest(URL_menu,
                response -> {
                    JSONObject responseObject;
                    JSONArray responseArray;

                    try {
                        responseObject = new JSONObject(response);
                        responseArray = responseObject.getJSONArray("items");

                        // Add every found string (of this category) to the ArrayList
                        for (int j = 0; j < responseArray.length(); j++) {
                            JSONObject dish = responseArray.getJSONObject(j);
                            String dish_name = dish.getString("name");
                            if (Objects.equals(dish_name, name)) {
                                int dish_id = dish.getInt("id");
                                String image_url = dish.getString("image_url");
                                int price = dish.getInt("price");
                                db.addItem(dish_id, name, price, image_url);
                            }
                        }
                        Toast.makeText(mContext, "Dish added to your order", Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getActivity(), R.string.error_network, Toast.LENGTH_SHORT).show());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

    // Click listener for extra back button
    private class CloseFragment implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            getFragmentManager().popBackStack();
        }
    }
}
