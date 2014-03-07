package com.fant.fanins;

//import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.common.io.Files;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;





public class MainActivity extends FragmentActivity {

	
    ///////////////////////////////////////////////////////////////////////////
    //                          DROPBOX.                      				 //
    ///////////////////////////////////////////////////////////////////////////
    // Replace this with your app key and secret assigned by Dropbox.
    // Note that this is a really insecure way to do this, and you shouldn't
    // ship code which contains your key & secret in such an obvious way.
    // Obfuscation is good.
    final static private String APP_KEY = "7dlkc5hdc0cvk82";
    final static private String APP_SECRET = "4m8c8auq5eheo3q_SECRET";
    ///////////////////////////////////////////////////////////////////////////
    //                      End app-specific settings.                       //
    ///////////////////////////////////////////////////////////////////////////
    // You don't need to change these, leave them alone.
    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final boolean USE_OAUTH1 = false;

    DropboxAPI<AndroidAuthSession> mApi;

    ///////////////////////////////////////////////////////////////////////////
    //                                 End DROPBOX                           //
    ///////////////////////////////////////////////////////////////////////////
    
    private boolean mDropboxLoggedIn;
	java.io.File retFileDropbox;
	private final String DROPBOX_INS_DIR = "/INS/";	
	
	
	
	
	static final int REQUEST_ACCOUNT_PICKER = 1;
	static final int REQUEST_AUTHORIZATION = 2;
	
	static final int UPLOAD_GDRIVE = 1;	
	static int actAfterAccountPicker;

	static final String SPREADSHEET_INS_TEMP_NAME = "INS_temp";
	

	
	com.google.api.services.drive.model.File fileOnGoogleDrive = null;
	
	private MyDatabase DBINSlocal, DBINSdownloaded;
	  	
    private static Drive service;
	private GoogleAccountCredential credential;	  
	    				
	private String valData, valTipoOper, valChiFa, valADa, valPersonale, valValore, valCategoria, valDescrizione, valNote;
	private boolean fileAccessOK;
	
	
	Menu myMainMenu;
	private boolean fileSqliteAccessOK;
	
	public static String fileName, fileNameFull;
	Spinner spinCategoria;
	String[] arrCategoria;
	AutoCompleteTextView textCategoria;
	ArrayAdapter<String> adapterCat;
	ArrayAdapter<String> adapterCatTxt;
	Spinner spinADa;
	String[] arrADa;
	AutoCompleteTextView textADa;
	ArrayAdapter<CharSequence> adapterADa;
	ArrayAdapter<CharSequence> adapterADaTxt;
	
	private ProgressDialog progDia = null;
	
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    // *************************************************************************
	// OnCreate
    // *************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // We create a new AuthSession so that we can use the Dropbox API.
        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        
        checkDropboxAppKeySetup();
                
        
		Spinner spinner;
		ArrayAdapter<CharSequence> adapter;
		

		EditText editTextData = (EditText) findViewById(R.id.TextData);
		editTextData.setOnTouchListener(new ClickDataButton());
					
		spinner = (Spinner) findViewById(R.id.SpinnerTipoOper);
		adapter = ArrayAdapter.createFromResource(this, R.array.tipo_operazione, android.R.layout.simple_spinner_item);	// Create an ArrayAdapter using the string array and a default spinner layout		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		// Specify the layout to use when the list of choices appears
		spinner.setAdapter(adapter);	// Apply the adapter to the spinner
				 
		spinner = (Spinner) findViewById(R.id.SpinnerChiFa);
		adapter = ArrayAdapter.createFromResource(this, R.array.chi_la_fa, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
				
		spinner = (Spinner) findViewById(R.id.SpinnerPersonale);
		adapter = ArrayAdapter.createFromResource(this, R.array.Personali, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		

		// Categoria 
		spinCategoria  = (Spinner) findViewById(R.id.SpinnerCategoria);
		arrCategoria = getResources().getStringArray(R.array.Categoria);
		adapterCat = new ArrayAdapter<String>(MainActivity.this,   android.R.layout.simple_spinner_item, arrCategoria);		
				
		adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinCategoria.setAdapter(adapterCat);
		//ArrayAdapter myAdap = (ArrayAdapter) spinCategoria.getAdapter();
		spinCategoria.setOnItemSelectedListener(new SelectSpinAutocomplete());

		adapterCatTxt = new ArrayAdapter<String>(MainActivity.this,   android.R.layout.simple_expandable_list_item_1, arrCategoria);
		textCategoria = (AutoCompleteTextView) findViewById(R.id.TextAutocompleteCategoria);
		textCategoria.setAdapter(adapterCatTxt);
		//textCategoria.setCompletionHint("Selezionare o scrivere categoria");
		textCategoria.setOnFocusChangeListener(new ChangeFocusAutoComplete());
		textCategoria.setValidator(new ValidateCategoria());
		
		
		// A/Da
		spinADa = (Spinner) findViewById(R.id.SpinnerADa);
		adapterADa =  ArrayAdapter.createFromResource(this, R.array.a_da, android.R.layout.simple_spinner_item);
		adapterADa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinADa.setAdapter(adapterADa);
		spinADa.setOnItemSelectedListener(new SelectSpinAutocomplete());
		
		textADa = (AutoCompleteTextView) findViewById(R.id.TextAutocompleteADa);
		arrADa = getResources().getStringArray(R.array.a_da);
		adapterADaTxt = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_1, arrADa);
		textADa.setAdapter(adapterADaTxt);
		textADa.setOnFocusChangeListener(new ChangeFocusAutoComplete());
		textADa.setValidator(new ValidateADa());
		
		// prepara file
		fileAccessOK = prepFileisOK();
		DBINSlocal = new MyDatabase(
				getApplicationContext(), 
				myGlobal.getStorageFantDir().getPath() + java.io.File.separator +  myGlobal.LOCAL_DB_FILENAME);
				
		DBINSdownloaded = new MyDatabase(
				getApplicationContext(), 
				myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator +  myGlobal.LOCAL_DOWNLOADED_DB_FILE);
		
		
		
		
		fileSqliteAccessOK = true;		
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
			    
			}   

		}
		DBINSlocal.close();
		
		if (!fileAccessOK) {
			showToast("Error file create: " + fileNameFull);
		}
		
		final ImageButton buttonOK = (ImageButton) findViewById(R.id.imgbtnOK);		
        buttonOK.setOnClickListener(new ClickOKButton());

        final ImageButton buttonReset = (ImageButton)  findViewById(R.id.imgbtnReset);
        buttonReset.setOnClickListener(new ClickResetButton());
        
        initTextValue();
    

        // Display the proper UI state if logged in or not
        setDropboxLoggedIn(mApi.getSession().isLinked());


    }




    // *************************************************************************
    // Gestione Activity result (chiamata Activity con result per scelta credenziali Google)
    // *************************************************************************
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
      switch (requestCode) {
      case REQUEST_ACCOUNT_PICKER:    	
        if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
          String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
          
          if (accountName != null) {
            credential.setSelectedAccountName(accountName);
            
            service = getDriveService(credential);

            if (actAfterAccountPicker == UPLOAD_GDRIVE){
                this.progDia = ProgressDialog.show(this, "INS..", "Uploading Data...", true);
                new uploadFileToGDrive().execute("");            	
            } 
          }
        }
        break;
        
      case REQUEST_AUTHORIZATION:
        if (resultCode == Activity.RESULT_OK) {
        	if (actAfterAccountPicker == UPLOAD_GDRIVE){
                this.progDia = ProgressDialog.show(this, "INS..", "Uploading Data...", true);
                new uploadFileToGDrive().execute("");            	
            } 
        } else {
          startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);          
        }
        break;
        
        default:
        	break;
      
      }
    }    

    
    // *************************************************************************
    // Carico fisicamente il file su Google Drive. Parametri: Directory, NomeFile, Metadata
    // *************************************************************************
    private boolean uploadSingleFile(String _pathName, String _fileName, String _metaData) {
    	
    	Uri fileUriDataBase;
    	
          fileUriDataBase = Uri.fromFile(new java.io.File(_pathName + java.io.File.separator + _fileName));
    	// File's binary content
          java.io.File fileContent = new java.io.File(fileUriDataBase.getPath());
          FileContent mediaContent = new FileContent(_metaData, fileContent);

          // File's metadata.
          com.google.api.services.drive.model.File body;
          body = new File();
          body.setTitle(fileContent.getName());
          body.setMimeType(_metaData);

		try {
			
			//file = service.files().insert(body, mediaContent).execute();
			Drive.Files.Insert insert = service.files().insert(body, mediaContent);
			MediaHttpUploader uploader = insert.getMediaHttpUploader();
			uploader.setDirectUploadEnabled(true);
			fileOnGoogleDrive = insert.execute();
        } catch (UserRecoverableAuthIOException e) {
            startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            showToast("Error UserRecoverableAuthIOException: " );
            return (false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showToast("Error IOException: " + e.getMessage());
			return (false);
		}
                    
        return (true);
         
    }


    // *************************************************************************
    // Upload file di testo in google drive, Task asincrono
    // *************************************************************************
    private class uploadFileToGDrive  extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
        	if (uploadSingleFile(myGlobal.getStorageFantDir().getPath() , fileName, "text/plain"))
        		showToast("Uploaded: " + fileName);
		
        	if (uploadSingleFile(myGlobal.getStorageFantDir().getPath() , myGlobal.LOCAL_DB_FILENAME, "application/octet-stream"))
        		showToast("Uploaded: " + myGlobal.LOCAL_DB_FILENAME);
        	
        	return "Executed";
        }      
        

        @Override
        protected void onPostExecute(String result) {
        	if (MainActivity.this.progDia != null) {
        		MainActivity.this.progDia.dismiss();
        	}
        }

        @Override
        protected void onPreExecute() {        	
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }        
    	
      }
    
    
    


    // *************************************************************************
    // Upload file di testo in google drive, Task asincrono
    // *************************************************************************
    private class updateINS2  extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
        	
        	SpreadsheetService myService = new SpreadsheetService("MySpreadsheetIntegration-v1");
            myService.setProtocolVersion(SpreadsheetService.Versions.V3);
        	try {
        		// TODO. Selezionare account diverso
        		myService.setUserCredentials("fantuz76@gmail.com", "Ramarro1");
        		

        	    // Define the URL to request.  This should never change.
        	    URL SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");

        	    // Make a request to the API and get all spreadsheets.
        	    SpreadsheetFeed feed = myService.getFeed(SPREADSHEET_FEED_URL,SpreadsheetFeed.class);
        	    List<SpreadsheetEntry> spreadsheetList = feed.getEntries();

        	    if (spreadsheetList.size() == 0) {
        	      // TODO: There were no spreadsheets, act accordingly.
        	    	showToast("No sheets found");
        	    } else {

        	    	// cerco posizione di SPREADSHEET_INS_TEMP_NAME
            	    int i, pos_sheet=0;
            	    for (i=0; i<spreadsheetList.size(); i++) {
            	    	//showToast(spreadsheetList.get(i).getTitle().toString());
            	    	//showToast(spreadsheetList.get(i).getPlainTextContent());
            	    	if (spreadsheetList.get(i).getTitle().getPlainText().equals(SPREADSHEET_INS_TEMP_NAME)) {	            	    		
            	    		pos_sheet = i;
            	    	}
            	    }	            	    
            	    SpreadsheetEntry spreadsheet = spreadsheetList.get(pos_sheet);	            	    
            	    if (spreadsheet.getTitle().getPlainText().equals("INS_temp")) {

	            	    showToast("Inizio scrittura spreadsheet: " + spreadsheet.getTitle().getPlainText());
	            	    //spreadsheet.getTitle().getId()	// questa ritorna il link all'xml con tutte le informazioni

            	    	// Get the first worksheet of the first spreadsheet.
	            	    WorksheetFeed worksheetFeed = myService.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
	            	    List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
	            	    WorksheetEntry worksheet = worksheets.get(0);
            	    	

            	    	int row = 0;
            	    	int col = 0;

	            	    // Fetch the cell feed of the worksheet.
	            	    URL cellFeedUrl = worksheet.getCellFeedUrl();
	            	    CellFeed cellFeed = myService.getFeed(cellFeedUrl, CellFeed.class);
	            	    
	            	    // Guardo ultima cella libera
	            	    for (CellEntry cell : cellFeed.getEntries()) {
	            	    	row = cell.getCell().getRow();
	            	    	col = cell.getCell().getCol();
	            	    }
	            	    if (row < 11) row = 11;		// minimo riga 11
	            	    
	            	    
	            	    // Se file txt OK carico tutto nel fiel excel 
	            	    if (fileAccessOK) {		            	    	
		            		Calendar c = Calendar.getInstance();
		            		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy@HH'h'mm'm'ss's'", Locale.ITALY);
		            		String formattedDate = df.format(c.getTime());
	            	    	CellEntry newCell;
	            	    	String ValToWrite = "";
	            	    	int rowCnt;
	            	    	int colCnt;
	            	    	FileReader fr = new FileReader(fileNameFull);
	            	    	int intread = 0;
	            	    	char chread = 0;
	            	    	
	            	    	rowCnt = row+1;
	            	    	colCnt = 1;
	            	    	
	            	    	// leggo file con campi separati da tab
	            	    	while (intread != -1) {		            	    	
	            	    		intread = fr.read();
	            	    		chread = (char) intread;
		            	    	if (chread == -1) {
		            	    		// fine file
		            	    	} else if (chread == '\n') {
		            	    		// a capo, nuova riga
		            	    		if (colCnt == 1) {			            	    			
		            	    			newCell = new CellEntry(rowCnt, colCnt, formattedDate);
					            	    cellFeed.insert(newCell);
					            	    colCnt++;
		            	    		}
		            	    		newCell = new CellEntry(rowCnt, colCnt, ValToWrite);
				            	    cellFeed.insert(newCell);		            	    		
		            	    		ValToWrite = "";
		            	    		rowCnt++;
		            	    		colCnt = 1;
		            	    	} else if (chread == '\t') {
		            	    		// nuovo valore
		            	    		if (colCnt == 1) {			            	    			
		            	    			newCell = new CellEntry(rowCnt, colCnt, formattedDate);
					            	    cellFeed.insert(newCell);
					            	    colCnt++;
		            	    		}
				            	    newCell = new CellEntry(rowCnt, colCnt, ValToWrite);
				            	    cellFeed.insert(newCell);		            	    		
		            	    		ValToWrite = "";
		            	    		colCnt++;
		            	    	} else {
		            	    		// carattere normale, concatena
		            	    		ValToWrite += chread; 
		            	    	}
	            	    	}
	            	    	fr.close();
	            	    	showToast("Scrittura spreadsheet completata");
            	    	
	            	    	// adesso, una volta caricato lo rinomino cos� resta nella SD del telefono come backup
	            	    	java.io.File oldFile = new java.io.File(fileNameFull);
	            	    	//Now invoke the renameTo() method on the reference, oldFile in this case
	            	    	oldFile.renameTo(new java.io.File(fileNameFull.replace(".txt", "_" + formattedDate + ".txt")));		            	    	
	            	    	showToast("rinominato file txt: " + fileNameFull.replace(".txt", "_" + formattedDate + ".txt"));
	            	    	
	            	    	// prepara file
	            			fileAccessOK = prepFileisOK();
	            	    }
	    			
    	    			
            	    } else {
            	    	showToast("Non trovato spreadsheet " + SPREADSHEET_INS_TEMP_NAME);
            	    }
        	    }
        	  } catch (Exception e) {
        		  showToast("Exception " + e.getMessage());
        		  	
        	  }
              return "Executed";
        }      

        @Override
        protected void onPostExecute(String result) {
                            
              if (MainActivity.this.progDia != null) {
            	  MainActivity.this.progDia.dismiss();
              }
        }

        @Override
        protected void onPreExecute() {        	
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }        
    	
      }
    
    
    
    private Drive getDriveService(GoogleAccountCredential credential) {
        return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).setApplicationName(
                "Google-DriveSample/1.0")
            .build();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.myMainMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {


        	case R.id.action_uploadDB:        	
        		Calendar c = Calendar.getInstance();
        		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy@HHmmss", Locale.ITALY);
        		String formattedDate = df.format(c.getTime());

    	    	// adesso, una volta caricato lo rinomino cos� resta nella SD del telefono come backup
    	    	java.io.File oldFile = new java.io.File(fileNameFull);
    	    	java.io.File newFile = new java.io.File(fileNameFull.replace(".txt", "_" + formattedDate + ".txt"));
    	    	
    	    	//Now invoke the renameTo() method on the reference, oldFile in this case
    	    	
			try {
				Files.copy(oldFile, newFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	    	
                UploadToDropbox upload = new UploadToDropbox(this, mApi, DROPBOX_INS_DIR, newFile);
                upload.execute();
                return true;
                
        	case R.id.action_downloadDB:
        		DownloadFromDropbox download = new DownloadFromDropbox(this, mApi, DROPBOX_INS_DIR, myGlobal.REMOTE_DB_FILENAME,
        				myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_DOWNLOADED_DB_FILE);
                download.execute();
              return true;
              
        	case R.id.action_sync:
    	        //Put up the Yes/No message box
    	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	    	builder    	    	
    	    	.setTitle(R.string.action_sync)
    	    	.setMessage("Are you sure?")
    	    	.setIcon(android.R.drawable.ic_dialog_alert)
    	    	.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	    	    public void onClick(DialogInterface dialog, int which) {    	    	    	    	    	    	
    	        		MainActivity.this.progDia = ProgressDialog.show(MainActivity.this, "INS..", "Sync Data...", true);
    	        		new updateINS2().execute("");    	    	    	
    	    	    }
    	    	})
    	    	.setNegativeButton("No", null)						//Do nothing on no
    	    	.show();
    	    	
        		return true;

        	case R.id.action_upload:        	
                credential = GoogleAccountCredential.usingOAuth2(this, Arrays.asList(DriveScopes.DRIVE));
                startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
                return true;
                
        	case R.id.action_readfile:
            	showToast("Menu setting not available");
            	//showDatePickerDialog(MainActivity.this);
                Intent intent = new Intent(this, ReadTxtActivity.class);
                EditText editText = (EditText) findViewById(R.id.TextDescrizione);
                String message = editText.getText().toString();

				intent.putExtra(EXTRA_MESSAGE, message);
                
                startActivity(intent);
              return true;


              
        	case R.id.action_authDropbox:        		
                if (mDropboxLoggedIn) {
                    logOutDropbox();
                } else {
                    // Start the remote authentication
                    if (USE_OAUTH1) {
                        mApi.getSession().startAuthentication(MainActivity.this);
                    } else {
                        mApi.getSession().startOAuth2Authentication(MainActivity.this);
                    }
                }


        		return true;
              
        	case R.id.action_settings:
            	showToast("Menu setting not available");
            	//showDatePickerDialog(MainActivity.this);
                Intent intentSettings = new Intent(this, SettingsActivity.class);                
                startActivity(intentSettings);
              return true;

            default:
                  return super.onOptionsItemSelected(item);
        }
    }
    
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	// Funzione richiamata ogni volta che viene presentato il MENU
    	MenuItem myMenuitem = myMainMenu.findItem(R.id.action_authDropbox);
    	
    	if (mDropboxLoggedIn) {
    		myMenuitem.setTitle(R.string.action_authDropbox_logout);
    	} else {
    		myMenuitem.setTitle(R.string.action_authDropbox_login);
    	}
    	
        return super.onPrepareOptionsMenu(menu);
    }
    // *************************************************************************
    // Controllo valori inseriti
    // *************************************************************************
    public boolean checkAllValues()  {
    	if (!(checkData())) {
    		showToast("Data Sbagliata");    		
    		return false;
    	}
    	
    	if (!checkValore()) {
    		showToast("Valore � Sbagliato");
    		return false;
    	} 
    	
    	if (!checkCategoria()) {
    		showToast("Categoria Sbagliata");
    		return false;
    	}
    	
    	if (!checkADa()) {
    		showToast("A/Da Sbagliato");
    		return false;
    	}
    	return true;
    }
    
    
    public boolean checkData()  {
    	String dataStr = valData;    		
		String[] splitData = {""};
		int giorno, mese, anno;
		boolean changed = false, errData = false;
		
		if (dataStr.contains("/")) {
				splitData = dataStr.split("/");
		} else if (dataStr.contains(".")) {
			splitData = dataStr.split(".");        				
		} else if (dataStr.contains(" ")) {
			splitData = dataStr.split(" ");        				
		} else if (dataStr.contains("-")) {
			splitData = dataStr.split("-");        				
		}
		if (splitData.length > 2 ) {
			anno = (int) Integer.parseInt(splitData[0]);
			if (anno<0 || anno >2299) {
				anno = 2014;
				errData = changed = true;				
			}
		}		
		if (splitData.length > 0 ) {
			mese = (int) Integer.parseInt(splitData[1]);
			if (mese<0 || mese >12) {
				mese = 1;
				errData = changed = true;
			}
		}
		if (splitData.length > 1 ) {
			giorno = (int) Integer.parseInt(splitData[2]);
			if (giorno<0 || giorno >31) {
				giorno = 1;
				errData = changed = true;
			}
		}

		if (changed) {
			showToast("Attenzione: Errore Data.");
		}

		// reg exp per MM-dd-yyyy o MM/dd/yyyy o MM.dd.yyyy
    	//String regEx = "^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$";
		
		// reg exp per yyyy-MM-dd o yyyy/MM/dd ecc...		
    	String regEx = "^(19|20)\\d\\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])$";
    	if (valData.matches(regEx) && !errData) 
    		return(true);
    	else
    		return(false);    	
    }
    

    public boolean checkValore()  {
    	float myFloat;
    	String regEx = "^(-)?\\d*(\\.\\d*)?$";
    	
    	
    	if (valValore == "") return false;
    	
    	try {
    		myFloat = Float.parseFloat(valValore);
    	} catch (Exception e) {
    		return false;
    	}
    	
    	if (myFloat == 0) return false;
    	
    	if (valValore.matches(regEx)) 
    		return(true);
    	else
    		return(false);
    	
    }
 
    public boolean checkCategoria()  {
    	List<String> lsCat = Arrays.asList(arrCategoria);    	
    	if (lsCat.contains(valCategoria)) 
    		return(true);
    	else
    		return(false);    	
    }
    
    public boolean checkADa()  {
    	List<String> lsCat = Arrays.asList(arrADa);    	
    	if (lsCat.contains(valADa))  {
    		return(true);
    	} else {
    		if (valTipoOper=="Spostamento" && (valADa == "")) {
    			return(false);
    		} else {
    			return(true);
    		}
    	}
    	
    }

    
    // *************************************************************************
    // Preparo file di testo, controllo consistenza 
    // *************************************************************************
    public boolean prepFileisOK() {
    	boolean mExternalStorageAvailable = false;
    	boolean mExternalStorageWriteable = false;
    	
        
        // definisco nome file usando IMEI telefono per avere file diversi da tel diversi
        TelephonyManager tMgr =(TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        final String mIMEIstr;
        if (tMgr.getDeviceId() != null) {
        	mIMEIstr = tMgr.getDeviceId();
        } else {
        	mIMEIstr = "unknownIMEI";
        }
        
        
        // inizializzo nome directory nome file        
        fileName = "FanINS_" + mIMEIstr.trim() +".txt";        
        fileNameFull = myGlobal.getStorageFantDir().getPath() + java.io.File.separator + fileName;
    	
 
		// Verifico presenza SD esterna per salvare dati
    	String state = Environment.getExternalStorageState();
    	if (Environment.MEDIA_MOUNTED.equals(state)) {
    	    // We can read and write the media
    	    mExternalStorageAvailable = mExternalStorageWriteable = true;
    	} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
    	    // We can only read the media
    	    mExternalStorageAvailable = true;
    	    mExternalStorageWriteable = false;
    	} else {
    	    // Something else is wrong. It may be one of many other states, but all we need
    	    //  to know is we can neither read nor write
    	    mExternalStorageAvailable = mExternalStorageWriteable = false;
    	}
    	


    	
    	if (mExternalStorageWriteable && mExternalStorageAvailable)  {
    		try  {

    	    	// Se non esiste creo il file
    	    	java.io.File checkFile = new java.io.File(fileNameFull);
    	    	if (!checkFile.exists()) {
    	    		if (!checkFile.createNewFile())
    	    			return false;
    	    	}    			    			
    			return true;
    		} catch (Exception ioe) {
    			return false;
    		}
    		
    	}
    	
    	return false;
    	
    }
    
    
    // *************************************************************************
    // Salvo dati su file di testo
    // *************************************************************************
    public void saveDataOnFile()  {
    	if (!fileAccessOK) {
    		showToast("Error file create: " + fileNameFull);
    	} else {    	
	        //Put up the Yes/No message box
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder
	    	.setTitle("Save Data file: " + fileName)
	    	.setMessage("Are you sure?")
	    	.setIcon(android.R.drawable.ic_dialog_alert)
	    	.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    	    public void onClick(DialogInterface dialog, int which) {			      	

	    	    	// Scrivo dati nel file, modalit� append	    	    	
	    	    	try  {
    	    	    	FileWriter fw = new FileWriter(fileNameFull, true);

    	    			// Scrivo separando i campi da TAB
    	    			fw.append(valData + '\t');
    	    			fw.append(valTipoOper + '\t');
    	    			fw.append(valChiFa + '\t');
    	    			fw.append(valADa + '\t');
    	    			fw.append(valPersonale + '\t');
    	    			fw.append(valValore + '\t');
    	    			fw.append(valCategoria + '\t');
    	    			fw.append('\t');					// Categoria Generica � vuota la calcola poi file excel
    	    			fw.append(valDescrizione + '\t');
    	    			fw.append(valNote + '\t');

    	                fw.append('\n');
    			
    	    			fw.flush();
    	    			fw.close();
    	    			
    	    	    	showToast("Dati Salvati");
	    	    	} catch (IOException ioe)
	    		      {ioe.printStackTrace();}
	    	    	
	    	    }
	    	})
	    	.setNegativeButton("No", null)						//Do nothing on no
	    	.show();

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

    
    
    // *************************************************************************
    // classe  click button ON
    // *************************************************************************    
    class ClickOKButton implements View.OnClickListener {
    	@Override
        public void onClick(View v) {
            // Perform action on click
        	final EditText editTextData = (EditText) findViewById(R.id.TextData);
        	valData = editTextData.getText().toString();

        	final Spinner editTextTipoOper = (Spinner) findViewById(R.id.SpinnerTipoOper);    				
        	valTipoOper = editTextTipoOper.getSelectedItem().toString();

        	final Spinner editTextChiFa = (Spinner) findViewById(R.id.SpinnerChiFa);    				
        	valChiFa = editTextChiFa.getSelectedItem().toString();

        	final Spinner editTextADa = (Spinner) findViewById(R.id.SpinnerADa);    				
        	valADa = editTextADa.getSelectedItem().toString();

        	final Spinner editTextPersonale = (Spinner) findViewById(R.id.SpinnerPersonale);    				
        	valPersonale = editTextPersonale.getSelectedItem().toString();
        	
        	final EditText editTextValore = (EditText) findViewById(R.id.TextValore);    				
        	valValore = editTextValore.getText().toString();

        	final Spinner editTextCategoria = (Spinner) findViewById(R.id.SpinnerCategoria);    				
        	valCategoria = editTextCategoria.getSelectedItem().toString();
        	
        	final EditText editTextDescrizione = (EditText) findViewById(R.id.TextDescrizione);    				
        	valDescrizione = editTextDescrizione.getText().toString();

        	final EditText editTextNote = (EditText) findViewById(R.id.TextNote);    				
        	valNote = editTextNote.getText().toString();
        	
        	if (checkAllValues()) {
        		saveDataOnFile() ;
        		
        		DBINSlocal.open();
        		DBINSlocal.insertRecordDataIns(valData, valTipoOper, valChiFa, valADa, valPersonale, valValore, valCategoria, valDescrizione, valNote, "");
        		DBINSlocal.close();
        	} else {
        		showToast("Dati non corretti nessun file caricato");
        	}
        	
        }
    };

    
    // *************************************************************************
    // classe  Spinner Selected item
    // *************************************************************************        
	class SelectSpinAutocomplete implements Spinner.OnItemSelectedListener{
		
	    @Override
	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	    		    	
	    	if (parentView.getId() == R.id.SpinnerCategoria) {
	    		AutoCompleteTextView textViewCat = (AutoCompleteTextView) findViewById(R.id.TextAutocompleteCategoria);
	    		textViewCat.setText(spinCategoria.getSelectedItem().toString().trim());
	    	} else if (parentView.getId() == R.id.SpinnerADa) {
	    		AutoCompleteTextView textViewCat = (AutoCompleteTextView) findViewById(R.id.TextAutocompleteADa);
	    		textViewCat.setText(spinADa.getSelectedItem().toString().trim());
	    	}
	    	
	    }

	    @Override
	    public void onNothingSelected(AdapterView<?> parentView) {
	        // your code here
	    	
	    }

	};
		
	
    
    // *************************************************************************
    // classe  per getione Focus Change Categoria
    // *************************************************************************            
    class ChangeFocusAutoComplete implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            //showToast("Focus changed");
            if ((v.getId() == R.id.TextAutocompleteCategoria && !hasFocus) || (v.getId() == R.id.TextAutocompleteADa && !hasFocus)) {
            	showToast("Performing validation");
                ((AutoCompleteTextView)v).performValidation();
            } 
        }
    }

    
    // *************************************************************************
    // classe  per validazione AutoComplete
    // *************************************************************************  
	class ValidateCategoria implements AutoCompleteTextView.Validator {			
		@Override
		public boolean isValid(CharSequence text) {
			List<String> lsCat = Arrays.asList(arrCategoria);
			if (lsCat.contains(text.toString())) {

				int spinnerPosition = adapterCat.getPosition(text.toString());
				spinCategoria.setSelection(spinnerPosition);
				
				return true;
			}

            return false;			
		}
		
		@Override
		public CharSequence fixText(CharSequence invalidText) {
			String fxTxt;
			int numline=0, posch=0, maxposch=0, memoline=0;
             // Whatever value you return here must be in the list of valid words.
			
			List<String> lsCat = Arrays.asList(arrCategoria);
			if (lsCat.contains(invalidText.toString())) {
				return invalidText;
			} else {
				while (numline < arrCategoria.length) {
					posch = 0;
					boolean charCmpIsDifferent = false;
					char cmp1, cmp2;
					while (!charCmpIsDifferent && (posch<arrCategoria[numline].length()) && (posch<invalidText.length())) {
						// confronto carattere per carattere
						cmp1 = arrCategoria[numline].charAt(posch);
						cmp2 = invalidText.charAt(posch);						
						
						// se sono lettere faccio Upcase
						if (Character.isLetter(arrCategoria[numline].charAt(posch))) {
							cmp1 = Character.toUpperCase(cmp1);
							cmp2 = Character.toUpperCase(cmp2);						
						}

						// Se confronto OK proseguo altrimenti mi fermo
						if (cmp1 == cmp2) {
							posch++;
						} else {
							charCmpIsDifferent = true;
						}
							
					}
					
					if (posch > maxposch) {
						maxposch = posch;
						memoline = numline;
					}
					
					numline++;
				}
				fxTxt = arrCategoria[memoline];
				showToast("Text Categoria Fixed: " + fxTxt);

				int spinnerPosition = adapterCat.getPosition(fxTxt);
				spinCategoria.setSelection(spinnerPosition);

				return fxTxt;
			}
			
		}
	};
	
	

	class ValidateADa implements AutoCompleteTextView.Validator {			
		@Override
		public boolean isValid(CharSequence text) {
			List<String> lsADa = Arrays.asList(arrADa);
			if (lsADa.contains(text.toString())) {

				int spinnerPosition = adapterADa.getPosition(text.toString());
				spinADa.setSelection(spinnerPosition);
				
				return true;
			}

            return false;			
		}
		
		@Override
		public CharSequence fixText(CharSequence invalidText) {

             // Whatever value you return here must be in the list of valid words.
			List<String> lsADa = Arrays.asList(arrADa);
			if (lsADa.contains(invalidText.toString())) {
				return invalidText;
			} else {				
				return invalidText;
			}
			
		}
	};	

	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user						
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
			//Date selDate = new Date(year, month, day);
			
			String formattedDate = df.format(new Date(year-1900, month, day));
			
			EditText editTextData = (EditText) getActivity().findViewById(R.id.TextData);
			editTextData.setText(formattedDate);

		}
	}


    
    class ClickDataButton implements View.OnTouchListener {
    	@Override
        public boolean onTouch(View v, MotionEvent event) {        
    		if (MotionEvent.ACTION_UP == event.getAction()) {
        	    DialogFragment newFragment = new DatePickerFragment();
        	    newFragment.show(getSupportFragmentManager(), "datePicker");    			
    		}
    		return false;
        }
    };
	


    // *************************************************************************
    // classe  click button ON
    // *************************************************************************    
    class ClickResetButton implements View.OnClickListener {
    	@Override
        public void onClick(View v) {
            // Perform action on click
    		initTextValue();

        	
        }
    };

    

    public void initTextValue() {
 
		Spinner spinner;
		EditText myeditText;
		//ArrayAdapter<CharSequence> adapter;
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
		String formattedDate = df.format(c.getTime());
		
		myeditText = (EditText) findViewById(R.id.TextData);
		myeditText.setText(formattedDate);
		
		myeditText = (EditText) findViewById(R.id.TextDescrizione);
		myeditText.setText("");

		myeditText = (EditText) findViewById(R.id.TextValore);
		myeditText.setText("0");

		myeditText = (EditText) findViewById(R.id.TextNote);
		myeditText.setText("");
		
			
		spinner = (Spinner) findViewById(R.id.SpinnerTipoOper);
		spinner.setSelection(0, true);
				 
		spinner = (Spinner) findViewById(R.id.SpinnerChiFa);
		spinner.setSelection(0, true);
		
				
		spinner = (Spinner) findViewById(R.id.SpinnerPersonale);
		spinner.setSelection(0, true);	

		// Categoria 
		spinCategoria  = (Spinner) findViewById(R.id.SpinnerCategoria);
		int spinnerPosition = adapterCat.getPosition("Spesa");
		spinCategoria.setSelection(spinnerPosition);


		// A/Da
		spinADa = (Spinner) findViewById(R.id.SpinnerADa);
		spinADa.setSelection(0, true);

    }

   
    
    
    

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void loadAuth(AndroidAuthSession session) {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) return;

        if (key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
            session.setOAuth2AccessToken(secret);
        } else {
            // Still support using old OAuth 1 tokens.
            session.setAccessTokenPair(new AccessTokenPair(key, secret));
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeAuth(AndroidAuthSession session) {
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
            return;
        }
        // Store the OAuth 1 access token, if there is one.  This is only necessary if
        // you're still using OAuth 1.
        AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
        if (oauth1AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
            edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
            edit.commit();
            return;
        }
    }


    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }   

    @Override
    protected void onResume() {
        super.onResume();
        AndroidAuthSession session = mApi.getSession();

        // The next part must be inserted in the onResume() method of the
        // activity from which session.startAuthentication() was called, so
        // that Dropbox authentication completes properly.
        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                // Store it locally in our app for later use
                storeAuth(session);
                setDropboxLoggedIn(true);
            } catch (IllegalStateException e) {
                showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
                Log.i(myGlobal.TAG, "Error authenticating", e);
            }
        }
    }

    private void logOutDropbox() {
        // Remove credentials from the session
        mApi.getSession().unlink();
        // Clear our stored keys
        clearKeys();
        // Change UI state to display logged out version
        setDropboxLoggedIn(false);
    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }
    /**
     * Convenience function to change UI state based on being logged in
     */
    private void setDropboxLoggedIn(boolean loggedIn) {
        	
    	mDropboxLoggedIn = loggedIn;
    	if (loggedIn) {
    		//mSubmit.setText("Unlink from Dropbox");
            //mDisplay.setVisibility(View.VISIBLE);
    	} else {
    		//mSubmit.setText("Link with Dropbox");
            //mDisplay.setVisibility(View.GONE);            
    	}
    }

    private void checkDropboxAppKeySetup() {
        // Check to make sure that we have a valid app key
        if (APP_KEY.startsWith("CHANGE") ||
                APP_SECRET.startsWith("CHANGE")) {
            showToast("You must apply for an app key and secret from developers.dropbox.com, and add them to the DBRoulette ap before trying it.");
            finish();
            return;
        }

        // Check if the app has set up its manifest properly.
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        String scheme = "db-" + APP_KEY;
        String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
        testIntent.setData(Uri.parse(uri));
        PackageManager pm = getPackageManager();
        if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
            showToast("URL scheme in your app's " +
                    "manifest is not set up correctly. You should have a " +
                    "com.dropbox.client2.android.AuthActivity with the " +
                    "scheme: " + scheme);
            finish();
        }
    }

}