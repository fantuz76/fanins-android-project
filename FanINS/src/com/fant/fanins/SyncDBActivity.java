package com.fant.fanins;

import java.io.File;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.widget.Toast;

public class SyncDBActivity extends ListActivity {

	private MyDatabase DBINSlocal, DBINSdownloaded, DNINSfulllocal;
	
	public static String versionName = "";
	
	private SimpleCursorAdapter dataAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Cursor mycursor;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_txt);

		// prepara file
		File filechk = new File(myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_DOWNLOADED_DB_FILE);
		if(!filechk.exists()) {
			showToast("File scaricato non trovato: " + myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_DOWNLOADED_DB_FILE);
			finish();
			return;
		}
		
		try {
			DBINSlocal = new MyDatabase(
					getApplicationContext(), 
					myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator +  myGlobal.LOCAL_DB_FILENAME);
					
			DBINSdownloaded = new MyDatabase(
					getApplicationContext(), 
					myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_DOWNLOADED_DB_FILE);
			
			
		
			DBINSlocal.open();
			DBINSdownloaded.open();
			
		
			// Attacco DB Locale al DB scaricato
			DBINSdownloaded.execSQLsimple("attach database \"" + myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator +  myGlobal.LOCAL_DB_FILENAME + "\" as locdbatt");
			// Faccio intersezione tra DB scaricato e DB locale su alcune Colonne
			mycursor = DBINSdownloaded.rawQuery("SELECT DataOperazione,TipoOperazione,ChiFa,ADa,CPers,Valore,Categoria,Descrizione FROM  myINSData" 
					+ " INTERSECT " +
					"SELECT DataOperazione,TipoOperazione,ChiFa,ADa,CPers,Valore,Categoria,Descrizione  FROM  locdbatt.myINSData ", null);
			
			// se getCount=0 vuol dire che non ci sono righe doppie
			if (mycursor.getCount() != 0) {
				// TODO Ci sono delle righe doppie 
				
			}
			
			
			
			// Aggiungo al DB scaricato i valori del DB locale che vengono cancellati man mano
			Cursor cursorLocal = DBINSlocal.fetchDati();		
			if (cursorLocal.getCount() != 0) {
				while ( cursorLocal.moveToNext() ) {
					DBINSdownloaded.insertRecordDataIns(
							cursorLocal.getString( cursorLocal.getColumnIndex(MyDatabase.DataINStable.DATA_OPERAZIONE_KEY) ), 
							cursorLocal.getString( cursorLocal.getColumnIndex(MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY) ), 
							cursorLocal.getString( cursorLocal.getColumnIndex(MyDatabase.DataINStable.CHI_FA_KEY) ), 
							cursorLocal.getString( cursorLocal.getColumnIndex(MyDatabase.DataINStable.A_DA_KEY) ), 
							cursorLocal.getString( cursorLocal.getColumnIndex(MyDatabase.DataINStable.C_PERS_KEY) ), 
							cursorLocal.getFloat ( cursorLocal.getColumnIndex(MyDatabase.DataINStable.VALORE_KEY) ), 
							cursorLocal.getString( cursorLocal.getColumnIndex(MyDatabase.DataINStable.CATEGORIA_KEY) ), 
							cursorLocal.getString( cursorLocal.getColumnIndex(MyDatabase.DataINStable.DESCRIZIONE_KEY) ), 
							cursorLocal.getString( cursorLocal.getColumnIndex(MyDatabase.DataINStable.NOTE_KEY) ), 
							cursorLocal.getString( cursorLocal.getColumnIndex(MyDatabase.DataINStable.SPECIAL_NOTE_KEY) ));
					
					// elimino dal database la riga corrispondente, guardando solo il codice ID univoco 
					String actualID = cursorLocal.getString( cursorLocal.getColumnIndex(MyDatabase.DataINStable.ID) );
					DBINSlocal.deleteDatabyID(actualID);
					
				}
			}
			
			DBINSlocal.close();
			DBINSdownloaded.close();
			
			// Salvo il DB downloaded così aggiornato come DB full locale
	    	java.io.File oldFile = new java.io.File(myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_DOWNLOADED_DB_FILE);
	    	//Now invoke the renameTo() method on the reference, oldFile in this case
	    	oldFile.renameTo(new java.io.File( myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_FULL_DB_FILE));		            	    	
	    	showToast("aggiornato file DB full locale: " + myGlobal.LOCAL_FULL_DB_FILE);
			
	    	// adesso lo leggo
	    	DNINSfulllocal  = new MyDatabase(
					getApplicationContext(), 
					myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_FULL_DB_FILE);
	    	DNINSfulllocal.open();
	
	    	//mycursor = DNINSfulllocal.fetchDati();
	    	mycursor = DNINSfulllocal.rawQuery("SELECT * FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " ORDER BY " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY +" DESC",  null );    	
			if (mycursor.getCount() != 0) {
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
			DNINSfulllocal.close();
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
