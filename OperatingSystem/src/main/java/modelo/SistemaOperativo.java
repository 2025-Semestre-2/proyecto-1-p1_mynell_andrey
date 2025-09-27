/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.IOException;
import java.util.*;
import javax.swing.JOptionPane;

/**
 *
 * @author Andrey
 */
/*
["arch1:5","arch2:12","arch3:18","arch4:24","arch5:30",
"mov ax, 5","mov bx, 3","load ax","add bx","sub ax","store ax","mov bx, -8",
"mov ax, 10","mov bx, 2","sub bx","add ax","store bx","mov cx, 7",
"mov cx, 4","mov dx, 6","load cx","add dx","store cx","mov ax, 12",
"mov ax, 1","mov bx, 1","add bx","add ax","store bx","mov dx, 9",
"mov ax, 15","mov bx, 5","sub bx","load bx","add ax","store ax","mov cx, -3"]
*/
public class SistemaOperativo {
    private BCP bcp;
    private CPU cpu;
    private Disco disco;
    private Memoria memoria;
    private List<String> instrucciones;
    private Queue<BCP> colaProcesos; //FIFO
    private int contProcess =1;
    
  
    public SistemaOperativo(){
        bcp = new BCP();
        cpu = new CPU();
        instrucciones = new ArrayList<>();
        colaProcesos = new LinkedList<>(); 
    }
    public void tamannoMemoria(int sizeMemoria) {
        this.memoria = new Memoria(sizeMemoria);
}

    public void tamannoDisco(int sizeDisco) throws IOException {
        this.disco = new Disco("src/hardware/Disco.txt", sizeDisco);
    }
    public void guardarInstrucciones(List<String> lista){
        instrucciones = lista;
    }

    public List<String> getIntr(){
        System.out.println(instrucciones);
        return instrucciones;
    }   

    public void inicializarSO(int tamanno){
        cpu.reset();
        cpu.setPC(0);
        int base = getEspacioSO(tamanno);
        bcp.setPc(base);
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
        int limite = algo;
        //crear BCP
        BCP bcp = new BCP(contProcess++,"nuevo",prioridad,base,limite);
        bcp.setEstado("nuevo");
        bcp.setCpuAsig("cpu0");
        bcp.setTiempoInicio(System.currentTimeMillis());
       // BCP bcp = new BCP;
        colaProcesos.add(bcp);   
    } 
    public void guardarBCPMemoria(BCP bcp, int posicion){
        memoria.setMemoria(posicion++,"p"+bcp.getIdProceso());
        memoria.setMemoria(posicion++,bcp.getEstado());
        memoria.setMemoria(posicion++,Integer.toString(bcp.getPc()));
        memoria.setMemoria(posicion++,Integer.toString(bcp.getBase()));
        memoria.setMemoria(posicion++,Integer.toString(bcp.getAlcance()));
        memoria.setMemoria(posicion++,Integer.toString(bcp.getAc()));
        memoria.setMemoria(posicion++,Integer.toString(bcp.getAx()));
        memoria.setMemoria(posicion++,Integer.toString(bcp.getBx()));
        memoria.setMemoria(posicion++,Integer.toString(bcp.getCx()));
        memoria.setMemoria(posicion++,Integer.toString(bcp.getDx()));
        memoria.setMemoria(posicion++,bcp.getIr());
        memoria.setMemoria(posicion++,Long.toString(bcp.getTiempoInicio()));
        memoria.setMemoria(posicion++,Long.toString(bcp.getTiempoFin()));
        memoria.setMemoria(posicion++,Long.toString(bcp.getTiempoTotal()));
        memoria.setMemoria(posicion++, bcp.getPila().toString());
        memoria.setMemoria(posicion++, String.join(",", bcp.getArchivos()));
    }
    
    public int numProcesos(){
        List<String> lista = getIntr();
        int numProceso = 0;
        for(String i:lista){
            if(i.toLowerCase().startsWith("arch")){
                numProceso++;
            }
        }
        return numProceso;   
    }
    
    public int getEspacioSO(int totalMemoria){
        int espacioSO = Math.max(20, totalMemoria/5);
        return espacioSO;
    }
    

    public void configurarMemoria(int totalMemoria, int numprocs){
        int espacioSO = Math.max(20, totalMemoria/5);
        int espacioUsuario = totalMemoria - espacioSO;
        
        int espacioBCP = 16*numProcesos();
        if(espacioUsuario<espacioBCP){
            System.out.println("Error:No hay suficiente espacio para "+numProcesos()+"procesos");
        }
    }
    
    
    public CPU getCPU() {return cpu;}
    public Disco getMemoria() {return disco;}
    
    
}
