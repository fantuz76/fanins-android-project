package com.fant.fanins;

import com.fant.fanins.MyDatabase.DataINStable;

import android.app.ListActivity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ReadTxtActivity extends ListActivity {

	private MyDatabase DBINSlocal, DBINSdownloaded;
	
	public static String versionName = "";
	
	private SimpleCursorAdapter dataAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_txt);


		try {
			versionName = this.getPackageManager()
            	    .getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			versionName = "Errore versione non rilevata";
		}

		TextView stFrom  = (TextView) findViewById(R.id.appVersion);
		stFrom.setText(versionName);
		
		
		// storing string resources into Array
        //String[] my_string_list = getResources().getStringArray(R.array.Categoria);
         
        // Binding resources Array to ListAdapter
        //this.setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, R.id.textView1, my_string_list));
/*
        ArrayAdapter<CharSequence> adapter1;
		adapter1 = ArrayAdapter.createFromResource(this, R.array.tipo_operazione, android.R.layout.simple_spinner_item);	// Create an ArrayAdapter using the string array and a default spinner layout		
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		// Specify the layout to use when the list of choices appears
*/	
		//spinner.setAdapter(adapter1);	// Apply the adapter to the spinner

      /*
        String[] my_string_list = getResources().getStringArray(R.array.Categoria);
        ListView listView = (ListView)findViewById(android.R.id.list);        
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this, R.layout.list_item, R.id.label, my_string_list);
        listView.setAdapter(arrayAdapter);
     */

		// prepara file
		
		DBINSlocal = new MyDatabase(
				getApplicationContext(), 
				myGlobal.getStorageFantDir().getPath() + java.io.File.separator +  myGlobal.LOCAL_DB_FILENAME);
				
		DBINSdownloaded = new MyDatabase(
				getApplicationContext(), 
				myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_DOWNLOADED_DB_FILE);
		
		
	
		DBINSlocal.open();
		if (DBINSlocal.fetchProducts().getCount() == 0) {
			assert true;	// nop
		} else {
			Cursor mycursor;
			mycursor = DBINSlocal.fetchProducts();
			while ( mycursor.moveToNext() ) {

			    Log.i(myGlobal.TAG, " FANTUZ --> " +
			    		mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.DATA_OPERAZIONE_KEY) ) +
			    		mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY) ) + 
			    		mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.CHI_FA_KEY) ) +
			    		mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.A_DA_KEY) ) +
			    		mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.C_PERS_KEY) ) +
			    		mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.VALORE_KEY) ) +
			    		mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.CATEGORIA_KEY) ) +
			    		mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.GENERICA_KEY) ) +
			    		mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.DESCRIZIONE_KEY) ) + 
			    		mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.NOTE_KEY) )
			    );
			
			    // The desired columns to be bound
			    String[] columns = new String[] {
			    		MyDatabase.DataINStable.DATA_OPERAZIONE_KEY,
			    		MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY,
			    		MyDatabase.DataINStable.CHI_FA_KEY,
			    		MyDatabase.DataINStable.A_DA_KEY,
			    		MyDatabase.DataINStable.C_PERS_KEY,
			    		MyDatabase.DataINStable.VALORE_KEY,
			    		MyDatabase.DataINStable.CATEGORIA_KEY,
			    		MyDatabase.DataINStable.GENERICA_KEY
			    };
			    
			    // the XML defined views which the data will be bound to
			    int[] to = new int[] { 
			      	10,
			      	10,
			      	20,
			      	30,
			      	10,
			      	10,
			      	20,
			      	30,

			    };
			   
			    
			    dataAdapter = new SimpleCursorAdapter(
			    	    this, R.layout.activity_read_txt, 
			    	    mycursor, 
			    	    columns, 
			    	    to,
			    	    0);
			    
			    ListView listView;
			    listView = (ListView) findViewById(android.R.id.list);
			    // Assign adapter to ListView
			    listView.setAdapter(dataAdapter);

			    
		        String[] my_string_list = getResources().getStringArray(R.array.Categoria);
		        listView = (ListView)findViewById(android.R.id.list);        
		        ArrayAdapter<String> arrayAdapter =
		                new ArrayAdapter<String>(this, R.layout.list_item, R.id.label, columns);
		        listView.setAdapter(arrayAdapter);			    
			}   

		}
		DBINSlocal.close();
/*
        String[] my_string_list = getResources().getStringArray(R.array.Categoria);
        ListView listView = (ListView)findViewById(android.R.id.list);        
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this, R.layout.list_item, R.id.label, my_string_list);
        listView.setAdapter(arrayAdapter);
		
		List<Contact> contact = new ArrayList<Contact>(); 
		contact=getAllContacts();     
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, contact); 
		 listContent.setAdapter(adapter);
        
        */
        
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.read_txt, menu);
		return true;
	}

}
