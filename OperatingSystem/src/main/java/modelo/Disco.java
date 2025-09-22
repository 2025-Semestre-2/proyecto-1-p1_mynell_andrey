/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package modelo;

/**
 *
 * @author Andrey
Clase Disco, que representa la disco principal de la minipc
Objetivo: Almcenar las intrucciones en la posicion indicada
 */
public class Disco {
    private String[] disco;
    /**
     * Contructor que inicializa el arreglo de la disco
     */
  
    public Disco(String[] disco) {
        this.disco = disco;
    }
    
    /**
     * Contructor que inicializa el tamaño de la disco
     */
    public Disco(int size){
        disco = new String[size];
    }
    /**
     * Asigna un valor a una posicion en disco
     * Entrada: 
     * @param pos posicion en disco
     * @param valor instruccion a almecenar
     * Salida: No tiene
     * Restricciones No tiene
     * Objetivo: Guardar a la instruccion en una posicion en disco
     */
    public void setDisco(int pos, String valor) {
        disco[pos] = valor;
    }
    /**
     * Retorna un valor a una posicion en disco
     * Entrada: 
     * @param pos posicion en disco
     * @param valor instruccion a almecenar
     * Salida: instruccion almacenada en la posicion x
     * Restricciones No tiene
     * Objetivo: Ejecutar a la instruccion en una posicion en disco
     */
    public String getDisco(int pos) {
        return disco[pos];
    }
    
    /**
     * Salida:
     * @return tamaño de la disco
     */
    public int size() {
        return disco.length;
    }
}
