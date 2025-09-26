/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.io.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
/**
 *
 * @author Andrey
 */
public class Utilidades {
    //abre un archivo y lo retorna
    /**
     * Entrada: usuario selecciona una archivo desde ui
     * Salida: el objeto con la ruta del archivo seleccionado
     * Restricciones: no posee restricciones
     * Objetivo: permitir al usuario escoger un archivo
     */
    public static File seleccionarArchivo(){
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos ASM", "asm"));
        int selecionado = fc.showOpenDialog(null);
        if(selecionado == JFileChooser.APPROVE_OPTION){
            return fc.getSelectedFile();
        }
        return null;
    }
    /**
     * Entrada: un objeto de tipo file para leer
     * Salida: una lista de string
     * Restricciones: no posee restricciones
     * Objetivo: cargar el contenido del archivo en una lista 
     *          para hacer más facil la lectura
     */
    public static List<String> leerArchivo(File archivo)throws IOException {
        List<String> lineas = new ArrayList<>();
        try(BufferedReader br = new BufferedReader (new FileReader(archivo))){
            String linea;
            while((linea = br.readLine())!= null){
                lineas.add(linea.trim());
            }
        }
        return lineas;
    }
  
   
}
