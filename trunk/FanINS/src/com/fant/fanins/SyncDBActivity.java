package com.fant.fanins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

public class SyncDBActivity extends Activity {

	private MyDatabase DBINSlocal, DBINSdownloaded;	
	public static String versionName = "";	



	// *************************************************************************
	// Classe che esegue in background sincronizzazione Database
	// *************************************************************************
	private class SyncAllDBData  extends AsyncTask<Void, Long, Boolean> {
		private UploadRequest mRequest;
		private boolean mCanceled;
		private String mPath;
		private ProgressDialog mDialog;
		private Long mFileLen;
		private Long totProgressLen;
		private String mErrorMsg;
		Cursor mycursor;
		private int faseTask;

		// *******************************
		// Costruttore, prepara tuttoe mostra dialog
		// *******************************        
		public SyncAllDBData() {

			// riferimento totale progress
			totProgressLen = Long.valueOf(500);


			mPath = myGlobal.DROPBOX_INS_DIR;
			faseTask = 0;


			mDialog = new ProgressDialog(SyncDBActivity.this);
			mDialog.setMax(100);
			mDialog.setMessage("Sincronizzazione Database ");
			mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mDialog.setProgress(0);

			mDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "Cancel", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// This will cancel the putFile operation
					//mRequest.abort();
					mCanceled = true;
					mErrorMsg = "Canceled";
				}
			});

			mDialog.show();
		}


		// *******************************
		// Operazioni da eseguire in background
		// *******************************
		@Override
		protected Boolean doInBackground(Void... params) {
			String path;



			try {
				if (mCanceled) return false;


				// *******************************
				// *-*-*-*  Innanzi tutto scarico ultima versione del file da DropBox

				// Get the metadata for a directory
				Entry dirent = myGlobal.mApiDropbox.metadata(mPath, 1000, null, true, null);

				if (!dirent.isDir || dirent.contents == null) {
					// It's not a directory, or there's nothing in it
					mErrorMsg = "File or empty directory";
					return false;
				}

				// Make a list of everything in it that we can get a thumbnail for
				ArrayList<Entry> thumbs = new ArrayList<Entry>();
				for (Entry ent: dirent.contents) {                	
					if (new String(ent.fileName()).equals(myGlobal.REMOTE_DB_FILENAME)) {
						// Add it to the list of thumbs we can choose from
						thumbs.add(ent);
					}
				}

				if (mCanceled) return false;

				if (thumbs.size() == 0) {
					// Nessun file remoto
					mErrorMsg = "Non trovato: " + myGlobal.REMOTE_DB_FILENAME ;
					return false;
				}

				// Now pick the first one
				Entry ent = thumbs.get(0);
				path = ent.path;
				mFileLen = ent.bytes;

				publishProgress((totProgressLen/10));
				faseTask++;

				try {
					File file = new File(myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_DOWNLOADED_DB_FILE);
					FileOutputStream outputStream;
					outputStream = new FileOutputStream(file);
					DropboxFileInfo info = myGlobal.mApiDropbox.getFile(ent.path, null, outputStream, 
							new ProgressListener() {
						@Override
						public long progressInterval() {
							// Update the progress bar every half-second or so
							return 100;
						}

						@Override
						public void onProgress(long bytes, long total) {    	                	
							publishProgress((totProgressLen/10) + (bytes/mFileLen * (totProgressLen/2)));
						}
					});
					Log.i(myGlobal.TAG, "The file's rev is: " + info.getMetadata().rev);
				} catch (FileNotFoundException e1) {
					mErrorMsg = "Impossibile creare file locale";
					return false;
				}



				publishProgress((totProgressLen/2));
				faseTask++;



				File filechk = new File(myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_DOWNLOADED_DB_FILE);
				if(!filechk.exists()) {
					mErrorMsg = "File scaricato non trovato: " + myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_DOWNLOADED_DB_FILE;    			
					return false;
				}


				// *******************************
				// *-*-*-* Apertura Database locale e remoto scaricato
				DBINSlocal = new MyDatabase(
						getApplicationContext(), 
						myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator +  myGlobal.LOCAL_DB_FILENAME);

				DBINSdownloaded = new MyDatabase(
						getApplicationContext(), 
						myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_DOWNLOADED_DB_FILE);



				DBINSlocal.open();
				DBINSdownloaded.open();

				// *******************************
				// *-*-*-*  Attacco DB Locale al DB scaricato
				DBINSdownloaded.execSQLsimple("attach database \"" + myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator +  myGlobal.LOCAL_DB_FILENAME + "\" as locdbatt");
				// Faccio intersezione tra DB scaricato e DB locale su alcune Colonne
				mycursor = DBINSdownloaded.rawQuery("SELECT DataOperazione,TipoOperazione,ChiFa,ADa,CPers,Valore,Categoria,Descrizione FROM  myINSData" 
						+ " INTERSECT " +
						"SELECT DataOperazione,TipoOperazione,ChiFa,ADa,CPers,Valore,Categoria,Descrizione  FROM  locdbatt.myINSData ", null);

				// se getCount=0 vuol dire che non ci sono righe doppie
				if (mycursor.getCount() != 0) {
					// TODO Ci sono delle righe doppie 

				}

				// *******************************
				// *-*-*-* Aggiungo al DB scaricato i valori del DB locale che vengono cancellati man mano
				Cursor cursorLocal = DBINSlocal.fetchDati();		
				showToast("Inserimento di " + cursorLocal.getCount() + " dati!");
				if (cursorLocal.getCount() != 0) {
					while ( cursorLocal.moveToNext() ) {
						DBINSdownloaded.insertRecordDataIns(
								cursorLocal.getString( cursorLocal.getColumnIndex(MyDatabase.DataINStable.DATA_OPERAZIONE_KEY) ), 
								cursorLocal.getString( cursorLocal.getColumnIndex(MyDatabase.DataINStable.TIPO_OPERAZIONE_KEY) ), 
								cursorLocal.getString( cursorLocal.getColumnIndex(MyDatabase.DataINStable.CHI_FA_KEY) ), 
								cursorLocal.getString( cursorLocal.getColumnIndex(MyDatabase.DataINStable.A_DA_KEY) ), 
								cursorLocal.getString( cursorLocal.getColumnIndex(MyDatabase.DataINStable.C_PERS_KEY) ), 
								cursorLocal.getString( cursorLocal.getColumnIndex(MyDatabase.DataINStable.VALORE_KEY) ), 
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



				publishProgress((totProgressLen*2/3));
				faseTask++;



				// *******************************
				// *-*-*-* Salvo il DB downloaded aggiornato rinominandolo come LOCAL_FULL_DB_FILE
				java.io.File oldFile = new java.io.File(myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_DOWNLOADED_DB_FILE);				
				oldFile.renameTo(new java.io.File( myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_FULL_DB_FILE));		            	    	
				showToast("aggiornato file DB full locale: " + myGlobal.LOCAL_FULL_DB_FILE);			


				// *******************************
				// *-*-*-* Preparo l'upload creando in locale una copia del file LOCAL_FULL_DB_FILE con nome REMOTE_DB_FILENAME
				java.io.File oldFileDB = new java.io.File(myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator +  myGlobal.LOCAL_FULL_DB_FILE);        		
				java.io.File newFileDB2 = new java.io.File(myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator +  myGlobal.REMOTE_DB_FILENAME);
				myGlobal.copyFiles(oldFileDB, newFileDB2);
				//myGlobal.copyFiles2(oldFileDB, newFileDB);   	    	
				//Files.copy(oldFileDB, newFileDB);




				// *******************************
				// *-*-*-* Upload del file REMOTE_DB_FILENAME
				mFileLen = newFileDB2.length();
				FileInputStream fis = new FileInputStream(newFileDB2);
				path = mPath + newFileDB2.getName();    
				if (true) {
					// prima di fare upload tento di farne una copia di backup in remoto
					try {
						DropboxAPI.Entry newEntry = myGlobal.mApiDropbox.copy(path, path + ".bkup_" + myGlobal.formattedDate());
					} catch (DropboxUnlinkedException e) {
						Log.e(myGlobal.TAG, "User has unlinked." + e.getMessage());
					} catch (DropboxException e) {
						Log.e(myGlobal.TAG, "Something went wrong while copying."  + e.getMessage());
					}
				}
				// By creating a request, we get a handle to the putFile operation, so we can cancel it later if we want to
				mRequest = myGlobal.mApiDropbox.putFileOverwriteRequest(path, fis, newFileDB2.length(), 
						new ProgressListener() {
					@Override
					public long progressInterval() {
						// Update the progress bar every ...
						return 100;
					}

					@Override
					public void onProgress(long bytes, long total) {
						publishProgress(((totProgressLen * 2/3)) + (bytes/mFileLen * (totProgressLen * 1/3)) );
					}
				});

				// *******************************
				// *-*-*-* Operazione terminata
				if (mRequest != null) {
					mRequest.upload();
					return true;
				}
				return true;

			} catch (DropboxUnlinkedException e) {
				// The AuthSession wasn't properly authenticated or user unlinked.
				mErrorMsg = "Dropbox session not properly authenticated or user unlinked";
			} catch (DropboxPartialFileException e) {
				// We canceled the operation
				mErrorMsg = "Download canceled";
			} catch (DropboxServerException e) {
				// Server-side exception.  These are examples of what could happen, 
				// but we don't do anything special with them here.
				if (e.error == DropboxServerException._304_NOT_MODIFIED) {
					// won't happen since we don't pass in revision with metadata
				} else if (e.error == DropboxServerException._401_UNAUTHORIZED) {
					// Unauthorized, so we should unlink them.  You may want to
					// automatically log the user out in this case.
				} else if (e.error == DropboxServerException._403_FORBIDDEN) {
					// Not allowed to access this
				} else if (e.error == DropboxServerException._404_NOT_FOUND) {
					// path not found (or if it was the thumbnail, can't be thumbnailed)
				} else if (e.error == DropboxServerException._406_NOT_ACCEPTABLE) {
					// too many entries to return
				} else if (e.error == DropboxServerException._415_UNSUPPORTED_MEDIA) {
					// can't be thumbnailed
				} else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
					// user is over quota
				} else {
					// Something else
				}
				// This gets the Dropbox error, translated into the user's language
				mErrorMsg = e.body.userError;
				if (mErrorMsg == null) {
					mErrorMsg = e.body.error;
				}
			} catch (DropboxIOException e) {
				// Happens all the time, probably want to retry automatically.
				mErrorMsg = "Network error.  Try again.";
			} catch (DropboxParseException e) {
				// Probably due to Dropbox server restarting, should retry
				mErrorMsg = "Dropbox error.  Try again.";
			} catch (DropboxException e) {
				// Unknown error
				mErrorMsg = "Unknown error.  Try again.";
			} catch (FileNotFoundException e1) {
				mErrorMsg = "File not found Exception. " + e1.getMessage();
			} catch (IOException e) {            	
				mErrorMsg = "Error IOException: " + e.getMessage();
				return (false);
			}




			return true;
		}      

		@Override
		protected void onPostExecute(Boolean result) {
			mDialog.dismiss();
			if (result) {
				// result OK
				showToast("Sincronizzazione terminata correttamente");
			} else {
				// Couldn't download it, so show an error
				showToast(mErrorMsg);
			}

		}

		@Override
		protected void onPreExecute() {        	
		}

		@Override
		protected void onProgressUpdate(Long... progress) {
			int percent = (int)(100.0*(double)progress[0]/totProgressLen + 0.5);
			mDialog.setProgress(percent);
			if (faseTask == 1) {
				mDialog.setMessage("Downloading " + myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_DOWNLOADED_DB_FILE);
			} else if (faseTask == 2) {
				mDialog.setMessage("Aggiornamento dati database.. ");
			} else if (faseTask == 3) {
				mDialog.setMessage("Uploading " + myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.REMOTE_DB_FILENAME);
			}

		}        

	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {


		super.onCreate(savedInstanceState);
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		//Remove notification bar
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_syncdb);


		SyncAllDBData ss = new SyncAllDBData();
		ss.execute();

		/*
		DownloadFromDropbox download1 = new DownloadFromDropbox(SyncDBActivity.this, myGlobal.mApiDropbox, myGlobal.DROPBOX_INS_DIR, myGlobal.REMOTE_DB_FILENAME,
				myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_DOWNLOADED_DB_FILE);





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
							cursorLocal.getString( cursorLocal.getColumnIndex(MyDatabase.DataINStable.VALORE_KEY) ), 
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

			// Salvo il DB downloaded cos� aggiornato come DB full locale
	    	java.io.File oldFile = new java.io.File(myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_DOWNLOADED_DB_FILE);
	    	//Now invoke the renameTo() method on the reference, oldFile in this case
	    	oldFile.renameTo(new java.io.File( myGlobal.getStorageDatabaseFantDir().getPath() + java.io.File.separator + myGlobal.LOCAL_FULL_DB_FILE));		            	    	
	    	showToast("aggiornato file DB full locale: " + myGlobal.LOCAL_FULL_DB_FILE);			

		} catch (Exception e) {
    		e.printStackTrace();
    		showToast("Error Exception: " + e.getMessage());
		}
		 */
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
