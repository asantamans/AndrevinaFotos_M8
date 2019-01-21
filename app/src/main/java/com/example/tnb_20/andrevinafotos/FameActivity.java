package com.example.tnb_20.andrevinafotos;



import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;


public class FameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fame);


        ArrayAdapter<Jugador> arrAdapter = new ArrayAdapter<Jugador>(this, R.layout.activity_fame, MainActivity.arrIntent){
            @Override
            public View getView(int pos, View convertView, ViewGroup container)
            {
                /*
                Volquem tota la informació dels jugadors i les fotos en un view
                Fem un layout i hi plasmem els valors del Array de Jugadors
                aixi com la seva foto
                */
                if( convertView==null ) {
                    convertView = getLayoutInflater().inflate(R.layout.foto_layout, container, false);
                }
                ((TextView) convertView.findViewById(R.id.name)).setText(getItem(pos).nom);
                ((TextView) convertView.findViewById(R.id.tries)).setText(Integer.toString(getItem(pos).intent));
                ((ImageView) convertView.findViewById(R.id.imgView)).setImageURI(getItem(pos).photoPath);
                return convertView;
            }
        };

        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(arrAdapter);
    }
}
