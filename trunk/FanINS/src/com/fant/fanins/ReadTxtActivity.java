package com.fant.fanins;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.DatePicker;
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
	Bundle mySavedInstance;
	
	private int year;
	private int month;
	private int day;
	EditText editTextDateInizio, editTextDateFine, editTextClicked;
	private String sqlOrderTpye = " ORDER BY " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY +" DESC ";;
	
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
		mySavedInstance = savedInstanceState;

		
		try {
			// recupero info extra e decido qual DB usare
			Bundle bun = getIntent().getExtras();
			readDBtype = bun.getString("readDBtype");


			if (readDBtype.equals("full")) {
				Button button1 = (Button) findViewById(R.id.btnread1);				
				button1.setVisibility(View.VISIBLE);
				button1.setText("Ordina");
				button1.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ordinaDatabase();						
					}
				});


				Button button2 = (Button) findViewById(R.id.btnread2);
				button2.setVisibility(View.VISIBLE);
				button2.setText("Salta");
				button2.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						posScroll += myListActivity.getCount() / 20;
						myListActivity.setSelection(posScroll);
						
					}

				});

				Button button3 = (Button) findViewById(R.id.btnread3);
				button3.setVisibility(View.INVISIBLE);



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
				button1.setVisibility(View.VISIBLE);
				button1.setText("Ordina");
				button1.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ordinaDatabase();						
					}
				});


				Button button2 = (Button) findViewById(R.id.btnread2);
				button2.setVisibility(View.VISIBLE);
				button2.setText("Salta");
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


			
			initializeActivity();
	        



			refreshAllDatabase();
			
			
			

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

		case R.id.action_refresh:
			refreshAllDatabase();
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
						+ " (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + editTextDateInizio.getText().toString() + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ editTextDateFine.getText().toString() + "') " + " AND "
						+ MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + " LIKE '%" + value + "%'" + " COLLATE NOCASE OR "
						+ MyDatabase.DataINStable.DESCRIZIONE_KEY + " LIKE '%" + value + "%'" + " COLLATE NOCASE OR "
						+ MyDatabase.DataINStable.NOTE_KEY + " LIKE '%" + value + "%'" + " COLLATE NOCASE OR "
						+ MyDatabase.DataINStable.CATEGORIA_KEY + " LIKE '%" + value + "%'" + " COLLATE NOCASE " ;
				if (readDBtype.equals("full")) {					
					querystr = querystr + sqlOrderTpye;
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

	
	public void refreshAllDatabase() {

		querystr = "SELECT * FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE "
				+ " (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + editTextDateInizio.getText().toString() + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ editTextDateFine.getText().toString() + "') "	;
		if (readDBtype.equals("full")) {
			querystr = querystr + sqlOrderTpye;
		}
		
		DBINStoread.open();

		//mycursor = DBINStoread.fetchDati();
		mycursor = DBINStoread.rawQuery(querystr,  null );

		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			super.onCreate(mySavedInstance);

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
	}
	
	
	private void ordinaDatabase(){
		
		if (sqlOrderTpye.contains(MyDatabase.DataINStable.DATA_OPERAZIONE_KEY +" DESC")) {
			sqlOrderTpye = sqlOrderTpye.replace(MyDatabase.DataINStable.DATA_OPERAZIONE_KEY +" DESC", MyDatabase.DataINStable.DATA_OPERAZIONE_KEY +" ASC");
			showToast("Ordine :" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY +" ASC");
		} else if (sqlOrderTpye.contains(MyDatabase.DataINStable.DATA_OPERAZIONE_KEY +" ASC")) {
			sqlOrderTpye = sqlOrderTpye.replace(MyDatabase.DataINStable.DATA_OPERAZIONE_KEY +" ASC", MyDatabase.DataINStable.ID +" DESC");
			showToast("Ordine :" + MyDatabase.DataINStable.ID +" DESC");
		} else if (sqlOrderTpye.contains(MyDatabase.DataINStable.ID +" DESC")) {
			sqlOrderTpye = sqlOrderTpye.replace(MyDatabase.DataINStable.ID +" DESC", MyDatabase.DataINStable.ID +" ASC");
			showToast("Ordine :" + MyDatabase.DataINStable.ID +" ASC");
		} else if (sqlOrderTpye.contains(MyDatabase.DataINStable.ID +" ASC")) {
			sqlOrderTpye = sqlOrderTpye.replace(MyDatabase.DataINStable.ID +" ASC", MyDatabase.DataINStable.DATA_OPERAZIONE_KEY +" DESC");
			showToast("Ordine :" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY +" DESC");

		}
		refreshAllDatabase();
	}
	


	class ClickDataButtonInizio implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {    		
			if (MotionEvent.ACTION_UP == event.getAction()) {
				editTextClicked = editTextDateInizio;	// oggetto da impostare in callback
				selezionaData();
			}
			return false;
		}
	};

	class ClickDataButtonFine implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {    		
			if (MotionEvent.ACTION_UP == event.getAction()) {
				editTextClicked = editTextDateFine;	// oggetto da impostare in callback
				selezionaData();
			}
			return false;
		}
	};

	private void initializeActivity() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
		String formattedDateOnly = df.format( c.getTime());
				
	    editTextDateInizio = (EditText) findViewById(R.id.DataInizio);
	    editTextDateFine = (EditText) findViewById(R.id.DataFine);
	    
	    editTextDateFine.setText(formattedDateOnly);
	    
	    c.set(Calendar.DAY_OF_YEAR, 1);
	    //c.set(c.get(Calendar.YEAR),1,1);
	    formattedDateOnly = df.format( c.getTime());	    
	    editTextDateInizio.setText(formattedDateOnly);
	    
	    editTextDateInizio.setOnTouchListener(new ClickDataButtonInizio());
	    editTextDateFine.setOnTouchListener(new ClickDataButtonFine());
	    
	}


	private void selezionaData(){
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        String[] datestr = editTextClicked.getText().toString().split("-");

        year = Integer.valueOf(datestr[0]);
        month = Integer.valueOf(datestr[1])-1;        // Calendar di Java ha il mese che parte da 0 e non da 1
        day = Integer.valueOf(datestr[2]);


		Dialog dd = new DatePickerDialog(mycontext, myDateSetListener, year, month, day);
		dd.show();
	}
	
	// questa è la Callback che indica che l'utente ha finito di scegliere la data
	OnDateSetListener myDateSetListener = new OnDateSetListener() {

	    @Override
	    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

	    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);	    	
	    	String formattedDate = df.format(new Date(year-1900, month, day));
	    	
	        Calendar cal = Calendar.getInstance();
	        cal.set(Calendar.YEAR, year);
	        cal.set(Calendar.DAY_OF_MONTH, day);
	        cal.set(Calendar.MONTH, month);
	    	String format = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).format(cal.getTime());
	    	
	    	editTextClicked.setText(format);
	    	refreshAllDatabase();
	    }
	};



}


