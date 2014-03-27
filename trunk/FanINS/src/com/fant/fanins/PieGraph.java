package com.fant.fanins;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class PieGraph {
	CategorySeries series;
	String Titolo;
	
	public PieGraph(){
		int[] values = { 1, 2, 3, 4, 5 };
		int k = 0;
		series = new CategorySeries("Pie Graph");
		for (int value : values) {
			series.add("Section " + ++k, value);
		}
		Titolo = "Demo";

	}

	public PieGraph(String _title, CategorySeries _cat){
		Titolo = _title;
		series = _cat;
	}

	public Intent getIntent(Context context) {

		int[] colors = new int[] { Color.rgb(40, 122, 169), Color.rgb(169, 122, 35), Color.rgb(200, 234, 229), Color.YELLOW, Color.CYAN, Color.GRAY };

		DefaultRenderer renderer = new DefaultRenderer();
		for (int i=0; i<series.getItemCount();i++) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(colors[i]);
			renderer.addSeriesRenderer(r);
		}
		renderer.setChartTitle(Titolo);
		renderer.setChartTitleTextSize(28);
		renderer.setZoomButtonsVisible(true);
		renderer.setAntialiasing(true);
		renderer.setLegendTextSize(18);
		renderer.setLabelsTextSize(18);
		renderer.setLabelsColor(Color.WHITE);
		renderer.setDisplayValues(true);

		Intent intent = ChartFactory.getPieChartIntent(context, series, renderer, "Pie");
		return intent;
	}
}
