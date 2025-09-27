package controlador;

import static controlador.Utilidades.leerArchivo;
import static controlador.Utilidades.seleccionarArchivo;
import modelo.SistemaOperativo;

import vista.View;

import vista.Estadistica;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import modelo.CPU;

public class Controlador {
    private SistemaOperativo pc;
    private View view;
    private Estadistica estadistica;
    private int contador =0;
    

    public Controlador(SistemaOperativo pc, View view,Estadistica estadistica ) {
        this.pc = pc;
        this.view = view;
  
        this.estadistica = estadistica;
        this.view.btnBuscarListener(e -> buscarArchivo());
        this.view.btnEjecutar(e -> ejecutarPC());
        this.view.btnLimpiar(e -> cleanAll());
        this.view.btnPasoListener(e -> ejecutarPasoPaso());
       
        this.view.btnVerEst(e -> mostrarEstadistica());
        this.estadistica.btnVolver(e -> volverEst());

    }
 
    public void ejecutarPC(){
        int sizeMemoria = (Integer) view.getSpnMemoria().getValue();
        int sizeDisco = (Integer) view.getSpnDisco().getValue();
        System.out.println("tamano memoria: "+sizeMemoria+" , "+sizeDisco);
       // pc = new SistemaOperativo(size);
        
        List<String> instr = pc.getIntr();
        if(instr.isEmpty()){
            JOptionPane.showMessageDialog(null,"Error: No hay intrucciones para leer");
            return;
        }
        pc.tamannoDisco(sizeDisco);
        pc.tamannoMemoria(sizeMemoria);


        //inicializo
        pc.inicializarSO(sizeMemoria);
        
        for(int i = 0; i < instr.size(); i++){
            String instruccion = instr.get(i);
            if(i>=pc.numProcesos()){
                updateProgram(instruccion);
                System.out.println("hola"+instruccion);}
            //cargo
            pc.cargarSO(instruccion);
            //ejecuto
            pc.pasoPaso();
           // updateProgram(i);
            updateMemoria(instruccion);
           // updateBCP(pc.getCPU());
        }
  
    }
    public void ejecutarPasoPaso(){
        int sizeMemoria = (Integer) view.getSpnMemoria().getValue();
        int sizeDisco = (Integer) view.getSpnDisco().getValue();
        System.out.println("tamano memoria: "+sizeMemoria+" , "+sizeDisco);
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
                pc.tamannoDisco(sizeDisco);
                pc.tamannoMemoria(sizeMemoria);
                pc.guardarInstrucciones(instr);
                //inicializo
                pc.inicializarSO(sizeMemoria);
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

    public void updateProgram(String instr){
        view.addFilaPrograma(instr, pc.binario(instr));
        
    }
 
    public void updateMemoria(String instr){ 
        int star = pc.getCPU().getPC();
        view.addFilaMemoria(Integer.toString(star), instr);
        
    }

    public void updateBCP(CPU cpu){
        view.setlbIBX(Integer.toString(cpu.getBX()));
        view.setlbIR(pc.binario(cpu.getIR()));
        view.setlblAC(Integer.toString(cpu.getAC()));
        view.setlblAX(Integer.toString(cpu.getAX()));
        view.setlblCX(Integer.toString(cpu.getCX()));
        view.setlblDX(Integer.toString(cpu.getDX()));
        view.setlblPC(Integer.toString(cpu.getPC())); 
    }
 
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
    

    private void mostrarEstadistica(){
       // view.dispose(); cierro ventana principal
        estadistica.setVisible(true);
    }

    private void volverEst(){
        estadistica.setVisible(false);
    }
       

}
