package com.mycompany.clienttr;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Mesure implements Serializable {
    private static final long serialVersionUID = 1L;

    private double tmp;
    private double hmd;
    private double prs;
    private LocalDateTime dateMesure;
    private int idClient;

    public Mesure(int idClient, double tmp, double hmd, double prs) {
        this.tmp = tmp;
        this.hmd = hmd;
        this.prs = prs;
        this.dateMesure = LocalDateTime.now();
        this.idClient = idClient;
    }

    // Constructeur utilitaire si besoin côté client
    public Mesure(int idClient) {
        java.util.Random r = new java.util.Random();
        this.tmp = 20 + r.nextDouble() * 10;
        this.hmd = 30 + r.nextDouble() * 50;
        this.prs = 900 + r.nextDouble() * 200;
        this.dateMesure = LocalDateTime.now();
        this.idClient = idClient;
    }

    public int getIdClient() { 
        return idClient; 
    }
    
    public double getTmp() { 
        return tmp; 
    }
    
    public double getHmd() { 
        return hmd; 
    }
    
    public double getPrs() { 
        return prs; 
    }
    
    public LocalDateTime getDateMesure() { 
        return dateMesure; 
    }

    @Override
    public String toString() {
        return "Client " + idClient + " | Temp=" + tmp + "°C | Hmd=" + hmd + "% | Prs=" + prs + " hPa | Date=" + dateMesure;
    }
}

