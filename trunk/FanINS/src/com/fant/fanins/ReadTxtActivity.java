package com.fant.fanins;

import java.io.IOException;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ReadTxtActivity extends ListActivity {

	static final int MY_REQUEST_MODIFY_DATA = 1;

	private String readDBtype;
	private MyDatabase DBINStoread;

	public static String versionName = "";
	static ListView myListActivity;

	private static SimpleCursorAdapter dataAdapter;
	private int posizioneDaEditare;
	private Cursor mycursor = null;
	private String querystr = "";
	private int posScroll;

	Context mycontext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Operation title bar
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);		 
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.read_title_actionbar);

		//Remove notification bar
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_read_txt);
		posScroll = 0;


		mycontext = this;


		try {
			// recupero info extra e decido qual DB usare
			Bundle bun = getIntent().getExtras();
			readDBtype = bun.getString("readDBtype");


			if (readDBtype.equals("full")) {
				Button button1 = (Button) findViewById(R.id.btnread1);
				button1.setVisibility(View.INVISIBLE);


				Button button2 = (Button) findViewById(R.id.btnread2);
				button2.setText("Salta");
				button2.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						posScroll += myListActivity.getCount() / 20;
						myListActivity.setSelection(posScroll);
						
					}

				});

				Button button3 = (Button) findViewById(R.id.btnread3);
				button3.setText("Ordina");


				if (myGlobal.statoDBLocalFull == false) {
					showToast("Errore di presenza file DB: " + myGlobal.LOCAL_FULL_DB_FILE);
					finish();
					return;
				}
				DBINStoread = new MyDatabase(
						getApplicationContext(), 
						myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_FULL_DB_FILE);
			} else {

				Button button1 = (Button) findViewById(R.id.btnread1);
				button1.setVisibility(View.INVISIBLE);


				Button button2 = (Button) findViewById(R.id.btnread2);
				button2.setText("salta");
				button2.setVisibility(View.VISIBLE);
				button2.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						posScroll += myListActivity.getCount() / 5;
						myListActivity.setSelection(posScroll);
					}

				});

				Button button3 = (Button) findViewById(R.id.btnread3);
				button3.setVisibility(View.INVISIBLE);



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
					String _dbID = mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.ID));
					String _specialNotes = mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.SPECIAL_NOTE_KEY));
					showToast("Elemento posizione="+ pos + "  _id=" + _dbID + "  Special notes=" + _specialNotes);									
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
							String _dbID;
							// the user clicked on colors[which]

							switch (which) {
							case 0:
								mycursor.moveToPosition(posizioneDaEditare);
								Intent intent = new Intent(ReadTxtActivity.this, ModifyDataActivity.class);


								// Passo parametri a nuova activity, con lo stesso nome della colonna DataBase
								intent.putExtra(MyDatabase.DataINStable.DATA_OPERAZIONE_KEY, 
										mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.DATA_OPERAZIONE_KEY)));
								intent.putExtra(MyDatabase.DataINStable.CHI_FA_KEY, 
										mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.CHI_FA_KEY)));
								intent.putExtra(MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY, 
										mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY)));
								intent.putExtra(MyDatabase.DataINStable.A_DA_KEY, 
										mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.A_DA_KEY)));
								intent.putExtra(MyDatabase.DataINStable.C_PERS_KEY, 
										mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.C_PERS_KEY)));
								intent.putExtra(MyDatabase.DataINStable.VALORE_KEY, 
										mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.VALORE_KEY)));
								intent.putExtra(MyDatabase.DataINStable.CATEGORIA_KEY, 
										mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.CATEGORIA_KEY)));
								intent.putExtra(MyDatabase.DataINStable.DESCRIZIONE_KEY, 
										mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.DESCRIZIONE_KEY)));
								intent.putExtra(MyDatabase.DataINStable.NOTE_KEY, 
										mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.NOTE_KEY)));

								startActivityForResult(intent, MY_REQUEST_MODIFY_DATA);
								break;

							case 1:
								mycursor.moveToPosition(posizioneDaEditare);
								_dbID = mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.ID));
								DBINStoread.open();
								DBINStoread.deleteDatabyID(_dbID);
								mycursor = DBINStoread.rawQuery(querystr,  null );
								dataAdapter.changeCursor(mycursor);
								dataAdapter.notifyDataSetChanged();		                    

								DBINStoread.close();

								showToast("Cancellato elemento _id=" + _dbID);			    	    		
								break;

							case 2:
								_dbID = mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.ID));
								showToast("Elemento _id=" + _dbID);									
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
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.read_data_actions, menu);
		return super.onCreateOptionsMenu(menu);		

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{

		case R.id.action_search:        	    		
			SearchTextOnDB();
			return true;

		case R.id.action_read_data_upload:
			// 
			// Preparo l'upload creando in locale una copia del file LOCAL_FULL_DB_FILE con nome REMOTE_DB_FILENAME
			java.io.File oldFileDB = new java.io.File(myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator +  myGlobal.LOCAL_FULL_DB_FILE);        		
			java.io.File newFileDB2 = new java.io.File(myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator +  myGlobal.REMOTE_DB_FILENAME);
			try {
				myGlobal.copyFiles(oldFileDB, newFileDB2);
			} catch (IOException e) {
				e.printStackTrace();
				showToast("Errore " + e.getMessage());
			}

			// Effettuo UPLOAD tentando il backup remoto e cancellando il file in locale alla fine
	  		UploadToDropbox uploadDB = new UploadToDropbox(this, myGlobal.mApiDropbox, myGlobal.DROPBOX_INS_DIR, newFileDB2, true, true);
    		uploadDB.execute();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}


	class SearchTextOnDBListener implements View.OnClickListener { 
		@Override
		public void onClick(View v) {
			SearchTextOnDB();
		}
	}

	public void SearchTextOnDB() {
		AlertDialog.Builder alert = new AlertDialog.Builder(ReadTxtActivity.this);


		alert.setMessage("Cerca Descrizione");

		// Set an EditText view to get user input 
		final EditText input = new EditText(ReadTxtActivity.this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();

				querystr = "SELECT * FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE "
						+ MyDatabase.DataINStable.DESCRIZIONE_KEY + " LIKE '%" + value + "%'" + " OR "
						+ MyDatabase.DataINStable.NOTE_KEY + " LIKE '%" + value + "%'" + " OR "
						+ MyDatabase.DataINStable.CATEGORIA_KEY + " LIKE '%" + value + "%'" ;
				if (readDBtype.equals("full")) {
					querystr = querystr + " ORDER BY " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY +" DESC";
				}

				DBINStoread.open();
				mycursor = DBINStoread.rawQuery(querystr,  null );
				dataAdapter.changeCursor(mycursor);
				dataAdapter.notifyDataSetChanged();		                    
				DBINStoread.close();

			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();
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


	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (requestCode == MY_REQUEST_MODIFY_DATA) {
			if (resultCode == RESULT_OK) {
				String _dbID;
				mycursor.moveToPosition(posizioneDaEditare);

				// Attenzione devo reinizializzare _dbID dopo aver riposizionato il cursore, perchè 
				// durante le manovre il cursore a volte si sposta (touch sullo schermo??)
				_dbID = mycursor.getString( mycursor.getColumnIndex(MyDatabase.DataINStable.ID));

				DBINStoread.open();
				int numElemMod = DBINStoread.updateRecordDataIns(
						_dbID, 
						data.getStringExtra(MyDatabase.DataINStable.DATA_OPERAZIONE_KEY), 
						data.getStringExtra(MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY), 
						data.getStringExtra(MyDatabase.DataINStable.CHI_FA_KEY), 
						data.getStringExtra(MyDatabase.DataINStable.A_DA_KEY), 
						data.getStringExtra(MyDatabase.DataINStable.C_PERS_KEY), 
						data.getStringExtra(MyDatabase.DataINStable.VALORE_KEY), 
						data.getStringExtra(MyDatabase.DataINStable.CATEGORIA_KEY), 
						data.getStringExtra(MyDatabase.DataINStable.DESCRIZIONE_KEY), 
						data.getStringExtra(MyDatabase.DataINStable.NOTE_KEY), 
						"");
				mycursor = DBINStoread.rawQuery(querystr,  null );
				dataAdapter.changeCursor(mycursor);
				dataAdapter.notifyDataSetChanged();		                    

				DBINStoread.close();

				showToast("Modificati " + numElemMod + " elementi. _id=" + _dbID);			    	    		


			}
		}
	}

}
