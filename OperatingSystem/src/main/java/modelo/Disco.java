/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package modelo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Andrey
Clase Disco, que representa la disco principal de la minipc
Objetivo: Almcenar las intrucciones en la posicion indicada
 */
public class Disco {
    private final File discoFile;
    private final int indices= 50;
    private final int TAMANO;
    
    /**
     * Contructor que inicializa el tamaño de la disco
     */
    public Disco(String rutaDisco, int tamano)throws IOException{
        this.discoFile = new File(rutaDisco);
        this.TAMANO = tamano;
        if (!discoFile.exists()) inicializarArchivo();
    }
    private void inicializarArchivo() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(discoFile))) {
            for (int i = 0; i < indices; i++) {
                bw.write("\n");
            }
        }
    }
    
    public List<String> leerTodo() throws IOException {
        List<String> lineas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(discoFile))) {
            String l;
            while ((l = br.readLine()) != null) {
                lineas.add(l);
            }
        }
        return lineas;
    }
    
    public List<String> getDatos() throws IOException{
        List<String> lista = leerTodo();
        return lista.subList(50, lista.size());
    }
    
    private void escribirTodo(List<String> lineas) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(discoFile))) {
            for (String l : lineas) {
                bw.write(l);
                bw.newLine();
            }
        }
    }
    
    public List<String> listarArchivos() throws IOException {
        List<String> lineas = leerTodo();
        List<String> archivos = new ArrayList<>();
        for (int i = 0; i < indices && i < lineas.size(); i++) {
            String linea = lineas.get(i).trim();
            if (!linea.isEmpty()) {
                archivos.add(linea.split("\\|")[0]);
            }
        }
        return archivos;
    }
    
    public void crearArchivo(String nombre, List<String> contenido) throws IOException {
        List<String> lineas = leerTodo();
        
        int datosActuales = Math.max(0, lineas.size() - indices);
        if (datosActuales + contenido.size() > TAMANO-indices) {
            throw new IOException("Espacio insuficiente en el disco");
        }
        
        int posicionInicio = Math.max(lineas.size(), indices);
        int longitud = contenido.size();
        while (lineas.size() < indices) lineas.add("");
        lineas.addAll(contenido);
        boolean agregado = false;
        for (int i = 0; i < indices; i++) {
            if (i >= lineas.size()) lineas.add("");
            if (lineas.get(i).trim().isEmpty()) {
                lineas.set(i, nombre + "|" + posicionInicio + "|" + longitud);
                agregado = true;
                break;
            }
        }
        if (!agregado) throw new IOException("Índice lleno (las primeras 50 líneas están ocupadas)");
        escribirTodo(lineas);
    }
    
    public List<String> leerArchivo(String nombre) throws IOException {
        List<String> lineas = leerTodo();
        for (int i = 0; i < indices && i < lineas.size(); i++) {
            String l = lineas.get(i).trim();
            if (l.startsWith(nombre + "|")) {
                String[] parts = l.split("\\|");
                int inicio = Integer.parseInt(parts[1]);
                int longitud = Integer.parseInt(parts[2]);
                List<String> contenido = new ArrayList<>();
                for (int j = inicio - 1; j < inicio - 1 + longitud && j < lineas.size(); j++) {
                    contenido.add(lineas.get(j));
                }
                return contenido;
            }
        }
        throw new IOException("Archivo no encontrado");
    }
    
    public void eliminarArchivo(String nombre) throws IOException {
        List<String> lineas = leerTodo();
        for (int i = 0; i < indices && i < lineas.size(); i++) {
            if (lineas.get(i).startsWith(nombre + "|")) {
                lineas.set(i, "");
                break;
            }
        }
        escribirTodo(lineas);
    }
    
    public void setDisco (int pos, String contenido){
        try {
            List<String> disco = leerTodo();
            disco.set(pos, contenido);
            escribirTodo(disco);
        } catch (IOException ex) {
            System.getLogger(Disco.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
    }
    
    public String getDisco(int pos){
        try {
            return leerTodo().get(pos);
        } catch (IOException ex) {
            System.getLogger(Disco.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return null;
    }
    
    public void ClearAll(){
        try {
            escribirTodo(List.of());
        } catch (IOException ex) {
            System.getLogger(Disco.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    public int size() {
        return TAMANO;
    }
}
