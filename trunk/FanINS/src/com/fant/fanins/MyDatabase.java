package com.fant.fanins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class MyDatabase {  

        SQLiteDatabase mDb;
        DbHelper mDbHelper;
        Context mContext;
    	
        // file di default
    	private static String local_SQL_creation_file = "";    	  	
        private static final String DB_DEFAULT_NAME= myGlobal.getStorageFantDir().getPath() + java.io.File.separator +  "INSbase.sqlite";//nome del db
        private static final int DB_VERSION=1; //numero di versione del nostro db

        private static final String TYPE_DB_STRING = " TEXT COLLATE RTRIM";
        
        public MyDatabase(Context ctx){
                mContext=ctx;
                mDbHelper=new DbHelper(ctx, DB_DEFAULT_NAME, null, DB_VERSION);   //quando istanziamo questa classe, istanziamo anche l'helper (vedi sotto)     
        }

        // Costruttore con parametro DB_NAME
        public MyDatabase(Context ctx, String strDBNAME){
            mContext=ctx;
            mDbHelper=new DbHelper(ctx, strDBNAME, null, DB_VERSION);   //quando istanziamo questa classe, istanziamo anche l'helper (vedi sotto)     
        }

        // Costruttore con parametro DB_NAME e fileCreazioneSQL
        public MyDatabase(Context ctx, String strDBNAME, String _creationFileSQL){
            mContext=ctx;
            
            try  {
    	    	// Se non esiste imposto a "" e userò stringa default
    	    	java.io.File checkFile = new java.io.File(_creationFileSQL);
    	    	if (!checkFile.exists()) {
    	    		_creationFileSQL = "";
    	    	}    			    			
    		} catch (Exception ioe) {    			
    		}            
            local_SQL_creation_file = _creationFileSQL;
            mDbHelper=new DbHelper(ctx, strDBNAME, null, DB_VERSION);   //quando istanziamo questa classe, istanziamo anche l'helper (vedi sotto)     
        }

        public void open(){  //il database su cui agiamo è leggibile/scrivibile
        	// Richiamare questo metodo significa rendere scrivibile il database
        	// Se non esiste automaticamente scatena onCreate di SQLiteOpenHelper
        	mDb=mDbHelper.getWritableDatabase();                
        }
        
        public void close(){ //chiudiamo il database su cui agiamo
                mDb.close();
        }

        static class ProductsMetaData {  // i metadati della tabella, accessibili ovunque
            static final String INSDATA_TABLE = "myINSData";
            //static final String ID = "_id";
            static final String DATA_OPERAZIONE_KEY = "DataOperazione";                
            static final String TIPO_OPERAZIONE_KEY = "TipoOperazione";
            static final String CHI_FA_KEY = "ChiFa";
            static final String A_DA_KEY = "ADa";
            static final String C_PERS_KEY = "CPers";
            static final String VALORE_KEY = "Valore";
            static final String CATEGORIA_KEY = "Categoria";
            static final String GENERICA_KEY = "Generica";
            static final String DESCRIZIONE_KEY = "Descrizione";
            static final String NOTE_KEY = "Note";
            static final String SPECIAL_NOTE_KEY = "SpecialNote";
            
    }
        
        private static final String INSDATA_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "  //codice sql di creazione della tabella
                + ProductsMetaData.INSDATA_TABLE + " ("                         
                + ProductsMetaData.DATA_OPERAZIONE_KEY + TYPE_DB_STRING + ", "
                + ProductsMetaData.TIPO_OPERAZIONE_KEY + TYPE_DB_STRING + ", "
                + ProductsMetaData.CHI_FA_KEY + TYPE_DB_STRING + ", "
                + ProductsMetaData.A_DA_KEY + TYPE_DB_STRING + ", "
                + ProductsMetaData.C_PERS_KEY + TYPE_DB_STRING + ", "
                + ProductsMetaData.VALORE_KEY + " REAL COLLATE RTRIM, "
                + ProductsMetaData.CATEGORIA_KEY + TYPE_DB_STRING + ", "
                + ProductsMetaData.GENERICA_KEY + TYPE_DB_STRING + ", "
                + ProductsMetaData.DESCRIZIONE_KEY + TYPE_DB_STRING + ", "                
                + ProductsMetaData.NOTE_KEY + TYPE_DB_STRING + ", "
                + ProductsMetaData.SPECIAL_NOTE_KEY + TYPE_DB_STRING + ");" ;
                
                
        
        //i seguenti 2 metodi servono per la lettura/scrittura del db. aggiungete e modificate a discrezione
        
        public void insertRecordDataIns(String _valData, String _valTipoOper, String _valChiFa, String _valADa, 
        		String _valPersonale, String _valValore, String _valCategoria, String _valDescrizione, String _valNote, String _valspecialNote){ //metodo per inserire i dati
                ContentValues cv=new ContentValues();
                cv.put(ProductsMetaData.DATA_OPERAZIONE_KEY, _valData);
                cv.put(ProductsMetaData.TIPO_OPERAZIONE_KEY, _valTipoOper);
                cv.put(ProductsMetaData.CHI_FA_KEY, _valChiFa);
                cv.put(ProductsMetaData.A_DA_KEY, _valADa);
                cv.put(ProductsMetaData.C_PERS_KEY, _valPersonale);
                cv.put(ProductsMetaData.VALORE_KEY, _valValore);
                cv.put(ProductsMetaData.CATEGORIA_KEY, _valCategoria);
                cv.put(ProductsMetaData.GENERICA_KEY, "");
                cv.put(ProductsMetaData.DESCRIZIONE_KEY, _valDescrizione);
                cv.put(ProductsMetaData.NOTE_KEY, _valNote);
                cv.put(ProductsMetaData.SPECIAL_NOTE_KEY, _valspecialNote);

                mDb.insert(ProductsMetaData.INSDATA_TABLE, null, cv);
        }
        
        public Cursor fetchProducts(){ //metodo per fare la query di tutti i dati
                return mDb.query(ProductsMetaData.INSDATA_TABLE, null,null,null,null,null,null);               
        }


 
        
    	private String readDBCreationFromFile(String _nomeFileSQL) {

    		//Read text from file
    		StringBuilder text = new StringBuilder();

    		try {
    		    BufferedReader br = new BufferedReader(new FileReader(_nomeFileSQL));
    		    String line;

    		    while ((line = br.readLine()) != null) {
    		        text.append(line);    		        
    		        text.append(System.getProperty("line.separator"));
    		    }
    		}
    		catch (IOException e) {
    		    //You'll need to add proper error handling here
    			
    		}
    		
    		return(text.toString());
    	}
    	
        private class DbHelper extends SQLiteOpenHelper { //classe che ci aiuta nella creazione del db

                public DbHelper(Context context, String name, CursorFactory factory,int version) {
                        super(context, name, factory, version);
                }

                @Override
                public void onCreate(SQLiteDatabase _db) { //solo quando il db viene creato, creiamo la tabella
                	_db.execSQL(INSDATA_TABLE_CREATE);
                		/*
                		if (local_SQL_creation_file == ""){
                			_db.execSQL(INSDATA_TABLE_CREATE);
                		} else {
                			String table_data_create = readDBCreationFromFile(local_SQL_creation_file);
                			_db.execSQL(table_data_create);
                		}*/
                        
                }

                @Override
                public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
                        //qui mettiamo eventuali modifiche al db, se nella nostra nuova versione della app, il db cambia numero di versione

                }

        }
                

}