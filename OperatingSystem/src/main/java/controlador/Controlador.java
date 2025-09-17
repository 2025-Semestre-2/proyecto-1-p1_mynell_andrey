package controlador;

import static controlador.Utilidades.leerArchivo;
import static controlador.Utilidades.seleccionarArchivo;
import modelo.MiniPC;

import vista.View;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import modelo.CPU;

public class Controlador {
    private MiniPC pc;
    private View view;
    private int contador =0;
    
    /**
     * Constructor del controlador
     * Entrada: la instancia de la minipc y la vista
     * Salida: ninguna
     * Restricciones: no posee restricciones
     * Objetivo: comunicacion entre la interfaz y la minipc
     */
    public Controlador(MiniPC pc, View view) {
        this.pc = pc;
        this.view = view;
        this.view.btnBuscarListener(e -> buscarArchivo());
        this.view.btnEjecutar(e -> ejecutarPC());
        this.view.btnLimpiar(e -> cleanAll());
        this.view.btnPasoListener(e -> ejecutarPasoPaso());
    }
    /**
     * Entrada: no recibe parametros
     * Salida: ejecuta,cargar e inicializar las instrucciones y actualiza los registro
     * Restricciones: que hayan instrucciones cargas
     * Objetivo: ejecucionc completa del programa, actualizando los 
     *           modelos de programa
     */
    public void ejecutarPC(){
        int size = (Integer) view.getSpnMemoria().getValue();
        System.out.println("tamano memoria: "+size);
       // pc = new MiniPC(size);
        
        List<String> instr = pc.getIntr();
        if(instr.isEmpty()){
            JOptionPane.showMessageDialog(null,"Error: No hay intrucciones para leer");
            return;
        }
        pc = new MiniPC(size);

        //limpio
        view.getModelProgram().setRowCount(0);
        view.getModelMemory().setRowCount(0);
        //inicializo
        pc.inicializarPC();
        
        for(String i : instr){
            //cargo
            pc.cargarSO(i);
            //ejecuto
            pc.pasoPaso();
            updateProgram(i);
            updateMemoria(i);
            updateBCP(pc.getCPU());
        }
  
    }
    public void ejecutarPasoPaso(){
        int size = (Integer) view.getSpnMemoria().getValue();
        System.out.println("tamano memoria: "+size);
        List<String> instr = pc.getIntr();
        if(instr.isEmpty()){
            JOptionPane.showMessageDialog(null,"Error: No hay intrucciones para leer");
            return;
        }
        
        try{
            if(contador == 0){
                System.out.println("entra");
                //limpio
                view.getModelProgram().setRowCount(0);
                view.getModelMemory().setRowCount(0);
                //guardo tamaño de memoria
                pc = new MiniPC(size);
                pc.guardarInstrucciones(instr);
                //inicializo
                pc.inicializarPC();
            }
            
            String i = instr.get(contador);
           
            //cargo
            pc.cargarSO(i);
            //ejecuto
            pc.pasoPaso();
            updateProgram(i);
            updateMemoria(i);
            updateBCP(pc.getCPU());
            contador++;
        } catch (IndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, "Fin de instrucciones: " + e.getMessage());
        }
    }
    /**
     * Entrada: una instruccion de ensamblador
     * Salida: añadir la tabla la instruccion en binario
     * Restricciones: no posee restriciones
     * Objetivo: añadir la instruccion de ensamblador en binario para
     *           simular la ejecucion a nivel maquina en la tabla
     */
    public void updateProgram(String instr){
        view.addFilaPrograma(instr, pc.binario(instr));
        
    }
        /**
     * Entrada: una instruccion de ensamblador
     * Salida: añadir la tabla la instruccion 
     * Restricciones: no posee restriciones
     * Objetivo: añadir la instruccion de ensamblador  para
     *           simular la ejecucion cargada
     */
    public void updateMemoria(String instr){ 
        int star = pc.getCPU().getPC();
        view.addFilaMemoria(Integer.toString(star), instr);
        
    }
    /**
     * Entrada: los registros de la cpu
     * Salida: la actulizacion del bcp
     * Restricciones: no posee restricciones
     * Objetivo: que el usuario logre visualizar las actulizaciones
     *          del bcp en tiempo de ejecucion
     */
    public void updateBCP(CPU cpu){
        view.setlbIBX(Integer.toString(cpu.getBX()));
        view.setlbIR(pc.binario(cpu.getIR()));
        view.setlblAC(Integer.toString(cpu.getAC()));
        view.setlblAX(Integer.toString(cpu.getAX()));
        view.setlblCX(Integer.toString(cpu.getCX()));
        view.setlblDX(Integer.toString(cpu.getDX()));
        view.setlblPC(Integer.toString(cpu.getPC())); 
    }
    /**
     * Entrada: el archivo seleccionado por el usuario
     * Salida: guardar las instrucciones en la minipc
     * Restricciones: no posee restricciones
     * Objetivo: cargar instrucciones del usuario
     */
    public void buscarArchivo(){
        File archivo = seleccionarArchivo();
        if(archivo != null){
            try{
                List<String> lista = leerArchivo(archivo);
                pc.guardarInstrucciones(lista);
                pc.getIntr();
                JOptionPane.showMessageDialog(null,"Archivo leido correctamente");
        
            } catch(Exception e){
                JOptionPane.showConfirmDialog(null, "Error al leer el archivo" + e.getMessage());
            }
        }
    }
    /**
     * Entrada: no recibe parametros
     * Salida: limpiar todo el contenido de la minipc
     * Restricciones: no posee restricciones
     * Objetivo: cuando el usuario desee volver a correr el programa
     *           que todo el contenido posterior se elimine
     */
    public void cleanAll(){
        view.getModelProgram().setRowCount(0);
        view.getModelMemory().setRowCount(0);
        pc.getCPU().reset();
        pc.guardarInstrucciones(List.of());
        contador =0;
        view.setlbIBX("---");
        view.setlbIR("---");
        view.setlblAC("---");
        view.setlblAX("---");
        view.setlblCX("---");
        view.setlblDX("---");
        view.setlblPC("---");
        view.getSpnMemoria().setValue(100);
        JOptionPane.showMessageDialog(null, "Sistema limpiado correctamente");
        
        
    }
    
       

}
