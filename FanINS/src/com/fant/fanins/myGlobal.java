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

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.google.common.io.Files;


public class myGlobal
{

	public static final String TAG = "FANTUZ_Activity";
	
	public static final String LOCAL_DB_FILENAME = "INSbase_loc.sqlite";	
	public static final String REMOTE_DB_FILENAME = "INSbase.sqlite";	
	public static final String LOCAL_DOWNLOADED_DB_FILE = "INSbase_download.sqlite";
	public static final String LOCAL_FULL_DB_FILE = "INSbase_full.sqlite";
	
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
 


}