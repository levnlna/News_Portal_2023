package com.example.newsportal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements CategoryAdapter.CategoryClicked {

    private RecyclerView rvNews, rvCategory;
    private ArrayList<Articles> articlesArrayList;
    private ArrayList<Category> categoryArrayList;
    private NewsAdapter newsAdapter;
    private CategoryAdapter categoryAdapter;

    private static final int LOCATION_REQUEST_CODE = 1;
    private TextView userLocation, userAddress, userLatitude, userLongitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //user location
        userLocation = findViewById(R.id.userLocation);
        userAddress = findViewById(R.id.userAddress);
        userLatitude = findViewById(R.id.userLatitude);
        userLongitude = findViewById(R.id.userLongitude);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //check permission
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }else{
            //permission granted
            getCurrLocation();
        }

        //recycler view category
        rvCategory = findViewById(R.id.rvCategory);
        categoryArrayList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoryArrayList, this, this::onCategoryClick);

        rvCategory.setAdapter(categoryAdapter);

        //recycler view news
        rvNews = findViewById(R.id.rvNews);
        articlesArrayList = new ArrayList<>();
        newsAdapter = new NewsAdapter(articlesArrayList, this);

        rvNews.setLayoutManager(new GridLayoutManager(this, 2));
        rvNews.setAdapter(newsAdapter);

        //get data
        getCategory();

        getNews("All");
        newsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_REQUEST_CODE && grantResults.length>0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrLocation();
            }else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrLocation(){
        LocationRequest locationRequest;
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(500)
                .setMaxUpdateDelayMillis(1000)
                .build();

        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback(){

                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .removeLocationUpdates(this);
                        if(locationResult != null && locationResult.getLocations().size() > 0){
                            int latestLocationIndex = locationResult.getLocations().size() -1;
                            double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                            String locality = "";
                            String subArea = "";
                            String address = "";

                            //get detail location from lat and long
                            try {
                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                                locality = addresses.get(0).getLocality();
                                subArea = addresses.get(0).getSubAdminArea();
                                address = addresses.get(0).getAddressLine(0);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //set location data
                            userLocation.setText(String.format("%s, %s", locality, subArea));
                            userAddress.setText(String.format("%s", address));
                            userLatitude.setText(String.format("%s , ", latitude));
                            userLongitude.setText(String.format("%s", longitude));
                        }
                    }
                }, Looper.getMainLooper());
    }

    private void getNews(String category){
        //get news from Indonesia (code : id)
        String url = "https://newsapi.org/v2/top-headlines?country=id&apiKey=3f85d5e0fc894a1e8fc22c6f9494138a";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://newsapi.org/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        Call<News> call;

        if (category.equals("All")){
            call = retrofitInterface.getNews(url);
        }else{
            articlesArrayList.clear();
            url = "https://newsapi.org/v2/top-headlines?country=id&category="+category.toLowerCase()+"&apiKey=3f85d5e0fc894a1e8fc22c6f9494138a";
            call = retrofitInterface.getNewsByCategory(url);
        }


        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                News news = response.body();
                ArrayList<Articles> articles = news.getArticles();
                for (int i = 0; i < articles.size(); i++){
                    articlesArrayList.add(new Articles(articles.get(i).getAuthor(), articles.get(i).getTitle(), articles.get(i).getDescription(), articles.get(i).getUrl(), articles.get(i).getUrlToImage(), articles.get(i).getPublishedAt(), articles.get(i).getContent()));
                }
                newsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Oh no we can't get the news", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCategory(){
        categoryArrayList.add(new Category("All"));
        categoryArrayList.add(new Category("Business"));
        categoryArrayList.add(new Category("Entertainment"));
        categoryArrayList.add(new Category("General"));
        categoryArrayList.add(new Category("Health"));
        categoryArrayList.add(new Category("Science"));
        categoryArrayList.add(new Category("Sports"));
        categoryArrayList.add(new Category("Technology"));
        categoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCategoryClick(int position) {
        String category = categoryArrayList.get(position).getCategoryName();
        getNews(category);
    }
}