/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modelo;

/**
 *
 * @author Juan Manuel, Nerina, Nahuel, Alaina, Juan Ignacio, Tiziana
 */
public class Lugar {
    
    private int codLugar;
    private Proyeccion proyeccion;
    private char fila;
    private int numero;
    private String estado;

    public Lugar() {
        this.estado = "Disponible";
    }

    public Lugar(int codLugar, Proyeccion proyeccion, char fila, int numero, String estado) {
        this.codLugar = codLugar;
        this.proyeccion = proyeccion;
        this.fila = fila;
        this.numero = numero;
        this.estado = estado;
    }

    public int getCodLugar() {
        return codLugar;
    }

    public void setCodLugar(int codLugar) {
        this.codLugar = codLugar;
    }

    public Proyeccion getProyeccion() {
        return proyeccion;
    }

    public void setProyeccion(Proyeccion proyeccion) {
        this.proyeccion = proyeccion;
    }

    public char getFila() {
        return fila;
    }

    public void setFila(char fila) {
        this.fila = fila;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Lugar{" + "codLugar=" + codLugar + ", proyeccion=" + proyeccion + ", fila=" + fila + ", numero=" + numero + ", estado=" + estado + '}';
    }
    
    
    
}
