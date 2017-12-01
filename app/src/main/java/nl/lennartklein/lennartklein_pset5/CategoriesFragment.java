package nl.lennartklein.lennartklein_pset5;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

public class CategoriesFragment extends ListFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String URL_categories = "https://resto.mprog.nl/categories";

        // Hide parts of the header
        TextView divider = getActivity().findViewById(R.id.divider);
        TextView heading = getActivity().findViewById(R.id.title_dishes_text);
        heading.setVisibility(View.INVISIBLE);
        divider.setVisibility(View.INVISIBLE);

        // Fetch categories from API and adapt them to a list
        StringRequest request = new StringRequest(URL_categories,
                response -> {
                    JSONObject responseObject;
                    JSONArray responseArray;

                    try {
                        // Get JSON object and array of data
                        responseObject = new JSONObject(response);
                        responseArray = responseObject.getJSONArray("categories");
                        ArrayList<String> categories = new ArrayList<>();

                        // Add every found string to the ArrayList
                        for (int i = 0; i < responseArray.length(); i++) {
                            String category = responseArray.getString(i);
                            categories.add(category);
                        }

                        // Use an adapter and the ArrayList to feed the list
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, categories);
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
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String s = l.getItemAtPosition(position).toString();

        MenuFragment fragment = new MenuFragment();

        Bundle args = new Bundle();
        args.putString("category", s);
        fragment.setArguments(args);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Hide parts of the header
        TextView divider = getActivity().findViewById(R.id.divider);
        TextView heading = getActivity().findViewById(R.id.title_dishes_text);
        heading.setVisibility(View.INVISIBLE);
        divider.setVisibility(View.INVISIBLE);
    }
}
