package com.fant.fanins;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabase {  

        SQLiteDatabase mDb;
        DbHelper mDbHelper;
        Context mContext;
    	
    	
        private static final String DB_NAME= myGlobal.getStorageFantDir().getPath() + java.io.File.separator +  "INSbase.sqlite";//nome del db
        private static final int DB_VERSION=1; //numero di versione del nostro db
        
        public MyDatabase(Context ctx){
                mContext=ctx;
                mDbHelper=new DbHelper(ctx, DB_NAME, null, DB_VERSION);   //quando istanziamo questa classe, istanziamo anche l'helper (vedi sotto)     
        }

        // Costruttore con parametro DB_NAME
        public MyDatabase(Context ctx, String strDBNAME){
            mContext=ctx;
            mDbHelper=new DbHelper(ctx, strDBNAME, null, DB_VERSION);   //quando istanziamo questa classe, istanziamo anche l'helper (vedi sotto)     
        }

        public void open(){  //il database su cui agiamo � leggibile/scrivibile
                mDb=mDbHelper.getWritableDatabase();                
        }
        
        public void close(){ //chiudiamo il database su cui agiamo
                mDb.close();
        }
        
        
        //i seguenti 2 metodi servono per la lettura/scrittura del db. aggiungete e modificate a discrezione
        
        public void insertRecordDataIns(String _valData, String _valTipoOper, String _valChiFa, String _valADa, 
        		String _valPersonale, String _valValore, String _valCategoria, String _valDescrizione, String _valNote){ //metodo per inserire i dati
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

                mDb.insert(ProductsMetaData.INSDATA_TABLE, null, cv);
        }
        
        public Cursor fetchProducts(){ //metodo per fare la query di tutti i dati
                return mDb.query(ProductsMetaData.INSDATA_TABLE, null,null,null,null,null,null);               
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
                
        }

        private static final String INSDATA_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "  //codice sql di creazione della tabella
                        + ProductsMetaData.INSDATA_TABLE + " ("                         
                        + ProductsMetaData.DATA_OPERAZIONE_KEY + " TEXT COLLATE RTRIM, "
                        + ProductsMetaData.TIPO_OPERAZIONE_KEY + " TEXT COLLATE RTRIM, "
                        + ProductsMetaData.CHI_FA_KEY + " TEXT COLLATE RTRIM, "
                        + ProductsMetaData.A_DA_KEY + " TEXT COLLATE RTRIM, "
                        + ProductsMetaData.C_PERS_KEY + " TEXT COLLATE RTRIM, "
                        + ProductsMetaData.VALORE_KEY + " REAL COLLATE RTRIM, "
                        + ProductsMetaData.CATEGORIA_KEY + " TEXT COLLATE RTRIM, "
                        + ProductsMetaData.GENERICA_KEY + " TEXT COLLATE RTRIM, "
                        + ProductsMetaData.DESCRIZIONE_KEY + " TEXT COLLATE RTRIM, "                
                        + ProductsMetaData.NOTE_KEY + " TEXT COLLATE RTRIM);";
        
    		                        
        private class DbHelper extends SQLiteOpenHelper { //classe che ci aiuta nella creazione del db

                public DbHelper(Context context, String name, CursorFactory factory,int version) {
                        super(context, name, factory, version);
                }

                @Override
                public void onCreate(SQLiteDatabase _db) { //solo quando il db viene creato, creiamo la tabella
                        _db.execSQL(INSDATA_TABLE_CREATE);
                }

                @Override
                public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
                        //qui mettiamo eventuali modifiche al db, se nella nostra nuova versione della app, il db cambia numero di versione

                }

        }
                

}