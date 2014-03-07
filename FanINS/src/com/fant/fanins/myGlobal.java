package com.fant.fanins;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.os.Environment;


public class myGlobal
{

	public static final String TAG = "FANTUZ_Activity";
	
	public static final String LOCAL_DB_FILENAME = "INSbase_loc.sqlite";	
	public static final String REMOTE_DB_FILENAME = "INSbase.sqlite";	
	public static final String LOCAL_DOWNLOADED_DB_FILE = "INSbase_download.sqlite";
	
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
}