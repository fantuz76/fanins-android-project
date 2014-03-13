package com.fant.fanins;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class ReadTxtActivity extends ListActivity {

	private MyDatabase DBINStoread;
	
	public static String versionName = "";
	
	private SimpleCursorAdapter dataAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_txt);

		
		try {
			// recupero info extra e decido qual DB usare
			Bundle bun = getIntent().getExtras();
			String readDBtype = bun.getString("readDBtype");
			

			if (readDBtype.equals("full")) {
				DBINStoread = new MyDatabase(
						getApplicationContext(), 
						myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_FULL_DB_FILE);
			} else {
				DBINStoread = new MyDatabase(
						getApplicationContext(), 
						myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator +  myGlobal.LOCAL_DB_FILENAME);
			}


			
			DBINStoread.open();
			Cursor mycursor = null;
			//mycursor = DBINStoread.fetchDati();
			mycursor = DBINStoread.rawQuery("SELECT * FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " ORDER BY " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY +" DESC",  null );
			if (mycursor.getCount() == 0) {
				assert true;	// nop
			} else {
				super.onCreate(savedInstanceState);
			
				dataAdapter = new SimpleCursorAdapter(
			    	    this, R.layout.list_item, 
			    	    mycursor, 
			    	    new String[] 
			    	    		{ MyDatabase.DataINStable.DATA_OPERAZIONE_KEY, 
			    	    		MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY, 
			    	    		MyDatabase.DataINStable.CHI_FA_KEY, 
			    	    		MyDatabase.DataINStable.A_DA_KEY, 
			    	    		MyDatabase.DataINStable.C_PERS_KEY, 
			    	    		MyDatabase.DataINStable.VALORE_KEY, 
			    	    		MyDatabase.DataINStable.CATEGORIA_KEY,  
			    	    		MyDatabase.DataINStable.DESCRIZIONE_KEY, 
			    	    		MyDatabase.DataINStable.NOTE_KEY}, 
			    	    new int[]
			    	    		{ R.id.dataText, 
			    	    		R.id.tipooperazioneText, 
			    	    		R.id.chifaText, 
			    	    		R.id.adaText, 
			    	    		R.id.cpersText, 
			    	    		R.id.valoreText, 
			    	    		R.id.categoriaText, 
			    	    		R.id.descrizioneText, 
			    	    		R.id.noteText},
			    	    0);

				setListAdapter(dataAdapter);

			}
			DBINStoread.close();
		} catch (Exception e) {
    		e.printStackTrace();
    		showToast("Error Exception: " + e.getMessage());
    	}

        
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.read_txt, menu);
		return true;
	}

	

    // *************************************************************************
    // Mostra messaggio toast 
    // *************************************************************************
    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
          }
        });
      }

	
}
