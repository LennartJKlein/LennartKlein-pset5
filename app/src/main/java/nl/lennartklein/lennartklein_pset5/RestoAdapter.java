package nl.lennartklein.lennartklein_pset5;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class RestoAdapter extends ResourceCursorAdapter {

    RestoAdapter(Context context, int layoutId, Cursor cursor) {
        super(context, layoutId, cursor, true);
    }

    @Override
    public void bindView(View view, Context context, Cursor cur) {

        // Fetch data from row
        String image_url = cur.getString(cur.getColumnIndex("image_url"));
        String name = cur.getString(cur.getColumnIndex("name"));
        String price = "$ " + cur.getInt(cur.getColumnIndex("price")) + ".00";
        int amount = cur.getInt(cur.getColumnIndex("amount"));

        // Set the data in the views
        ImageView viewImage = view.findViewById(R.id.dish_image);
        new DownloadImage(viewImage).execute(image_url);

        TextView viewName = view.findViewById(R.id.dish_name);
        viewName.setText(name);

        TextView viewPrice = view.findViewById(R.id.dish_price);
        viewPrice.setText(price);

        TextView viewAmount = view.findViewById(R.id.dish_amount);
        viewAmount.setText(String.valueOf(amount));

    }
}
