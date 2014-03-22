package com.fant.fanins;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.achartengine.model.CategorySeries;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ReportActivity extends ListActivity {

	static final int MY_REQUEST_MODIFY_DATA = 1;

	
	private MyDatabase DBINStoread;

	public static String versionName = "";
	static ListView myListActivity;

	private static SimpleCursorAdapter dataAdapter;

	private Cursor mycursor = null;
	private String querystr = "";

	private ListAdapter myadapter;
	
	EditText editTextDateInizio, editTextDateFine, editTextClicked;

	Context mycontext;
	Bundle mySavedInstance;	
	CategorySeries seriePie;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Operation title bar
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);		 
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.read_title_actionbar);

		//Remove notification bar
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_report);

		mycontext = this;
		mySavedInstance = savedInstanceState;


		initializeActivity();

		if (myGlobal.statoDBLocalFull == false) {
			showToast("Errore di presenza file DB: " + myGlobal.LOCAL_FULL_DB_FILE);
			finish();
			return;
		}

		calcoloTotale();

		




	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.report_actions, menu);
		return super.onCreateOptionsMenu(menu);		

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.action_graph:
			
			//LineGraph line = new LineGraph();
			//Intent lineIntent = line.getIntent(this);
			//startActivity(lineIntent);
			
			
			
			PieGraph pie = new PieGraph("Distribuzione Spese", seriePie);
			Intent pieIntent = pie.getIntent(this);
			startActivity(pieIntent);
			return true;


		default:
			return super.onOptionsItemSelected(item);
		}
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






	private void calcoloTotale () {

		DBINStoread = new MyDatabase(
				getApplicationContext(), 
				myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_FULL_DB_FILE);


		ArrayList<String> columnArray1 = new ArrayList<String>();
		ArrayList<String> columnArray2 = new ArrayList<String>();
		String DataInizio = "2007-01-11";
		String DataFine = "2015-02-01";
		String queryTipoOperazione = "Spesa";
		String queryCPers = "C";
		String queryChiFa = "IWBank";

		DataInizio = editTextDateInizio.getText().toString();
		DataFine = editTextDateFine.getText().toString();
		
		
		DBINStoread.open();
/*
		// ***-- Spese comuni
		queryTipoOperazione = "Spesa";
		queryCPers = "C";		


		queryChiFa = "IWBank";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE " ; 
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}



		queryChiFa = "JB";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE " ; 
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}



		queryChiFa = "Fineco";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE " ; 
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}



		queryChiFa = "LaBanque";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE " ; 
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}


		queryChiFa = "SF";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE " ; 
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}



		queryChiFa = "MPS";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE " ; 
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}












		// ***-- Spese JB
		queryTipoOperazione = "Spesa";
		queryCPers = "JB";		


		queryChiFa = "IWBank";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE " ; 
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}



		queryChiFa = "JB";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE " ; 
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}



		queryChiFa = "Fineco";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE " ; 
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}



		queryChiFa = "LaBanque";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE " ; 
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}


		queryChiFa = "SF";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE " ; 
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}



		queryChiFa = "MPS";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE " ; 
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}











		// ***-- Spese SF
		queryTipoOperazione = "Spesa";
		queryCPers = "SF";		


		queryChiFa = "IWBank";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE " ; 
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}



		queryChiFa = "JB";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE " ; 
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}



		queryChiFa = "Fineco";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE " ; 
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}



		queryChiFa = "LaBanque";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE " ; 
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}


		queryChiFa = "SF";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE " ; 
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}



		queryChiFa = "MPS";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE " ; 
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}







		String queryAda = "";


		// Spostamenti
		queryTipoOperazione = "Spostamento";
		queryCPers = "C";		

		queryChiFa = "IWBank";

		queryAda = "IWBank";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE AND "+ 
				MyDatabase.DataINStable.A_DA_KEY + "='" + queryAda + "' COLLATE NOCASE ";
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " aDa " + queryAda + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}



		queryAda = "JB";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE AND "+ 
				MyDatabase.DataINStable.A_DA_KEY + "='" + queryAda + "' COLLATE NOCASE ";
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " aDa " + queryAda + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}



		queryAda = "Fineco";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE AND "+ 
				MyDatabase.DataINStable.A_DA_KEY + "='" + queryAda + "' COLLATE NOCASE ";
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " aDa " + queryAda + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}



		queryAda = "LaBanque";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE AND "+ 
				MyDatabase.DataINStable.A_DA_KEY + "='" + queryAda + "' COLLATE NOCASE ";
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " aDa " + queryAda + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}


		queryAda = "sf";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE AND "+ 
				MyDatabase.DataINStable.A_DA_KEY + "='" + queryAda + "' COLLATE NOCASE ";
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " aDa " + queryAda + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}




		queryAda = "mps";

		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + queryChiFa + "' COLLATE NOCASE AND "+ 
				MyDatabase.DataINStable.A_DA_KEY + "='" + queryAda + "' COLLATE NOCASE ";
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			assert true;	// nop
		} else {
			mycursor.moveToFirst();
			columnArray1.add(DataInizio + " <-> " + DataFine + " # " + queryTipoOperazione + " " + queryChiFa + " aDa " + queryAda + " " + queryCPers + "   ==>" +
					mycursor.getString(mycursor.getColumnIndex("Total")));
		}




		myadapter = new ArrayAdapter<String>(
				this,
				android.R.layout.simple_list_item_1,
				columnArray1
				);
		setListAdapter(myadapter);		

		*/
		String tmpchifa;

		float ComuniIW = getResultSpesa(DataInizio, DataFine, "C", "IWBank");
		float ComuniJB = getResultSpesa(DataInizio, DataFine, "C", "JB") + getResultSpesa(DataInizio, DataFine, "C", "Fineco") + getResultSpesa(DataInizio, DataFine, "C", "LaBanque");
		float ComuniSF = getResultSpesa(DataInizio, DataFine, "C", "SF") + getResultSpesa(DataInizio, DataFine, "C", "MPS");

		float PersJBIW = getResultSpesa(DataInizio, DataFine, "JB", "IWBank");
		float PersJBJB = getResultSpesa(DataInizio, DataFine, "JB", "JB") + getResultSpesa(DataInizio, DataFine, "JB", "Fineco") + getResultSpesa(DataInizio, DataFine, "JB", "LaBanque");
		float PersJBSF = getResultSpesa(DataInizio, DataFine, "JB", "SF") + getResultSpesa(DataInizio, DataFine, "JB", "MPS");

		float PersSFIW = getResultSpesa(DataInizio, DataFine, "SF", "IWBank");
		float PersSFJB = getResultSpesa(DataInizio, DataFine, "SF", "JB") + getResultSpesa(DataInizio, DataFine, "SF", "Fineco") + getResultSpesa(DataInizio, DataFine, "SF", "LaBanque");
		float PersSFSF = getResultSpesa(DataInizio, DataFine, "SF", "SF") + getResultSpesa(DataInizio, DataFine, "SF", "MPS");


		tmpchifa = "IWBank";
		float SpostdaIWaIW = getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "IWBank");
		float SpostdaIWaJB = getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "JB") + getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "Fineco") + getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "LaBanque");
		float SpostdaIWaSF = getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "SF") + getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "MPS");


		tmpchifa = "JB";
		float SpostdaJBaIW = getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "IWBank");
		float SpostdaJBaJB = getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "JB") + getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "Fineco") + getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "LaBanque");
		float SpostdaJBaSF = getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "SF") + getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "MPS");

		tmpchifa = "Fineco";
		float SpostdaFinecoaIW = getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "IWBank");
		float SpostdaFinecoaJB = getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "JB") + getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "Fineco") + getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "LaBanque");
		float SpostdaFinecoaSF = getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "SF") + getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "MPS");

		tmpchifa = "LaBanque";
		float SpostdaLabanqueaIW = getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "IWBank");
		float SpostdaLabanqueaJB = getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "JB") + getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "Fineco") + getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "LaBanque");
		float SpostdaLabanqueaSF = getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "SF") + getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "MPS");


		float SpostdaJulieAIW = SpostdaJBaIW + SpostdaFinecoaIW + SpostdaLabanqueaIW;
		float SpostdaJulieAJulie = SpostdaJBaJB + SpostdaFinecoaJB + SpostdaLabanqueaJB;
		float SpostdaJulieASimone = SpostdaJBaSF + SpostdaFinecoaSF + SpostdaLabanqueaSF;


		tmpchifa = "SF";
		float SpostdaSFaIW = getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "IWBank");
		float SpostdaSFaJB = getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "JB") + getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "Fineco") + getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "LaBanque");
		float SpostdaSFaSF = getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "SF") + getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "MPS");


		tmpchifa = "MPS";
		float SpostdaMPSaIW = getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "IWBank");
		float SpostdaMPSaJB = getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "JB") + getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "Fineco") + getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "LaBanque");
		float SpostdaMPSaSF = getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "SF") + getResultSpostamento(DataInizio, DataFine, "C", tmpchifa, "MPS");


		float SpostdaSimoneAIW = SpostdaSFaIW + SpostdaMPSaIW;
		float SpostdaSimoneAJulie = SpostdaSFaJB + SpostdaMPSaJB + SpostdaLabanqueaJB;
		float SpostdaSimoneASimone = SpostdaSFaSF + SpostdaMPSaSF;		


		//Spese o movimenti di soldi riguardanti spese comuni				
		//JB sp comuni  	ComuniJB 
		//JB dato a IW 		SpostdaJulieAIW
		//JB preso da IW 		SpostdaIWaJB
		// Tot messi da JB 		ComuniJB + SpostdaJulieAIW - SpostdaIWaJB;

		// SF sp comuni		
		// SF dato a IW	
		// SF preso da IW	
		// Tot messi da SF	ComuniSF + SpostdaSimoneAIW - SpostdaIWaSF


		float SFdovrebbeJB1 = ((ComuniJB + SpostdaJulieAIW - SpostdaIWaJB) - (ComuniSF + SpostdaSimoneAIW - SpostdaIWaSF)) / 2;

		// Spese o passaggi di soldi non riguardanti le spese comuni				
		// SF ha dato a JB		SpostdaSimoneAJulie	
		// SF ha pagato a JB	PersJBSF
		// Tot da SF a JB	

		// JB ha dato a SF		SpostdaJulieASimone
		// JB ha pagato a SF	PersSFJB
		// Tot da JB a SF	

		float SFdovrebbeJB2 = (SpostdaJulieASimone + PersSFJB) - (SpostdaSimoneAJulie + PersJBSF);

		float SFdeve = SFdovrebbeJB1+ SFdovrebbeJB2;
		float SFversaIW = SFdeve * 2;

		showToast("SF deve versare su IW:" + SFversaIW);


		columnArray1.clear();		
		columnArray1.add("");
		columnArray1.add("Il calcolo del dovuto");
		columnArray1.add("SF DEVE= 						" + myGlobal.FloatToStr(SFdeve));
		columnArray1.add("SF DEVE versare su IW= 		" + myGlobal.FloatToStr(SFversaIW));
		columnArray1.add("(valore *2)");
		columnArray1.add("");
		columnArray1.add("SF deve a JB per spese comuni o IW=	" + myGlobal.FloatToStr(SFdovrebbeJB1));
		columnArray1.add("calcolati facendo [Tot messi da JB]-[Tot messi da SF] e poi diviso 2 (tutte spese comuni):");  //((ComuniJB + SpostdaJulieAIW - SpostdaIWaJB) - (ComuniSF + SpostdaSimoneAIW - SpostdaIWaSF)) / 2;
		columnArray1.add("+" + myGlobal.FloatToStr(ComuniJB)+ "\t\tJB sp comuni");
		columnArray1.add("+" + myGlobal.FloatToStr(SpostdaJulieAIW)+ "\t\tJB dato a IW");
		columnArray1.add("-" + myGlobal.FloatToStr(SpostdaIWaJB) +"\t\tJB preso da IW");
		columnArray1.add("[Tot messi da JB]= 			" + myGlobal.FloatToStr((ComuniJB + SpostdaJulieAIW - SpostdaIWaJB)));
		columnArray1.add("");
		columnArray1.add("+" + myGlobal.FloatToStr(ComuniSF) + "\t\tSF sp comuni");
		columnArray1.add("+" + myGlobal.FloatToStr(SpostdaSimoneAIW) + "\t\tSF dato a IW");		
		columnArray1.add("-" + myGlobal.FloatToStr(SpostdaIWaSF) + "\t\tSF preso da IW");
		columnArray1.add("[Tot messi da SF]= 			" + myGlobal.FloatToStr((ComuniSF + SpostdaSimoneAIW - SpostdaIWaSF)));		
		columnArray1.add("");
		columnArray1.add("SF dovrebbe a JB per scambi diretti o personali=	" + myGlobal.FloatToStr(SFdovrebbeJB2));
		columnArray1.add("calcolati facendo [Spost JB->SF] - [Spost SF->JB] :");  //(SpostdaJulieASimone + PersSFJB) - (SpostdaSimoneAJulie + PersJBSF);
		columnArray1.add("+" + myGlobal.FloatToStr(SpostdaJulieASimone) + "\t\tJB ha dato a SF");
		columnArray1.add("+" + myGlobal.FloatToStr(PersSFJB) + "\t\tJB ha pagato pers SF");
		columnArray1.add("[Spost SF->JB]= 			" + myGlobal.FloatToStr((SpostdaSimoneAJulie + PersJBSF)));
		columnArray1.add("");
		columnArray1.add("+" + myGlobal.FloatToStr(SpostdaSimoneAJulie) + "\t\tSF ha dato a JB");
		columnArray1.add("+" + myGlobal.FloatToStr(PersJBSF) + "\t\tSF ha pagato pers JB ");
		columnArray1.add("[Spost JB->SF]= 			" + myGlobal.FloatToStr((SpostdaJulieASimone + PersSFJB)));
		columnArray1.add("");
		columnArray1.add("");
		columnArray1.add("");	
		columnArray1.add("PARZIALI");
		columnArray1.add("");
		columnArray1.add("Sp Comuni fatte da IW/JB/SF");
		columnArray1.add(myGlobal.FloatToStr(ComuniIW));
		columnArray1.add(myGlobal.FloatToStr(ComuniJB));
		columnArray1.add(myGlobal.FloatToStr(ComuniSF));
		columnArray1.add("");
		columnArray1.add("Pers JB    fatte da IW/JB/SF");
		columnArray1.add(myGlobal.FloatToStr(PersJBIW));
		columnArray1.add(myGlobal.FloatToStr(PersJBJB));
		columnArray1.add(myGlobal.FloatToStr(PersJBSF));
		columnArray1.add("");
		columnArray1.add("Pers SF    fatte da IW/JB/SF");
		columnArray1.add(myGlobal.FloatToStr(PersSFIW));
		columnArray1.add(myGlobal.FloatToStr(PersSFJB));
		columnArray1.add(myGlobal.FloatToStr(PersSFSF));			
		columnArray1.add("");
		columnArray1.add("Spost da IW   verso IW/JB/SF");
		columnArray1.add(myGlobal.FloatToStr(SpostdaIWaIW));
		columnArray1.add(myGlobal.FloatToStr(SpostdaIWaJB));
		columnArray1.add(myGlobal.FloatToStr(SpostdaIWaSF));		
		columnArray1.add("");
		columnArray1.add("Spost da JB  verso IW/JB/SF");
		columnArray1.add(myGlobal.FloatToStr(SpostdaJulieAIW));
		columnArray1.add(myGlobal.FloatToStr(SpostdaJulieAJulie));
		columnArray1.add(myGlobal.FloatToStr(SpostdaJulieASimone));		
		columnArray1.add("");
		columnArray1.add("Spost da SF  verso IW/JB/SF");
		columnArray1.add(myGlobal.FloatToStr(SpostdaSimoneAIW));
		columnArray1.add(myGlobal.FloatToStr(SpostdaSimoneAJulie));
		columnArray1.add(myGlobal.FloatToStr(SpostdaSimoneASimone));		

		myadapter = new ArrayAdapter<String>(
				this,
				R.layout.list_report,
				R.id.descrizioneReport,
				columnArray1
				);
		setListAdapter(myadapter);
		

		
		
		/// Impostazione delle serie per i grafici
		seriePie = new CategorySeries("Spese Comuni fatte dai soggetti");
		seriePie.add("Spese IW", ComuniIW);
		seriePie.add("Spese JB", ComuniJB);
		seriePie.add("Spese SF", ComuniSF);
		
		
		
		DBINStoread.close();

	}



	private float getResultSpesa(String _DataInizio, String _DataFine, String _queryCPers, String _queryChiFa){
		String _queryTipoOperazione = "Spesa";
		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + _DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ _DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + _queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + _queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + _queryChiFa + "' COLLATE NOCASE " ; 
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			return 0;
		} else {
			mycursor.moveToFirst();
			if (mycursor.getString(mycursor.getColumnIndex("Total")) == null) 
				return 0;
			String str = mycursor.getString(mycursor.getColumnIndex("Total"));
			float retval;

			retval = Float.valueOf(str);
			return retval;			
		}
	}


	private float getResultSpostamento(String _DataInizio, String _DataFine, String _queryCPers, String _queryChiFa, String _queryAda){
		String _queryTipoOperazione = "Spostamento";
		querystr = "SELECT SUM(Valore) AS Total FROM " + MyDatabase.DataINStable.TABELLA_INSDATA + " WHERE " + 
				" (" + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + ">='" + _DataInizio + "' AND " + MyDatabase.DataINStable.DATA_OPERAZIONE_KEY + "<='"+ _DataFine + "') AND " +
				" " + MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY + "='" + _queryTipoOperazione+"' COLLATE NOCASE AND " + 
				MyDatabase.DataINStable.C_PERS_KEY + "='" + _queryCPers + "' COLLATE NOCASE AND "+
				MyDatabase.DataINStable.CHI_FA_KEY + "='" + _queryChiFa + "' COLLATE NOCASE AND "+ 
				MyDatabase.DataINStable.A_DA_KEY + "='" + _queryAda + "' COLLATE NOCASE ";
		mycursor = DBINStoread.rawQuery(querystr,  null );
		if (mycursor.getCount() == 0) {
			return 0;
		} else {
			mycursor.moveToFirst();
			if (mycursor.getString(mycursor.getColumnIndex("Total")) == null) 
				return 0;			
			String str = mycursor.getString(mycursor.getColumnIndex("Total"));
			float retval;

			retval = Float.valueOf(str);
			return retval;			
		}
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
				
	    editTextDateInizio = (EditText) findViewById(R.id.DataInizioReport);
	    editTextDateFine = (EditText) findViewById(R.id.DataFineReport);
	    
	    editTextDateFine.setText(formattedDateOnly);
	    
	    
    	c.set(Calendar.DAY_OF_YEAR, 1);
    	c.set(Calendar.YEAR, 2007);
    
    	formattedDateOnly = df.format( c.getTime());	    
    	editTextDateInizio.setText(formattedDateOnly);
	    
	    editTextDateInizio.setOnTouchListener(new ClickDataButtonInizio());
	    editTextDateFine.setOnTouchListener(new ClickDataButtonFine());
	    
	}


	private void selezionaData(){
		int year, month, day;
		
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
	    	
	    	calcoloTotale();
	    }
	};

}


