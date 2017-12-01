package nl.lennartklein.lennartklein_pset5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RestoDatabase extends SQLiteOpenHelper {

    // Singleton holder
    private static RestoDatabase instance;

    // Names
    private static int DB_VERSION = 1;
    private static String NAME_DB = "order";
    private static String NAME_TABLE_ORDER = "my_order";
    private static String NAME_ID = "_id";
    private static String NAME_DISH = "name";
    private static String NAME_DISH_ID = "dish_id";
    private static String NAME_PRICE = "price";
    private static String NAME_IMAGE = "image_url";
    private static String NAME_AMOUNT = "amount";

    // SQL statements
    private static String CREATE_TABLE_ORDER = "CREATE TABLE " + NAME_TABLE_ORDER
            + " (" + NAME_ID + " INTEGER PRIMARY KEY, "
            + NAME_DISH_ID + " INTEGER, "
            + NAME_DISH + " TEXT, "
            + NAME_PRICE + " INTEGER, "
            + NAME_IMAGE + " TEXT, "
            + NAME_AMOUNT + " INTEGER"
            + ")";

    private Context mContext;

    // Constructor
    private RestoDatabase(Context context) {
        super(context, NAME_DB, null, DB_VERSION);
        mContext = context;
    }

    // Get instance of singleton
    static RestoDatabase getInstance(Context context) {
        if(instance == null) {
            instance = new RestoDatabase(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ORDER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NAME_TABLE_ORDER);
        onCreate(db);
    }

    Cursor selectAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + NAME_TABLE_ORDER, null);
    }

    void addItem(int dish_id, String name, int price, String image) {
        // Get amount in database
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM "+NAME_TABLE_ORDER+" WHERE dish_id = '"+dish_id+"'", null);

        // If exists: update item amount
        if (c.getCount() > 0) {
            c.moveToFirst();
            int amount = c.getInt(c.getColumnIndex("amount"));
            amount += 1;
            ContentValues val = new ContentValues();
            val.put(NAME_AMOUNT, amount);
            db.update(NAME_TABLE_ORDER, val, "dish_id = " + dish_id, null);
        }
        else {
            // Add item
            ContentValues val = new ContentValues();
            val.put(NAME_DISH_ID, dish_id);
            val.put(NAME_DISH, name);
            val.put(NAME_PRICE, price);
            val.put(NAME_IMAGE, image);
            val.put(NAME_AMOUNT, 1);

            db.insert(NAME_TABLE_ORDER, null, val);
        }
    }

    void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + NAME_TABLE_ORDER);
    }

    void delete(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NAME_TABLE_ORDER, "_id = " + id,  null);
    }
}
