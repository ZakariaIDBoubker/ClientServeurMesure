package com.mycompany.clienttr;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientTr {

    public static void main(String[] args) {

        try {
            Socket s = new Socket("127.0.0.1", 1234);
            PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

            Scanner sc = new Scanner(System.in);

            System.out.print("Rôle (admin/user) : ");
            String role = sc.nextLine();

            System.out.print("Mot de passe : ");
            String pwd = sc.nextLine();

            pw.println(role);
            pw.println(pwd);

            String auth = br.readLine();
            if (auth.equals("AUTH_FAILED")) {
                System.out.println("Erreur authentification !");
                return;
            }

            int idClient = Integer.parseInt(br.readLine());
            System.out.println("Authentifié avec succès id=" + idClient);

            if (role.equalsIgnoreCase("user")) {

                while (true) {
                    Mesure m = new Mesure(idClient);

                    pw.println(m.getTmp() + ";" + m.getHmd() + ";" + m.getPrs());

                    String ack = br.readLine();
                    System.out.println("Serveur : " + ack);

                    Thread.sleep(3000);
                }

            } else {

                while (true) {
                    System.out.println("\n--- MENU ADMIN ---");
                    System.out.println("1. Afficher 10 mesures");
                    System.out.println("2. Mesure par id");
                    System.out.println("3. Moyenne tous capteurs");
                    System.out.println("4. Moyenne d’un capteur (temp/hum/pres)");
                    System.out.println("5. Quitter");
                    System.out.print("Choix : ");
                    int choix = sc.nextInt();
                    sc.nextLine();

                    pw.println(choix);

                    if (choix == 5) break;

                    if (choix == 2) {
                        System.out.print("ID Mesure : ");
                        pw.println(sc.nextInt());
                        sc.nextLine();
                    }

                    if (choix == 4) {
                        System.out.print("Choisir capteur (temperature / humidite / pression) : ");
                        String cap = sc.nextLine();
                        pw.println(cap);
                    }

                    String line;
                    while (!(line = br.readLine()).equals("END")) {
                        System.out.println(line);
                    }
                }

                System.out.println("Déconnexion...");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
