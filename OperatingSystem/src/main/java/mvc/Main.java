/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mvc;

import modelo.MiniPC;
import vista.View;
import vista.BCPview;
import vista.Estadistica;
import controlador.Controlador;

public class Main {

    public static void main(String[] args) {
        MiniPC modelo = new MiniPC(512,512);
        View view = new View();
        BCPview bcpview = new BCPview();
        Estadistica estadistica = new Estadistica();
        Controlador controlador = new Controlador(modelo, view,bcpview,estadistica);
        view.setVisible(true);
    }
}
