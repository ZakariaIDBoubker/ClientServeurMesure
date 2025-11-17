package com.mycompany.serveurtr;

import java.io.*;
import java.net.Socket;
import java.sql.*;

public class Communication extends Thread {
    private Socket clientSocket;

    public Communication(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);

            // Lire login envoyé par le client
            String role = br.readLine();
            String password = br.readLine();

            System.out.println("Tentative connexion: role=" + role + " pwd=" + password);

            int idClient = Authentifier(role, password);
            if (idClient == -1) {
                pw.println("AUTH_FAILED");
                return;
            }

            pw.println("AUTH_OK");
            pw.println(idClient);

            System.out.println("Client authentifié id=" + idClient);

            // USER
            if (role.equalsIgnoreCase("user")) {

                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(";");

                    double tmp = Double.parseDouble(parts[0]);
                    double hmd = Double.parseDouble(parts[1]);
                    double prs = Double.parseDouble(parts[2]);

                    Mesure m = new Mesure(idClient, tmp, hmd, prs);
                    m.AjoutMesure();

                    pw.println("MESURE_BIEN_AJOUTEE");
                }

            } else {
                // ADMIN
                while (true) {
                    String input = br.readLine();
                    if (input == null) break;

                    int choix = Integer.parseInt(input);
                    if (choix == 5) break;

                    Connection conn = null;

                    try {
                        conn = DatabaseConnection.getConnection();

                        switch (choix) {

                            case 1 -> {
                                Statement st = conn.createStatement();
                                ResultSet rs = st.executeQuery("SELECT * FROM public.\"Mesure\" LIMIT 10");

                                while (rs.next()) {
                                    pw.println(
                                        "Id Client: " + rs.getInt("id_Client") +
                                        " | Temp=" + rs.getDouble("temperature") +
                                        " | Hmd=" + rs.getDouble("himidite") +
                                        " | Prs=" + rs.getDouble("pression") +
                                        " | Date=" + rs.getTimestamp("date")
                                    );
                                }
                            }

                            case 2 -> {
                                int idMesure = Integer.parseInt(br.readLine());
                                PreparedStatement ps = conn.prepareStatement(
                                        "SELECT * FROM public.\"Mesure\" WHERE \"id_Mesure\"=?");
                                ps.setInt(1, idMesure);

                                ResultSet rs = ps.executeQuery();
                                if (rs.next()) {
                                    pw.println(
                                        "Id Client: " + rs.getInt("id_Client") +
                                        " | Temp=" + rs.getDouble("temperature") +
                                        " | Hmd=" + rs.getDouble("himidite") +
                                        " | Prs=" + rs.getDouble("pression") +
                                        " | Date=" + rs.getTimestamp("date")
                                    );
                                } else {
                                    pw.println("Aucune mesure trouvée !");
                                }
                            }

                            case 3 -> {
                                Statement st = conn.createStatement();
                                ResultSet rs = st.executeQuery(
                                        "SELECT AVG(temperature) AS avgTmp, AVG(himidite) AS avgHmd, AVG(pression) AS avgPrs FROM public.\"Mesure\"");

                                if (rs.next()) {
                                    pw.println("Temp moyenne: " + rs.getDouble("avgTmp"));
                                    pw.println("Humidité moyenne: " + rs.getDouble("avgHmd"));
                                    pw.println("Pression moyenne: " + rs.getDouble("avgPrs"));
                                }
                            }

                            case 4 -> {
                                String capteur = br.readLine();

                                String colonne = switch (capteur.toLowerCase()) {
                                    case "temperature", "tmp" -> "temperature";
                                    case "humidite", "hmd" -> "himidite";
                                    case "pression", "prs" -> "pression";
                                    default -> null;
                                };

                                if (colonne == null) {
                                    pw.println("Capteur invalide !");
                                    break;
                                }

                                ResultSet rs = conn.createStatement().executeQuery(
                                        "SELECT AVG(" + colonne + ") AS val FROM public.\"Mesure\""
                                );

                                if (rs.next()) {
                                    pw.println("Moyenne de " + colonne + " = " + rs.getDouble("val"));
                                }
                            }
                        }

                    } catch (Exception e) {
                        pw.println("Erreur SQL: " + e.getMessage());
                    }

                    pw.println("END");
                }
            }

        } catch (IOException e) {
            System.out.println("Erreur client: " + e.getMessage());
        }

        System.out.println("Client déconnecté");
    }

    // Fonction authentification SQL
    private int Authentifier(String role, String pwd) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT \"id_Client\" FROM public.\"Client\" WHERE role=? AND password=?");

            ps.setString(1, role);
            ps.setString(2, pwd);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_Client");
            }

        } catch (Exception e) { return -1; }

        return -1;
    }
}
