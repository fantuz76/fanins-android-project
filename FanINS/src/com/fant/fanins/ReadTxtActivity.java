package com.fant.fanins;

import android.app.ListActivity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ReadTxtActivity extends ListActivity {

	public static String versionName = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_txt);


		try {
			versionName = this.getPackageManager()
            	    .getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			versionName = "Errore versione non rilevata";
		}

		TextView stFrom  = (TextView) findViewById(R.id.appVersion);
		stFrom.setText(versionName);
		
		
		// storing string resources into Array
        //String[] my_string_list = getResources().getStringArray(R.array.Categoria);
         
        // Binding resources Array to ListAdapter
        //this.setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, R.id.textView1, my_string_list));
/*
        ArrayAdapter<CharSequence> adapter1;
		adapter1 = ArrayAdapter.createFromResource(this, R.array.tipo_operazione, android.R.layout.simple_spinner_item);	// Create an ArrayAdapter using the string array and a default spinner layout		
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		// Specify the layout to use when the list of choices appears
*/	
		//spinner.setAdapter(adapter1);	// Apply the adapter to the spinner

        
        String[] my_string_list = getResources().getStringArray(R.array.Categoria);
        ListView listView = (ListView)findViewById(android.R.id.list);        
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this, R.layout.list_item, R.id.label, my_string_list);
        listView.setAdapter(arrayAdapter);
        
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.read_txt, menu);
		return true;
	}

}
