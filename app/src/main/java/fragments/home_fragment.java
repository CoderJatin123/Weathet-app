package fragments;

import static UtilityClass.Reasourse.getResoursc;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;

import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.lad.weatherlty.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;


import Model.ChartTempData;
import Model.WeatherModel;
import Repository.Repository;
import ViewModel.WeatherViewmodel;


public class home_fragment extends Fragment implements Repository.RefreshBar {

    View view;
    private ImageView mainWeatherIcon;
    private LineChart temp_humidity_line_chart;
    private LottieAnimationView refresh_bar;
    private TextView temp_view,feels_like_temp,city_view,sunrise,sunset,max_temp,min_temp,humidity,uv_index,wind_speed,last_update;
    private WeatherViewmodel weatherViewmodel;


    public static home_fragment getHome_Fragment_Instance(){

        home_fragment my_home_fragment=new home_fragment();
        return my_home_fragment;
    }

    public home_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.fragment_home, container, false);
        init();
        Repository.setRefreshInterface(this);

        weatherViewmodel= new ViewModelProvider(this).get(WeatherViewmodel.class);
        weatherViewmodel.setContext(this.getContext());

        refresh_bar=view.findViewById(R.id.refresh_bar);


        weatherViewmodel.getWeather_info().observe(this.getViewLifecycleOwner(), new Observer<List<WeatherModel>>() {
            @Override
            public void onChanged(List<WeatherModel> weatherModels) {

                if(weatherModels.size() > 0) {
                    WeatherModel temp_weather = weatherModels.get(0);
                    update_weather_view(temp_weather);
                }
            }
        });

        weatherViewmodel.getLive_temp_list().observe(this.getViewLifecycleOwner(), new Observer<List<ChartTempData>>() {
            @Override
            public void onChanged(List<ChartTempData> chartTempData) {



                if(chartTempData.size()>0) {

                    LineDataSet temp_dataset = new LineDataSet(Temperature_Value(chartTempData), "Temperature");
                    temp_dataset.setColors(R.color.primary_light);
                    temp_dataset.setCircleColor(R.color.primary_light);
                    temp_dataset.setCircleHoleColor(R.color.primary_light);
                    temp_dataset.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return String.valueOf(Math.round(value));


                        }
                    });


                    ArrayList<LineDataSet> myDataset = new ArrayList<>();
                    myDataset.add(temp_dataset);

                    LineData data = new LineData(temp_dataset);

                    temp_humidity_line_chart.setData(data);
                    temp_humidity_line_chart.getXAxis().setLabelCount(9);
                    temp_humidity_line_chart.invalidate();
                    temp_humidity_line_chart.getXAxis().setDrawAxisLine(false);
                    Description myDescripition=new Description();
                    myDescripition.setText("");
                    temp_humidity_line_chart.setDescription(myDescripition);

                    XAxis xAxis=temp_humidity_line_chart.getXAxis();
                    xAxis.setValueFormatter(new myXformatter());

                    temp_humidity_line_chart.getAxisRight().setDrawGridLines(false);
                    temp_humidity_line_chart.getAxisLeft().setDrawGridLines(false);
                    temp_humidity_line_chart.getXAxis().setDrawGridLines(false);
                    temp_humidity_line_chart.getAxisRight().setDrawAxisLine(false);
                    temp_humidity_line_chart.getAxisLeft().setDrawAxisLine(false);
                    temp_humidity_line_chart.setSelected(false);


                }
            }
        });

        return view;
    }
    void init(){
        temp_view=view.findViewById(R.id.temperature_view);
        feels_like_temp=view.findViewById(R.id.feelsl_like);
        city_view=view.findViewById(R.id.city_view);
        sunrise=view.findViewById(R.id.sunrise);
        sunset=view.findViewById(R.id.sunset);
        max_temp=view.findViewById(R.id.max_temp);
        min_temp=view.findViewById(R.id.min_temp);
        humidity=view.findViewById(R.id.humidity);
        uv_index=view.findViewById(R.id.uv_index);
        wind_speed=view.findViewById(R.id.wind_speed);
        last_update=view.findViewById(R.id.last_update);
        temp_humidity_line_chart=view.findViewById(R.id.line_temp_chart);
        mainWeatherIcon=view.findViewById(R.id.mainWeatherIcon);

    }

    @SuppressLint("SetTextI18n")
    void update_weather_view(WeatherModel temp_weather){

       String Iconname=temp_weather.getWeather_code();
        if(Iconname!=""){
            int Icon=getResources().getIdentifier(getResoursc(Iconname),"drawable",getActivity().getPackageName());
            mainWeatherIcon.setBackgroundResource(Icon);
        }

        refresh_bar.setVisibility(View.GONE);
        temp_view.setText(Math.round(temp_weather.getTemperature()) +" ⁰C");
        feels_like_temp.setText("Feels like "+ Math.round(temp_weather.getFeels_like_temp()) +" ⁰C");
        city_view.setText(temp_weather.getCityName());
        sunrise.setText(temp_weather.getSunrise());
        sunset.setText(temp_weather.getSunset());
        humidity.setText(temp_weather.getHumidity() +" %");
        uv_index.setText(String.valueOf(temp_weather.getUv_index()));
        wind_speed.setText(Math.round((temp_weather.getWind_speed()))+" Km/h");
        max_temp.setText(String.valueOf(Math.round(temp_weather.getMax_temp())));
        min_temp.setText(String.valueOf(Math.round(temp_weather.getMin_temp())));

        String last_updated_day="";

        Date last_up=new Date(temp_weather.getReqTime()*1000);
        DateFormat obj = new SimpleDateFormat("hh:mm aa");

        Date curr_time=new Date();

        if(curr_time.getDate() == last_up.getDate()){

            last_updated_day="Today "+obj.format(last_up);

        }

        else if(curr_time.getDate()==  new Date(last_up.getTime()+1000*60*60*24).getDate()){

            last_updated_day="Yesterday "+obj.format(last_up);
            //Log.d("DATE", "onChanged:  Yestredaya");

        }
        else if(curr_time.before(new Date(last_up.getTime()+1000*60*60*24*7))){

            DateFormat day=new SimpleDateFormat("E");
            last_updated_day=day.format(last_up);
        }
        else
        {
            last_updated_day="";
        }

        last_update.setText(last_updated_day);


    }


    private ArrayList<Entry> Temperature_Value(List<ChartTempData> chart){

        ArrayList<Entry> dataVals=new ArrayList<Entry>();

        SimpleDateFormat format=new SimpleDateFormat("H");

        int currHour=Integer.valueOf(format.format(new Date()));

        int c=0;
        while(!(currHour<c)){
            c+=3;
        }
        c-=3;

        for(int i=c; i<24; i+=2){
            dataVals.add(new Entry(i, (float) chart.get(0).getTemp_list().get(i).getTempC()));
        }
        for(int i=0; i<=c; i+=2){
            dataVals.add(new Entry(i+24, (float) chart.get(1).getTemp_list().get(i).getTempC()));
        }
        return dataVals;
    }

    @Override
    public void onRefreshBar(Boolean isRefresh) {
        if(isRefresh){
            refresh_bar.setVisibility(View.VISIBLE);
            refresh_bar.setAnimation(R.raw.loading);
            refresh_bar.playAnimation();
            refresh_bar.setRepeatCount(Animation.INFINITE);
        }
        else{
            refresh_bar.setVisibility(View.INVISIBLE);
        }
    }


    private class myXformatter extends IndexAxisValueFormatter{
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            int Hour = Math.round(value);


            if(Hour<23) {
                if (Hour < 12) {

                    if(Hour==0){
                        return "12am";
                    }
                    else{
                    return Hour + "am";
                    }
                } else if (Hour > 12) {
                    return (Hour - 12) + "pm";
                } else {
                    return "12pm";
                }
            }
            else {
                Hour = Hour - 24;

                if (Hour < 12) {

                    if (Hour == 0) {
                        return "12am";
                    } else {
                        return Hour + "am";
                    }
                } else if (Hour > 12) {
                    return (Hour - 12) + "pm";
                } else {
                    return "12pm";
                }
            }
        }
    }



}