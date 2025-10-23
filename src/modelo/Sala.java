
package modelo;

/**
 *
 * @author Juan Manuel, Nerina, Nahuel, Alaina, Juan Ignacio, Tiziana
 */
public class Sala {
    private int nroSala;
    private boolean apta3D;
    private int capacidad;
    private String estado;
    private boolean activo;

    public Sala() {
        this.estado = "Disponible";
        this.activo = true;
    }

    public Sala(int nroSala, boolean apta3D, int capacidad, String estado, boolean activo) {
        this.nroSala = nroSala;
        this.apta3D = apta3D;
        this.capacidad = capacidad;
        this.estado = estado;
        this.activo = activo;
    }

    public int getNroSala() {
        return nroSala;
    }

    public void setNroSala(int nroSala) {
        this.nroSala = nroSala;
    }

    public boolean isApta3D() {
        return apta3D;
    }

    public void setApta3D(boolean apta3D) {
        this.apta3D = apta3D;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Sala{" + "nroSala=" + nroSala + ", apta3D=" + apta3D + ", capacidad=" + capacidad + ", estado=" + estado + ", activo=" + activo + '}';
    }
    
    
    
}
