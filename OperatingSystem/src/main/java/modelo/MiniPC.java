/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import javax.swing.JOptionPane;

/**
 *
 * @author Andrey
 */
public class MiniPC {
    private CPU cpu;
    private Disco disco;
    private Memoria memoria;
    private int tamanno;
    private List<String> instrucciones;
    private Queue<BCP> colaProcesos; //FIFO
    private int contProcess =1;
    private int memoriaLibre = 0;
    
  
    public MiniPC(int sizeDisco,int sizeMemoria){
        cpu = new CPU();
         disco = new Disco(sizeDisco);
        this.tamanno = tamanno;
        memoria = new Memoria(sizeMemoria);
        instrucciones = new ArrayList<>();
        colaProcesos = new LinkedList<>(); 
    }
   
    public void guardarInstrucciones(List<String> lista){
        instrucciones = lista;
    }

    public List<String> getIntr(){
        System.out.println(instrucciones);
        return instrucciones;
    }   

    public void inicializarPC(){
        cpu.reset();
        Random rand = new Random();
        //posicion inical donde empieza a guardase las intrucciones
        int pos = rand.nextInt(disco.size()-20+1) + 20;
        cpu.setPC(pos);
    }

    public void cargarSO(String instr){
        int pos = cpu.getPC();
        if(pos <= disco.size()){
            disco.setDisco(pos,instr);
            pos++;
        } else{
            JOptionPane.showConfirmDialog(null, "Error al leer el archivo" );
         
        }
           
    }
   

    public void pasoPaso(){
        System.out.println("tamaño disco: "+disco.size());
        int pc = cpu.getPC();
        if(pc >= disco.size()){
            JOptionPane.showConfirmDialog(null, "Fin de la disco" );
            return;
        }
        //se toma instruccion que se acaba de cargar
        String instr = disco.getDisco(pc);
        if(instr == null){
            JOptionPane.showConfirmDialog(null, "No hay instrucciones en la posicion "+pc );
            return;
        }
        cpu.setIR(instr);
        interprete(instr);
        cpu.setPC(pc+1);
    }
    
    

    public void movRegistro(String registro,int valor){
        switch(registro.replace(",", "").toLowerCase()){
            case "ax":cpu.setAX(valor);break;
            case "bx":cpu.setBX(valor);break;
            case "cx":cpu.setCX(valor);break;
            case "dx":cpu.setDX(valor);break;
            
        }
    }

    public int getRegistro(String registro){
        switch(registro.replace(",", "").toLowerCase()){
            case "ax": return cpu.getAX();
            case "bx": return cpu.getBX();
            case "cx": return cpu.getCX();
            case "dx": return cpu.getDX();
            
        }
        return 0;
    }

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
            
            case "inc": str = "0110"; break;
            case "dec": str = "0111";break;
            case "swap": str = "1000";break;
            case "int": str = "1001";break;
            case "jmp": str = "1010";break;
            case "cmp": str = "1011";break;
            case "je": str = "1100";break;
            case "jne": str = "1101";break;
            case "param": str = "1110";break;
            case "push": str = "1111";break;
            case "pop": str = "0000";break;
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
    //bcp
    
     
    public void crearProceso(List<String> instrucciones, int prioridad){
        int algo = 1;
        //calcular base donde guardar el proceso en el disco
        int base = algo;
        int liminte = algo;
        //crear BCP
        BCP bcp = new BCP(contProcess++,"nuevo",prioridad,base,liminte);
        bcp.setEstado("nuevo");
        bcp.setCpuAsig("cpu0");
        bcp.setTiempoInicio(System.currentTimeMillis());
       // BCP bcp = new BCP;
        colaProcesos.add(bcp);
        
    }
      
    public void guardarBCPMemoria(BCP bcp, int posicion){
        memoria.setMemoria(posicion,"p"+bcp.getIdProceso());
        memoria.setMemoria(posicion + 1,bcp.getEstado());
        memoria.setMemoria(posicion + 2,Integer.toString(bcp.getPc()));
        memoria.setMemoria(posicion + 3,Integer.toString(bcp.getBase()));
        memoria.setMemoria(posicion + 3,Integer.toString(bcp.getAlcance()));
        memoria.setMemoria(posicion,Integer.toString(bcp.getAc()));
        memoria.setMemoria(posicion,Integer.toString(bcp.getAx()));
        memoria.setMemoria(posicion,Integer.toString(bcp.getBx()));
        memoria.setMemoria(posicion,Integer.toString(bcp.getCx()));
        memoria.setMemoria(posicion,Integer.toString(bcp.getDx()));
        memoria.setMemoria(posicion,bcp.getIr());
        memoria.setMemoria(posicion,Long.toString(bcp.getTiempoInicio()));
        memoria.setMemoria(posicion,Long.toString(bcp.getTiempoFin()));
        memoria.setMemoria(posicion,Long.toString(bcp.getTiempoTotal()));
        //falta pila y archivos
    }
    

    private int asignarMemoria(int tamano) {
        int base = memoriaLibre;
        memoriaLibre += tamano;
        return base;
    }
      
    
    
    
    public CPU getCPU() {return cpu;}
    public Disco getMemoria() {return disco;}
    public void setTamanno(int tamanno){this.tamanno = tamanno;}
    
}
