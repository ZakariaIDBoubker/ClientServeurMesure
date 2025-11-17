package com.mycompany.serveurtr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Mesure {
    private double tmp;
    private double hmd;
    private double prs;
    private LocalDateTime dateMesure;
    private int idClient;

    public Mesure(int idClient, double tmp, double hmd, double prs) {
        this.idClient = idClient;
        this.tmp = tmp;
        this.hmd = hmd;
        this.prs = prs;
        this.dateMesure = LocalDateTime.now();
    }

    public void AjoutMesure() {
        String sql = "INSERT INTO public.\"Mesure\" (\"id_Client\", temperature, himidite, pression, date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idClient);
            ps.setDouble(2, tmp);
            ps.setDouble(3, hmd);
            ps.setDouble(4, prs);
            ps.setTimestamp(5, Timestamp.valueOf(dateMesure));
            ps.executeUpdate();
            System.out.println("Mesure ajoutée en base : " + this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Client " + idClient + " | Temp=" + tmp + "°C | Hmd=" + hmd + "% | Prs=" + prs + " hPa | Date=" + dateMesure;
    }
}
