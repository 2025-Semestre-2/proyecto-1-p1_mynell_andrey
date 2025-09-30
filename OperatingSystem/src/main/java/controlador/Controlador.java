package controlador;

import static controlador.Utilidades.leerArchivo;
import static controlador.Utilidades.seleccionarArchivos;
import modelo.SistemaOperativo;

import vista.View;

import vista.Estadistica;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import modelo.CPU;
import modelo.BCP;
import modelo.Estadisticas;

public class Controlador {
    private SistemaOperativo pc;
    private View view;
    private Estadistica estadistica;
    private int contador =0;
    private Estadisticas est;
    

    public Controlador(SistemaOperativo pc, View view,Estadistica estadistica ) {
        this.pc = pc;
        this.view = view;
        this.est = new Estadisticas();
  
        this.estadistica = estadistica;
        this.view.btnBuscarListener(e -> buscarArchivo());
        this.view.btnEjecutar(e -> ejecutarSO());
        this.view.btnLimpiar(e -> cleanAll());
        this.view.btnPasoListener(e -> ejecutarPasoPaso());
       
        this.view.btnVerEst(e -> mostrarEstadistica());
        this.estadistica.btnVolver(e -> volverEst());
        showDisk();

    }
    public void ejecutarSO(){
        int sizeMemoria = (Integer) view.getSpnMemoria().getValue();
        int sizeDisco = (Integer) view.getSpnDisco().getValue();
        try {
        pc.tamannoDisco(sizeDisco);
        pc.tamannoMemoria(sizeMemoria);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al inicializar el disco: " + e.getMessage());
            return;
        }
        
        //inicializo
        pc.inicializarSO(sizeMemoria);
        
        //guardarEnDisco();
        pc.crearProcesos();
        System.out.println("----");
        System.out.println(pc.getPlanificador().getColaListos());
        guardarEspacioSO(sizeMemoria);
        planificadorTrabajos();
        
    }
 
    public void guardarEspacioSO(int size){
        for(int i =0;i<pc.getEspacioSO(size);i++){
            view.addFilaMemoria(Integer.toString(i), "<so>");
        }
    }
        
    public void planificadorTrabajos() {
    new Thread(() -> {   
        int indice = pc.getBCP().getPc();
        while (pc.getPlanificador().sizeCola() > 0) {

            // tomar el proceso de la cola
            BCP proceso = pc.getPlanificador().obeterSiguienteProceso();

            // pasar a preparado
            proceso.setEstado("preparado");
            int finalIndice = indice;
            preparadoBCP(proceso, finalIndice);

            // pasar a ejecucion
            proceso.setEstado("ejecucion");
            proceso.setTiempoInicio(System.currentTimeMillis());
            updateBCP(proceso, finalIndice);

            // ejecutar instrucciones
            for (int i = proceso.getBase(); i < proceso.getBase() + proceso.getAlcance(); i++) {
                String instr = pc.getDisco().getDisco(i);
                if (instr != null) {
                    pc.getCPU().setIR(instr);
                    pc.interprete(instr);
                    proceso.setPc(i + 1);

                    int tiempoInstr = pc.getTimer(instr);
                    try {
                        Thread.sleep(tiempoInstr);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    // actualizar UI de memoria y BCP en el hilo de Swing
                    pc.actualizarBCPDesdeCPU(proceso);
                    pc.guardarBCPMemoria(proceso, finalIndice);
                    updateMemoria(proceso, finalIndice);
                    actualizarBCP(proceso);
                    
                }
            }

            // finalizar proceso
            proceso.setEstado("finalizado");
            proceso.setTiempoFin(System.currentTimeMillis());
            proceso.setTiempoTotal(proceso.getTiempoFin() - proceso.getTiempoInicio());
            est.agregar(proceso.getIdProceso(), proceso.getTiempoTotal()); 

            updateBCP(proceso, finalIndice);
            agregarEstadosTabla(proceso.getArchivos());
            est.agregar(proceso.getIdProceso(), proceso.getTiempoTotal()); 

            indice += 16;
            
        }
    }).start(); 
        
}

    public void updateBCP(BCP proceso,int indice){
        pc.guardarBCPMemoria(proceso,indice);
        updateMemoria(proceso,indice);
        updateEstados(Integer.toString(proceso.getIdProceso()),proceso.getEstado());
            
    }
    public void preparadoBCP(BCP proceso,int indice){
        pc.guardarBCPMemoria(proceso,indice);
        addMemoria(proceso,indice);
        updateEstados(Integer.toString(proceso.getIdProceso()),proceso.getEstado());
            
    }
    public void agregarEstadosTabla(List<String> archivos){
        int i=0;
        for(String arch:archivos){
            view.addFilaES(i,arch);
            i++;
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
                //pc.guardarInstrucciones(instr);
                //inicializo
                pc.inicializarSO(sizeMemoria);
            }
            
            String i = instr.get(contador);
           
            //cargo
            pc.cargarSO(i);
            //ejecuto
            pc.pasoPaso();
            //updateProgram(i);
            updateMemoria(i);
            //updateBCP(pc.getCPU());
            contador++;
        } catch (IndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, "Fin de instrucciones: " + e.getMessage());
        } catch (IOException ex) {
            System.getLogger(Controlador.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    public void updateEstados(String valor1,String valor2){
        view.addFilaEstados(valor1, valor2);
        
    }
 
    public void updateMemoria(String instr){ 
        int star = pc.getCPU().getPC();
        view.addFilaMemoria(Integer.toString(star), instr);
        
    }
    public void addMemoria(BCP bcp, int posicion){
        view.addFilaMemoria(Integer.toString(posicion++),"p"+bcp.getIdProceso());
        view.addFilaMemoria(Integer.toString(posicion++),bcp.getEstado());
        view.addFilaMemoria(Integer.toString(posicion++),Integer.toString(bcp.getPc()));
        view.addFilaMemoria(Integer.toString(posicion++),Integer.toString(bcp.getBase()));
        view.addFilaMemoria(Integer.toString(posicion++),Integer.toString(bcp.getAlcance()));
        view.addFilaMemoria(Integer.toString(posicion++),Integer.toString(bcp.getAc()));
        view.addFilaMemoria(Integer.toString(posicion++),Integer.toString(bcp.getAx()));
        view.addFilaMemoria(Integer.toString(posicion++),Integer.toString(bcp.getBx()));
        view.addFilaMemoria(Integer.toString(posicion++),Integer.toString(bcp.getCx()));
        view.addFilaMemoria(Integer.toString(posicion++),Integer.toString(bcp.getDx()));
        view.addFilaMemoria(Integer.toString(posicion++),bcp.getIr());
        view.addFilaMemoria(Integer.toString(posicion++),Long.toString(bcp.getTiempoInicio()));
        view.addFilaMemoria(Integer.toString(posicion++),Long.toString(bcp.getTiempoFin()));
        view.addFilaMemoria(Integer.toString(posicion++),Long.toString(bcp.getTiempoTotal()));
        view.addFilaMemoria(Integer.toString(posicion++), bcp.getPila().toString());
        view.addFilaMemoria(Integer.toString(posicion++), String.join(",", bcp.getArchivos()));
    }
    public void updateMemoria(BCP bcp, int posicion){
        view.updateFilaMemoria(posicion,Integer.toString(posicion++),"p"+bcp.getIdProceso());
        view.updateFilaMemoria(posicion,Integer.toString(posicion++),bcp.getEstado());
        view.updateFilaMemoria(posicion,Integer.toString(posicion++),Integer.toString(bcp.getPc()));
        view.updateFilaMemoria(posicion,Integer.toString(posicion++),Integer.toString(bcp.getBase()));
        view.updateFilaMemoria(posicion,Integer.toString(posicion++),Integer.toString(bcp.getAlcance()));
        view.updateFilaMemoria(posicion,Integer.toString(posicion++),Integer.toString(bcp.getAc()));
        view.updateFilaMemoria(posicion,Integer.toString(posicion++),Integer.toString(bcp.getAx()));
        view.updateFilaMemoria(posicion,Integer.toString(posicion++),Integer.toString(bcp.getBx()));
        view.updateFilaMemoria(posicion,Integer.toString(posicion++),Integer.toString(bcp.getCx()));
        view.updateFilaMemoria(posicion,Integer.toString(posicion++),Integer.toString(bcp.getDx()));
        view.updateFilaMemoria(posicion,Integer.toString(posicion++),bcp.getIr());
        view.updateFilaMemoria(posicion,Integer.toString(posicion++),Long.toString(bcp.getTiempoInicio()));
        view.updateFilaMemoria(posicion,Integer.toString(posicion++),Long.toString(bcp.getTiempoFin()));
        view.updateFilaMemoria(posicion,Integer.toString(posicion++),Long.toString(bcp.getTiempoTotal()));
        view.updateFilaMemoria(posicion,Integer.toString(posicion++), bcp.getPila().toString());
        view.updateFilaMemoria(posicion,Integer.toString(posicion++), String.join(",", bcp.getArchivos()));
    }


    public void actualizarBCP(BCP bcp){
        view.setlbIBX(Integer.toString(bcp.getBx()));
        view.setlbIR(pc.binario(bcp.getIr()));
        view.setlblAC(Integer.toString(bcp.getAc()));
        view.setlblAX(Integer.toString(bcp.getAx()));
        view.setlblCX(Integer.toString(bcp.getCx()));
        view.setlblDX(Integer.toString(bcp.getDx()));
        view.setlblPC(Integer.toString(bcp.getPc())); 
        
       // view.setlbEnlace(bcp.getSiguiente());
        view.setlbCPU(bcp.getCpuAsig());
        view.setlbBase(Integer.toString(bcp.getBase()));
        view.setlbAlcance(Integer.toString(bcp.getAlcance()));
        view.setlblPrioridad(Integer.toString(bcp.getPrioridad()));
    }
 
    public void buscarArchivo(){
        File[] archivos = seleccionarArchivos();
        if(archivos != null){
            for(File archivo: archivos){
                try{
                    List<String> lista = leerArchivo(archivo);
                    pc.guardarInstrucciones(archivo.getName(),lista);
                    pc.getIntr();
                    JOptionPane.showMessageDialog(null,"Archivo leido correctamente");

                } catch(Exception e){
                    JOptionPane.showConfirmDialog(null, "Error al leer el archivo" + e.getMessage());
                }
            }
        }
        showDisk();
    }
    
    private void showDisk(){
        List<String> disco= pc.getDisk();
        DefaultTableModel modelo = (DefaultTableModel) this.view.jTable3.getModel();
        modelo.setRowCount(0);
        for(int i = 0;i<disco.size();i++){
            modelo.addRow(new Object[]{i, disco.get(i)});
        }
        for(int i = disco.size(); i<pc.getDisco().size(); i++){
            modelo.addRow(new Object[]{i, ""});
        }
    }

    public void cleanAll(){
        view.getModelProgram().setRowCount(0);
        view.getModelMemory().setRowCount(0);
        pc.getCPU().reset();
        pc.ClearDisk();
        contador =0;
        view.setlbIBX("---");
        view.setlbIR("---");
        view.setlblAC("---");
        view.setlblAX("---");
        view.setlblCX("---");
        view.setlblDX("---");
        view.setlblPC("---");
        view.getSpnMemoria().setValue(100);
        showDisk();
        JOptionPane.showMessageDialog(null, "Sistema limpiado correctamente");
        
    }
    

    private void mostrarEstadistica(){
       // view.dispose(); cierro ventana principal
       
        estadistica.setVisible(true);
        estadistica.mostrarGraficoBarras(est.getRegistros());
        System.out.println(est.toString());
    }

    private void volverEst(){
        estadistica.setVisible(false);
    }
       

}
