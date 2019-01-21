package com.example.tnb_20.andrevinafotos;

import android.net.Uri;
public class Jugador implements Comparable<Jugador>{

    public int intent;
    public String nom;
    public Uri photoPath;

    public Jugador(int intent, String nom, Uri photoPath){
        this.intent=intent;
        this.nom=nom;
        this.photoPath=photoPath;
    }

    @Override
    public int compareTo(Jugador Jugador){
        return this.intent-Jugador.intent;
    }

    @Override
    public String toString() {
        return "Jugador [Intents=" + intent + ", Nom=" + nom + "]";
    }

}
