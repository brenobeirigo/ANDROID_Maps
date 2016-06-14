package com.bbgo.appmapas;

import android.app.ActionBar;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

//Mais informações: https://developers.google.com/maps/documentation/android-api/utility/#introduction

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    //Objeto mapa
    private GoogleMap mMap;
    // Coordenadas torre eiffel
    private final LatLng eiffel = new LatLng(48.8584, 2.2945);

    // Coordenadas mur pour la paix
    private final LatLng mur = new LatLng(48.853361, 2.302081);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Botão para desenhar linha
        Button bDrawLine = (Button) findViewById(R.id.draw_line);
        bDrawLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PolylineOptions line = new PolylineOptions();
                line.add(eiffel);
                line.add(mur);
                line.color(Color.RED);
                Polyline polyline = mMap.addPolyline(line);
                polyline.setGeodesic(true);
            }
        });
        //Botão para mover a câmera
        Button bCamMove = (Button) findViewById(R.id.button_cam);
        bCamMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Muda posição da camera
                CameraPosition position = new CameraPosition.Builder()
                        .target(eiffel) //localização
                        .bearing(45) //rotação da câmera em graus
                        .tilt(90) //ângulo que a câmera está posicionada em graus
                        .zoom(17) //zoom
                        .build();

                //Cria objeto camera update
                CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
                //Move a câmera
                mMap.animateCamera(update, 5000, null);
            }
        });

    }

    @Override
    public void onMapClick(LatLng latlng) {
        Toast.makeText(this, "Clicou em " + latlng, Toast.LENGTH_SHORT).show();
        CameraUpdate update = CameraUpdateFactory.newLatLng(latlng);
        mMap.animateCamera(update);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setOnMapClickListener(this);

        mMap.addMarker(new MarkerOptions()
                .position(eiffel)
                .title("Torre Eiffel")
                .snippet("Bem-vindos à França!")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.eiffel_marker)));
        int zoom = 10;

        //Muda o estilo de exibição dos balões
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            //Muda a borda do balão
            @Override
            public View getInfoWindow(Marker marker) {
                LinearLayout linear = (LinearLayout) this.getInfoContents(marker);
                //Borda imagem 9-patch
                linear.setBackgroundResource(R.drawable.janela_marker);
                return linear;

            }

            //Muda o estilo do contéudo do balão
            @Override
            public View getInfoContents(Marker marker) {
                //Cria linear layout
                LinearLayout linear = new LinearLayout(getBaseContext());
                linear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                linear.setOrientation(LinearLayout.VERTICAL);

                //Cria um cabeçalho padrão para os balões
                TextView tHeader = new TextView(getBaseContext());
                tHeader.setText("View Customizada");
                tHeader.setTextColor(Color.BLUE);
                tHeader.setGravity(Gravity.CENTER);
                linear.addView(tHeader);

                //Muda as características do título
                TextView tTitle = new TextView(getBaseContext());
                tTitle.setText(marker.getTitle());
                tTitle.setTextColor(Color.RED);
                linear.addView(tTitle);

                //Muda as características do texto
                TextView tSnippet = new TextView(getBaseContext());
                tSnippet.setText(marker.getSnippet());
                tSnippet.setTextColor(Color.BLACK);
                linear.addView(tSnippet);
                return linear;
            }
        });
        //Evento de clique em marcador - janela fechada
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getBaseContext(), "Clicou no marcador " + marker.getTitle() + " > " + marker.getPosition(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        //Evento de clique em marcador - janela aberta (impossível adicionar botões: janela é uma imagem)
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(getBaseContext(), "Clicou na janela do marcador " + marker.getTitle() + " > " + marker.getPosition(), Toast.LENGTH_SHORT).show();
            }
        });

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eiffel, zoom));
    }
}
