package com.fant.fanins;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
 
public class DbAdapter {
  @SuppressWarnings("unused")
  private static final String LOG_TAG = DbAdapter.class.getSimpleName();
         
  private Context context;
  private SQLiteDatabase database;
  private DatabaseHelper dbHelper;
 
  // Database fields
  private static final String DATABASE_TABLE      = "myINSData";
 
  
  
 	public static final String KEY_DATA = "DataOperazione";
 	public static final String KEY_TIPO = "TipoOperazione";
 	public static final String KEY_CHIFA = "ChiFa";
 	public static final String KEY_ADA = "ADa";
 	public static final String KEY_CPERS = "CPers";
 	public static final String KEY_VALORE = "Valore";
 	public static final String KEY_CATEGORIA = "Categoria";
 	public static final String KEY_GENERICA = "Generica";
 	public static final String KEY_DESCRIZIONE = "Descrizione";
 	public static final String KEY_NOTE = "Note";
 	
 	
  public DbAdapter(Context context) {
    this.context = context;
  }
 
  public DbAdapter open() throws SQLException {
    dbHelper = new DatabaseHelper(context);
    database = dbHelper.getWritableDatabase();
    return this;
  }
 
  public void close() {
    dbHelper.close();
  }
 
  private ContentValues createContentValues(String name, String surname, String sex, String birth_date ) {
    ContentValues values = new ContentValues();
    values.put( KEY_DATA, name );
    values.put( KEY_TIPO, surname );
    values.put( KEY_CHIFA, sex );
    values.put( KEY_CPERS, birth_date );
     
   return values;
  }
         
  //create a contact
  public long createContact(String name, String surname, String sex, String birth_date ) {
    ContentValues initialValues = createContentValues(name, surname, sex, birth_date);
    return database.insertOrThrow(DATABASE_TABLE, null, initialValues);
  }
 
  //update a contact
  public boolean updateContact( long contactID, String name, String surname, String sex, String birth_date ) {
    ContentValues updateValues = createContentValues(name, surname, sex, birth_date);
    return database.update(DATABASE_TABLE, updateValues, KEY_CHIFA + "=" + contactID, null) > 0;
  }
                 
  //delete a contact      
  public boolean deleteContact(long contactID) {
    return database.delete(DATABASE_TABLE, KEY_DATA + "=" + contactID, null) > 0;
  }
 
  //fetch all contacts
  public Cursor fetchAllContacts() {
    //return database.query(DATABASE_TABLE, new String[] { KEY_DATA, KEY_TIPO, KEY_CHIFA,KEY_ADA,KEY_CPERS,KEY_VALORE,KEY_CATEGORIA,KEY_GENERICA,KEY_DESCRIZIONE,KEY_NOTE}, null, null, null, null, null);
	  return database.query(DATABASE_TABLE, new String[] { KEY_DATA, KEY_TIPO, KEY_CHIFA}, null, null, null, null, null);
  }
   
  //fetch contacts filter by a string
  public Cursor fetchContactsByFilter(String filter) {
    Cursor mCursor = database.query(true, DATABASE_TABLE, new String[] {
    		KEY_DATA, KEY_TIPO, KEY_CHIFA,KEY_ADA,KEY_CPERS,KEY_VALORE,KEY_CATEGORIA,KEY_GENERICA,KEY_DESCRIZIONE,KEY_NOTE },
    		KEY_CHIFA + " like '%"+ filter + "%'", null, null, null, null, null);
         
    return mCursor;
  }
  
}