package controlador;

import static controlador.Utilidades.leerArchivo;
import static controlador.Utilidades.seleccionarArchivo;
import modelo.MiniPC;

import vista.View;
import vista.BCPview;
import vista.Estadistica;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import modelo.CPU;

public class Controlador {
    private MiniPC pc;
    private View view;
    private Estadistica estadistica;
    private BCPview bcpV;
    private int contador =0;
    

    public Controlador(MiniPC pc, View view,BCPview bcpV,Estadistica estadistica ) {
        this.pc = pc;
        this.view = view;
        this.bcpV = bcpV;
        this.estadistica = estadistica;
        this.view.btnBuscarListener(e -> buscarArchivo());
        this.view.btnEjecutar(e -> ejecutarPC());
        this.view.btnLimpiar(e -> cleanAll());
        this.view.btnPasoListener(e -> ejecutarPasoPaso());
        
        this.view.btnVerBCP(e -> mostrarBCP());
        this.view.btnVerEst(e -> mostrarEstadistica());
        this.estadistica.btnVolver(e -> volverEst());
        this.bcpV.btnVolver(e -> volverBCP());
    }
 
    public void ejecutarPC(){
        int sizeMemoria = (Integer) view.getSpnMemoria().getValue();
        int sizeDisco = (Integer) view.getSpnDisco().getValue();
        System.out.println("tamano memoria: "+sizeMemoria+" , "+sizeDisco);
       // pc = new MiniPC(size);
        
        List<String> instr = pc.getIntr();
        if(instr.isEmpty()){
            JOptionPane.showMessageDialog(null,"Error: No hay intrucciones para leer");
            return;
        }
        pc = new MiniPC(sizeMemoria,sizeDisco);

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
                pc = new MiniPC(sizeMemoria,sizeDisco);
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

    public void updateProgram(String instr){
        view.addFilaPrograma(instr, pc.binario(instr));
        
    }
 
    public void updateMemoria(String instr){ 
        int star = pc.getCPU().getPC();
        view.addFilaMemoria(Integer.toString(star), instr);
        
    }

    public void updateBCP(CPU cpu){
        bcpV.setlbIBX(Integer.toString(cpu.getBX()));
        bcpV.setlbIR(pc.binario(cpu.getIR()));
        bcpV.setlblAC(Integer.toString(cpu.getAC()));
        bcpV.setlblAX(Integer.toString(cpu.getAX()));
        bcpV.setlblCX(Integer.toString(cpu.getCX()));
        bcpV.setlblDX(Integer.toString(cpu.getDX()));
        bcpV.setlblPC(Integer.toString(cpu.getPC())); 
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
        bcpV.setlbIBX("---");
        bcpV.setlbIR("---");
        bcpV.setlblAC("---");
        bcpV.setlblAX("---");
        bcpV.setlblCX("---");
        bcpV.setlblDX("---");
        bcpV.setlblPC("---");
        view.getSpnMemoria().setValue(100);
        JOptionPane.showMessageDialog(null, "Sistema limpiado correctamente");
    }
    
    private void mostrarBCP(){
       // view.dispose(); cierro ventana principal
        bcpV.setVisible(true);
    }
    private void mostrarEstadistica(){
       // view.dispose(); cierro ventana principal
        estadistica.setVisible(true);
    }

    private void volverEst(){
        estadistica.setVisible(false);
    }
    private void volverBCP(){
        bcpV.setVisible(false);
        
    }
    
       

}
