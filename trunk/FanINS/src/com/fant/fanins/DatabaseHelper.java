package com.fant.fanins;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "INSbase.sqlite";
    private static final int DATABASE_VERSION = 1;

    // Lo statement SQL di creazione del database
    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS myINSData ("
    		+ "DataOperazione TEXT COLLATE RTRIM,"
    		+ "TipoOperazione TEXT COLLATE RTRIM,"
    		+ "ChiFa TEXT COLLATE RTRIM,"
    		+ "ADa TEXT COLLATE RTRIM,"
    		+ "CPers TEXT COLLATE RTRIM,"
    		+ "Valore REAL COLLATE RTRIM,"
    		+ "Categoria TEXT COLLATE RTRIM,"
    		+ "Generica TEXT COLLATE RTRIM,"
    		+ "Descrizione TEXT COLLATE RTRIM,"
    		+ "Note NULL COLLATE RTRIM);";

    // Costruttore
    public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Questo metodo viene chiamato durante la creazione del database
    @Override
    public void onCreate(SQLiteDatabase database) {
            database.execSQL(DATABASE_CREATE);
    }

    // Questo metodo viene chiamato durante l'upgrade del database, ad esempio quando viene incrementato il numero di versione
    @Override
    public void onUpgrade( SQLiteDatabase database, int oldVersion, int newVersion ) {
            // Questo elimina la tabella e la sostituisce con nuova versione chiamata in onCreate 
            // database.execSQL("DROP TABLE IF EXISTS myINSData");
            //onCreate(database);
             
    }
}