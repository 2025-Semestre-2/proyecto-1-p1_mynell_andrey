/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.*;
import javax.swing.JOptionPane;

/**
 *
 * @author Andrey
 */
public class MiniPC {
    private CPU cpu;
    private Memoria memoria;
    private List<String> instrucciones;
    
    /**
     * Constructor
     * Entrada: el tamaño de la memoria 
     * Salida: inicializar la minipc
     * Restricciones: no posee restricciones
     * Objetivo: crar una minipc con memoria del tamaño especifido
     */
    public MiniPC(int tamanno){
        cpu = new CPU();
       // memoria = new Memoria(100);
        memoria = new Memoria(tamanno);
        instrucciones = new ArrayList<>();
    }
    /**
     * Entrada: no recibe parametros
     * Salida: asigna la lista cargada
     * Restricciones no posee restricciones
     * Objetivo: acceder al conjuto de instrucciones cargadas
     * 
     */
    public void guardarInstrucciones(List<String> lista){
        instrucciones = lista;
    }
    /**
     * Entrada: no recibe parametros
     * Salida: retona la lista cargada
     * Restricciones no posee restricciones
     * Objetivo: acceder al conjuto de instrucciones cargadas
     * 
     */
    public List<String> getIntr(){
        System.out.println(instrucciones);
        return instrucciones;
    }   
     /**
     * Entrada: no recibe parametros
     * Salida: indica la posicion donde arranca el pc
     * Restricciones: el 20 al 100
     * Objetivo: establecer el pc en una posicion aleatoria
     */
    public void inicializarPC(){
        cpu.reset();
        Random rand = new Random();
        //posicion inical donde empieza a guardase las intrucciones
        int pos = rand.nextInt(memoria.size()-20+1) + 20;
        cpu.setPC(pos);
    }
     /**
     * Entrada: una intruccion de ensamblador
     * Salida: se carga en memoria la instruccion
     * Restricciones: la posicicon debe ser menor al tamaño de la memoria
     * Objetivo: cargar una instruccion en memoria para que pueda ser ejecutada
     */
    public void cargarSO(String instr){
        int pos = cpu.getPC();
        if(pos <= memoria.size()){
            memoria.setMemoria(pos,instr);
            pos++;
        } else{
            JOptionPane.showConfirmDialog(null, "Error al leer el archivo" );
         
        }
           
    }
   
    /**
     * Entrada: no recibe parametros
     * Salida: ejectuar instruccion en memoria
     * Restricciones: el pc debe estar en el rango de memoria
     * Objetivo: ejecutar la instruccion carga en registro y avanzar el pc
     */
    public void pasoPaso(){
        System.out.println("tamaño memoria: "+memoria.size());
        int pc = cpu.getPC();
        if(pc >= memoria.size()){
            JOptionPane.showConfirmDialog(null, "Fin de la memoria" );
            return;
        }
        //se toma instruccion que se acaba de cargar
        String instr = memoria.getMemoria(pc);
        if(instr == null){
            JOptionPane.showConfirmDialog(null, "No hay instrucciones en la posicion "+pc );
            return;
        }
        cpu.setIR(instr);
        interprete(instr);
        cpu.setPC(pc+1);
    }
    /**
     * Entrada: nombre de un registro y un valor entero
     * Salida: actualizar el valor del registro
     * Restricciones: solo tiene los registro ax,bx,cx,dx
     * Objetivo: funcion auxiliar de interprete que nos ayuda a asignar
     *          un valor a un registro de la CPU
     */
    public void movRegistro(String registro,int valor){
        switch(registro.replace(",", "").toLowerCase()){
            case "ax":cpu.setAX(valor);break;
            case "bx":cpu.setBX(valor);break;
            case "cx":cpu.setCX(valor);break;
            case "dx":cpu.setDX(valor);break;
            
        }
    }
    /**
     * Entrada: nombre de un registro temporal
     * Salida: el valor almacenador en el registro
     * Restricciones: solo tiene los registro ax,bx,cx,dx
     * Objetivo: funcion auxiliar de interprete que nos ayuda a obtener
     *          el contenido actual de un registro de la CPU
     */
    public int getRegistro(String registro){
        switch(registro.replace(",", "").toLowerCase()){
            case "ax": return cpu.getAX();
            case "bx": return cpu.getBX();
            case "cx": return cpu.getCX();
            case "dx": return cpu.getDX();
            
        }
        return 0;
    }
    /**
     * Entrada: una instruccion de ensamblador
     * Salida: modificacion de los registros y el acumulador
     * Restricciones: debe de tener las operacion load,store,mov,sub,add
     * Objetivo: actualiar el estado de los registros de la CPU
     */
    public void interprete(String instr){
        String[] partes = instr.split(" ");
        String op = partes[0].toLowerCase();
        switch(op){
            case "load":
                cpu.setAC(getRegistro(partes[1]));
                break;
            case "store":
                movRegistro(partes[1],cpu.getAC());
                break;
            case "mov":
                movRegistro(partes[1],Integer.parseInt(partes[2]));
                break;
            case "sub":
                cpu.setAC(cpu.getAC()-getRegistro(partes[1]));
                break;
            case "add":
                cpu.setAC(cpu.getAC()+getRegistro(partes[1]));
                break;
        }
    }
    /**
     * Entrada: una intruccion(asm) de tipo string  
     * Salida: la instruccion en binario
     * Restricciones: solo entiende asm
     * Objetivo: traducir instruccion en ensamblador en binario para
     *           simular la ejecucion a nivel maquina
     */
    public String binario (String instr){
        String[] partes = instr.split(" ");
        String op = partes[0].toLowerCase();
        String str = "";
        switch(op){
            case "load": str = "0001"; break;
            case "store": str = "0010";  break; //0110?
            case "mov": str = "0011"; break;
            case "sub": str = "0100";break; //0111?
            case "add": str = "0101";break;
        }
        String reg = partes[1].replace(",", "").toLowerCase();
        switch(reg){
            case "ax": str += " 0001"; break;
            case "bx": str += " 0010";  break; 
            case "cx": str += " 0011"; break;
            case "dx": str += " 0100";break; 
            }
        if(instr.contains(",")){
            
            int val = Integer.parseInt(partes[2]);
            String valBin = String.format("%08d", Integer.parseInt(Integer.toBinaryString(val & 0xFF)));
            str += " " + valBin;
        }else{
            str += " 00000000";
        }
        return str;
        
    }
    
    public CPU getCPU() {return cpu;}
    public Memoria getMemoria() {return memoria;}
    
    
}
