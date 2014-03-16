package com.fant.fanins;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ReadTxtActivity extends ListActivity {

	private MyDatabase DBINStoread;
	
	public static String versionName = "";
	static ListView myListActivity;
	
	private static SimpleCursorAdapter dataAdapter;
	private int posizioneDaEditare;
	private Cursor mycursor = null;
	private String querystr = "";
	
	Context mycontext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		//Remove notification bar
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_read_txt);

		
		mycontext = this;

		
		try {
			// recupero info extra e decido qual DB usare
			Bundle bun = getIntent().getExtras();
			String readDBtype = bun.getString("readDBtype");
			

			if (readDBtype.equals("full")) {
				if (myGlobal.statoDBLocalFull == false) {
					showToast("Errore di presenza file DB: " + myGlobal.LOCAL_FULL_DB_FILE);
					finish();
					return;
				}
				DBINStoread = new MyDatabase(
						getApplicationContext(), 
						myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_FULL_DB_FILE);
			} else {
				if (myGlobal.statoDBLocal == false) {
					showToast("Errore di presenza file DB: " + myGlobal.LOCAL_DB_FILENAME);
					finish();
					return;
				}
				DBINStoread = new MyDatabase(
						getApplicationContext(), 
						myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator +  myGlobal.LOCAL_DB_FILENAME);
			}


			
			DBINStoread.open();
			
			//mycursor = DBINStoread.fetchDati();
			querystr = "SELECT * FROM " + MyDatabase.DataINStable.TABELLA_INSDATA ;
			if (readDBtype.equals("full")) {
				querystr = querystr + " ORDER BY " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY +" DESC";
			}
			mycursor = DBINStoread.rawQuery(querystr,  null );
			
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
						 
			myListActivity = getListView();

			myListActivity.setOnItemClickListener(new OnItemClickListener() {

		        @Override
		        public void onItemClick(AdapterView<?> arg0, View view, int pos,
		            long id) {
		            // TODO Auto-generated method stub

		            //some code here...

		            //String posit = values.get(pos).toString();
		        	showToast("Pressione");
		            
		        }
		    });
			
			myListActivity.setOnItemLongClickListener(new OnItemLongClickListener() {

			    @Override
			    public boolean onItemLongClick(AdapterView<?> arg0, View view,
			            int pos, long id) {
			    	
			    	posizioneDaEditare = pos;

			    	
			    	CharSequence sceltePopup1[] = new CharSequence[] {"modifica", "elimina", "seleziona"};

			    	AlertDialog.Builder builder = new AlertDialog.Builder(mycontext);
			    	//builder.setTitle("Scegliere operazione");
			    	builder.setItems(sceltePopup1, new DialogInterface.OnClickListener() {
			    		
			    	    @Override
			    	    public void onClick(DialogInterface dialog, int which) {
			    	        // the user clicked on colors[which]
			    	    	
			    	    	switch (which) {
			    	    	case 0:
			    	    		break;
			    	    		
			    	    	case 1:
				    	    	mycursor.moveToPosition(posizioneDaEditare);
				    	    	String _dbID = mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.ID));
				    	    	DBINStoread.open();
				    	    	DBINStoread.deleteDatabyID(_dbID);
				    	    	mycursor = DBINStoread.rawQuery(querystr,  null );
				    	    	dataAdapter.changeCursor(mycursor);
				    	    	dataAdapter.notifyDataSetChanged();		                    
				    	    	
				    	    	DBINStoread.close();
				    	    	
				    	    	showToast("Cancellato elemento _id=" + _dbID);			    	    		
			    	    		break;

			    	    	default:
			    	    		break;
			    	    	}
			    	    				    	    	
			    	    }
			    	});
			    	builder.show();

			    	
			        return true;
			    }
			});


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
