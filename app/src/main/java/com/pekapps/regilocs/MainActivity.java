package com.pekapps.regilocs;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import android.provider.Settings;

import com.pekapps.regilocs.entity.Device;
import com.pekapps.regilocs.entity.Server;
import com.pekapps.regilocs.entity.ServerStatus;
import com.pekapps.regilocs.entity.User;

import java.net.URI;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };
    private static final int INITIAL_REQUEST=1337;
    String longitude = "";
    String latitude = "";
    String altitude = "";
    String nombre="";
    String Status="";
    String uuid="";
    Server server = new Server ("34.238.53.222","8080","DOWN");
    private static final String TAG = "Debug";
    private Boolean flag = false;
    private LocationManager locationMangaer=null;
    private LocationListener locationListener=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new isServerUp().execute();
        uuid = Secure.getString(this.getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        flag = displayGpsStatus();
        if (flag) {

            Log.v(TAG, "onClick");
            locationListener = new MyLocationListener();

           /* if (locationManager != null) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
                }
            }*/

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            String provider = locationManager.getBestProvider(criteria, true);
            if(provider != null) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        locationManager.requestLocationUpdates(provider,10, 10, locationListener);
                    } catch (Exception e) {
                        Log.e("MainActivity", e.getMessage(), e);
                    }
                }

            }

        } else {
            Log.e ("Gps Status!!", "Your GPS is: OFF");
        }

    }

    private boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
    }
    public void getUser(View view) {
        new getUserTask().execute();
    }

    public void registerUser(View view) {
        new registerUserTask().execute();
    }

    private class registerUserTask extends AsyncTask<Void, Void, User> {
        @Override
        protected User doInBackground(Void... params) {
            try {
                EditText editText = (EditText) findViewById(R.id.registerText);
                String userName = editText.getText().toString();
                final String url = "http://"+server.getIp()+":"+server.getPort()+"/users/";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Device d = new Device(uuid);
                User user = new User();
                user.setName(userName);
                user.setRole("user");
                ResponseEntity<User> response = restTemplate.postForEntity(url,user, User.class);
                String uri = response.getHeaders().getLocation().toString()+"/devices/";
                ResponseEntity<Device> responseD = restTemplate.postForEntity(uri,d, Device.class);
                Status = responseD.getStatusCode().toString();
                return user;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(User l) {
            TextView textView = findViewById(R.id.ResultSend);
            if ((Status!=null && Status.equals("200"))) {
                textView.setText("User & Device registered");
            }
            new isDeviceRegistered().execute();
        }
    }




    public void sendLocation(View view) {
        new SendkLocations().execute();
    }

    public void connectServer (View view) {
        testConnection();
    }

    private void testConnection() {
        EditText sT = (EditText) findViewById(R.id.serverText);
        server.setIp(sT.getText().toString());
        new isServerUp().execute();
    }


    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }

    private class getUserTask extends AsyncTask<Void, Void, User> {
        @Override
        protected User doInBackground(Void... params) {
            try {
                EditText editText = (EditText) findViewById(R.id.registerText);
                String idUser = editText.getText().toString();
                final String url = "http://"+server.getIp()+":"+server.getPort()+"/users/"+idUser;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                User user = restTemplate.getForObject(url, User.class);
                nombre=user.getName();
                return user;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            TextView textView = findViewById(R.id.ResultSend);
            textView.setText(nombre);
        }
    }


    private class isServerUp extends AsyncTask<Void, Void, Server> {
        @Override
        protected Server doInBackground(Void... params) {
            try {

                final String url = "http://"+server.getIp()+":"+server.getPort()+"/actuator/health";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ServerStatus up = restTemplate.getForObject(url, ServerStatus.class);
                server.setStatus(up.getStatus());
                return server;
            }  catch (RestClientException e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return server;
        }

        @Override
        protected void onPostExecute(Server server) {
            TextView textView = findViewById(R.id.ResultSend);
            textView.setText("Server status: " + server.getStatus()+ " at "+server.getIp() );
            if (server.getStatus().equals("UP")) {
                EditText sT = (EditText) findViewById(R.id.serverText);
                Button b = (Button) findViewById(R.id.connectButton);
                sT.setEnabled(false);
                b.setEnabled(false);
                new isDeviceRegistered().execute();
            }
        }
    }


    private class isDeviceRegistered extends AsyncTask<Void, Void, Device> {
        @Override
        protected Device doInBackground(Void... params) {
            Device device = new Device();
            try {
                final String url = "http://"+server.getIp()+":"+server.getPort()+"/devices/"+uuid;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                device = restTemplate.getForObject(url, Device.class);
                return device;
            } catch (HttpClientErrorException ex)   {
                if (ex.getStatusCode() != HttpStatus.NOT_FOUND) {
                    throw ex;
                }
            }

            catch (RestClientException e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return device;
        }

        @Override
        protected void onPostExecute(Device device) {
            if ((device.getId()!=null)&&(device.getId().equals(uuid))) {
                EditText sT = (EditText) findViewById(R.id.registerText);
                Button b = (Button) findViewById(R.id.registerButton);
                sT.setEnabled(false);
                b.setEnabled(false);
                TextView textView = findViewById(R.id.statusText);
                textView.setText("Device Registered id:" + uuid);
            } else {
                TextView textView = findViewById(R.id.statusText);
                textView.setText("Device Not Registered ");
            }

        }
    }

    private class SendkLocations extends AsyncTask<Void, Void, com.pekapps.regilocs.entity.Location> {
        @Override
        protected com.pekapps.regilocs.entity.Location doInBackground(Void... params) {
            try {
                EditText editText = (EditText) findViewById(R.id.registerText);
                String idUser = editText.getText().toString();
                final String url = "http://"+server.getIp()+":"+server.getPort()+"/devices/"+uuid+"/locations";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                com.pekapps.regilocs.entity.Location location = new com.pekapps.regilocs.entity.Location();
                location.setAltitude(altitude);
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                ResponseEntity<com.pekapps.regilocs.entity.Location> response = restTemplate.postForEntity(url,location, com.pekapps.regilocs.entity.Location.class);
                Status = response.getStatusCode().toString();
                return location;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(com.pekapps.regilocs.entity.Location l) {
            if ((Status!=null && Status.equals("201"))) {
                TextView textView = findViewById(R.id.ResultSend);
                textView.setText("Location sent OK");
                new isServerUp().execute();
            }

        }
    }

    /*----------Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            longitude = "Longitude: " +loc.getLongitude();
            Log.v(TAG, longitude);
            latitude = "Latitude: " +loc.getLatitude();
            Log.v(TAG, latitude);
            altitude = "Altitude: " + loc.getAltitude();

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }
}

