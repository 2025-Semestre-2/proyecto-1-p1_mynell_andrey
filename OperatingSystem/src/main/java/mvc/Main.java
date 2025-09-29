/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mvc;

import modelo.SistemaOperativo;
import vista.View;
import vista.Estadistica;
import controlador.Controlador;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        /*String[] a = "MOV ".split(" ");
        if (a[1].isBlank()){
            System.out.println("Nada");
        }else{
        System.out.println(Arrays.toString(a));}¨*/
        
        SistemaOperativo modelo = new SistemaOperativo();
        View view = new View();
     
        Estadistica estadistica = new Estadistica();
        Controlador controlador = new Controlador(modelo, view,estadistica);
        view.setVisible(true);
    }
}
