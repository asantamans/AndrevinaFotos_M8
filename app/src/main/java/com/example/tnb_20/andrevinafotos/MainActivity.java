package com.example.tnb_20.andrevinafotos;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

   public static int numeroAdivinar;
    int intentJug=0;

    String nomJug="";
    static List<Jugador> arrIntent = new ArrayList<Jugador>();

    static final int REQUEST_IMAGE_CAPTURE = 1;
    File dir = new File("data"+File.separator+"data"+File.separator+"com.example.tnb_20.endevinaNumero"+File.separator+"photos");
    File img;
    boolean fotoFeta;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        fotoFeta=false;
        //Comprobem si tenim carregades les dades;  nomes s'executa al inici de la app
        if (arrIntent.size()==0){
            carregarDades();
        }

        //si no esta la ruta de les fotos creada, la creem per evitar crash per ruta no trobada
        if(!dir.exists()){
            dir.mkdir();
        }

        setContentView(R.layout.activity_main);
        generarRandom(new Random().nextInt(99)+1);

        final EditText editText = findViewById(R.id.editTextNumeroAdivinar);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    adivinarNumero(editText);
                    return true;
                }
                return false;
            }
        });



        //Boto per adivinar
        final Button button = findViewById(R.id.btnAdivinar);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!editText.getText().toString().equals("")){
                    intentJug++;
                    adivinarNumero(editText);
                }
            }
        });



        //Boto per iniciar el fame activity, falta comprovar que hi hagi registres previs i si no, surti Toast error
        final Button bRanking = findViewById(R.id.btnRanking);
        final Intent intent = new Intent();
        bRanking.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FameActivity.class);
                startActivity(intent);
            }
        });

    }

    public void adivinarNumero(EditText editText){
        Context context = getApplicationContext();
        CharSequence text=null;
        //Comparativa del numero
        if (Integer.parseInt(editText.getText().toString())> numeroAdivinar){
            text = "El numero ha de ser mes petit ";
        }else if(Integer.parseInt(editText.getText().toString())< numeroAdivinar){
            text = "El numero ha de ser mes gran ";
        }else {
            text = "Felicitats, has encertat el numero";
            //Generem dialog per a registrar el nom del jugador, asociem els intents a un nom
            //!Cal acabar de cuadrar les dimensions
            dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.dialogview);
            dialog.setTitle("Registre de jugador");

            //imgView necesaria per a colocar la foto
            final ImageView photo = dialog.findViewById(R.id.imageView);

            /*
            Posarem 2 botons; 1 per la camara i 1 per continuar
            Es necesari que haguem fet una foto per continaur
            */
            Button btnCamera = dialog.findViewById(R.id.btnCamera);
            Button button = dialog.findViewById(R.id.ok);

            //Accio del boto de camera
            btnCamera.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            });
            //listener boto de continuar amb la premisa esmentada anteriorment
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    EditText edit = dialog.findViewById(R.id.editTextNom);
                    nomJug = edit.getText().toString();
                    if (!nomJug.equals("") && fotoFeta) {
                        dialog.dismiss();
                        arrIntent.add(new Jugador(intentJug, nomJug, Uri.fromFile(img)));
                        nomJug = "";
                        fotoFeta=false;
                        intentJug = 0;
                        generarRandom(new Random().nextInt(99)+1);
                        Collections.sort(arrIntent);
                        guardarDades();
                    }else{
                        //Mostrem un missatge emergent en cas que no hagi fet la foto o no tingui nom (o ambdos inclosos)
                        Toast toast = Toast.makeText(getApplicationContext(), "Error: Has de ferte una foto i introduir el teu nom", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            });

            dialog.show();
        }
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }



    public void generarRandom(int num){
        //Funcio per asignar un nou numero a adivinar un cop finalitzi la partida
        numeroAdivinar=num; }



    //camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            fotoFeta=true;

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView iv = dialog.findViewById(R.id.imageView);
            iv.setImageBitmap(imageBitmap);

            OutputStream os = null;
            try {
                //Guardem les imatges dels Jugadors amb un numero i aixi podrem quantificar cuants jugadors tindrem
                //Tambe les podem guardar amb el nom del jugador, pero podria causar problemes de persistencia
                //SI hi hagues dos jugadors amb mateix nom
                if (dir.list().length>0){
                    img = new File(dir, (Integer.parseInt(dir.listFiles()[dir.listFiles().length-1].getName().substring(0,dir.listFiles()[dir.listFiles().length-1].getName().length()-4))+1) + ".png");
                }else{
                    img = new File(dir, "1.png");
                }
                os = new FileOutputStream(img);
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            } catch(IOException e) {
                System.out.println("Error: Hi ha hagut un problema al guardar la imatge");
            }
        }

    }

    //Funcions que ens garanteixen la persistencia de l'aplicacio
    private void carregarDades(){
        try{
            //Nom fitxer de guardar dades = registresGuardats.txt
            BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput("registresGuardats.txt")));
            String tmp;
            while((tmp = br.readLine())!=null){
                arrIntent.add(new Jugador(Integer.parseInt(tmp.split(";")[0]),tmp.split(";")[1], Uri.parse(tmp.split(";")[2])));
            }
            br.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void guardarDades(){
        try {
            //Nom fitxer de guardar dades = registresGuardats.txt
            OutputStreamWriter osw = new OutputStreamWriter(openFileOutput("registresGuardats.txt",Context.MODE_PRIVATE));
            for (int i=0; i<arrIntent.size(); i++){
                osw.write(arrIntent.get(i).intent+";"+arrIntent.get(i).nom+";"+arrIntent.get(i).photoPath.toString());
                //Concatenem sempre el text i fem salt de linia,si no, estariem sobrescribint registres antics (PROBAT)
                osw.append("\r\n");
            }
            osw.close();

        } catch (Exception  e) {
            e.printStackTrace();
        }
    }
}
