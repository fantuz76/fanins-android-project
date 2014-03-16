package com.fant.fanins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.google.common.io.Files;


public class myGlobal
{

	public static final String TAG = "FANTUZ_Activity";
	
	public static final String LOCAL_DB_FILENAME = "INSbase_loc.sqlite";	
	public static final String REMOTE_DB_FILENAME = "INSbase.sqlite";
	public static final String REMOTE_DB_FILENAME_EMPTY = "INSbase_loc_empty.sqlite";
	public static final String LOCAL_DOWNLOADED_DB_FILE = "INSbase_download.sqlite";
	public static final String LOCAL_FULL_DB_FILE = "INSbase_full.sqlite";
	public static final String DROPBOX_INS_DIR = "/INS/";
	public static boolean statoDBLocal;
	public static boolean statoDBLocalFull;
	public static DropboxAPI<AndroidAuthSession> mApiDropbox;
	
    Context mContext;
    

    // constructor
    public myGlobal(Context context){
        this.mContext = context;
    }
    
    // *************************************************************************
    // Ritorno il percorso dove vado a salvare i file, se non esiste lo crea anche
    // *************************************************************************
    public static java.io.File getStorageFantDir(){
    	// controllo presenza dir e se non c'è la creo
    	String storageDir = Environment.getExternalStorageDirectory().getPath() + java.io.File.separator + "FanINS";
    	java.io.File myfolder = new java.io.File(storageDir);
        if (!myfolder.exists())
        	myfolder.mkdir();        
        return (myfolder);

    }
    
    public static java.io.File getStorageDatabaseFantDir(){
    	// controllo presenza dir e se non c'è la creo
    	String storageDir = Environment.getExternalStorageDirectory().getPath() + java.io.File.separator + "FanINS" + java.io.File.separator + "DB";
    	java.io.File myfolder = new java.io.File(storageDir);
        if (!myfolder.exists())
        	myfolder.mkdir();        
        return (myfolder);
    }
    
    
    // TODO 
    public String convToSQLiteDate(String _dataDaConvertire) throws ParseException {
    	String tmpstr = _dataDaConvertire;
    	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYY");
    	Date myDate = dateFormat.parse(_dataDaConvertire);

		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.ITALY);
			
		
    	return (tmpstr);
    }
    
    public static void copyFiles(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
        
    }
    
    public static void copyFiles2(File src, File dst) throws IOException {
   
		Files.copy(src, dst);
	
    }     
 

    public static boolean checkData(String _valData)  {
    	String dataStr = _valData;    		
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



		// reg exp per MM-dd-yyyy o MM/dd/yyyy o MM.dd.yyyy
    	//String regEx = "^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$";
		
		// reg exp per yyyy-MM-dd o yyyy/MM/dd ecc...		
    	String regEx = "^(19|20)\\d\\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])$";
    	if (_valData.matches(regEx) && !errData) 
    		return(true);
    	else
    		return(false);    	
    }
    
    public static boolean checkValore(String _valValore)  {
    	float myFloat;
    	String regEx = "^(-)?\\d*(\\.\\d*)?$";
    	
    	
    	if (_valValore == "") return false;
    	
    	try {
    		myFloat = Float.parseFloat(_valValore);
    	} catch (Exception e) {
    		return false;
    	}
    	
    	if (myFloat == 0) return false;
    	
    	if (_valValore.matches(regEx)) 
    		return(true);
    	else
    		return(false);
    	
    }

}