/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mvc;

/**
 *
 * @author Andrey
 */
import modelo.MiniPC;
import vista.View;
import controlador.Controlador;

public class Main {
    /**
     * Metodo princial que inicia la aplicacion
     * Entrada: @param args argumentos por medio de la UI
     * Salida: lanza la UI
     * Restricciones: No posee restricciones
     * Objetivo: inicializar el modelo, vista y controlador para establecer
     *          comunicion del patron mvc
     */
    public static void main(String[] args) {
        MiniPC modelo = new MiniPC(100);
        View view = new View();
        Controlador controlador = new Controlador(modelo, view);
        view.setVisible(true);
    }
}
