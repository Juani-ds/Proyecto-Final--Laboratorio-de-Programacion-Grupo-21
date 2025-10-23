
package modelo;
import java.time.LocalDateTime;

/**
 *
 * @author Juan Manuel, Nerina, Nahuel, Alaina, Juan Ignacio, Tiziana
 */
public class Proyeccion {
    
    private int idProyeccion;
    private Pelicula pelicula;
    private Sala sala;
    private String idioma;
    private boolean es3D;
    private boolean subtitulada;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    private double precioLugar;
    private boolean activo;

    public Proyeccion() {
        this.activo = true;
    }

    public Proyeccion(int idProyeccion, Pelicula pelicula, Sala sala, String idioma, boolean es3D, boolean subtitulada, LocalDateTime horaInicio, LocalDateTime horaFin, double precioLugar, boolean activo) {
        this.idProyeccion = idProyeccion;
        this.pelicula = pelicula;
        this.sala = sala;
        this.idioma = idioma;
        this.es3D = es3D;
        this.subtitulada = subtitulada;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.precioLugar = precioLugar;
        this.activo = activo;
    }

    public Proyeccion(Pelicula pelicula, Sala sala, String idioma, boolean es3D, boolean subtitulada, LocalDateTime horaInicio, LocalDateTime horaFin, double precioLugar, boolean activo) {
        this.pelicula = pelicula;
        this.sala = sala;
        this.idioma = idioma;
        this.es3D = es3D;
        this.subtitulada = subtitulada;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.precioLugar = precioLugar;
        this.activo = activo;
    }

    public int getIdProyeccion() {
        return idProyeccion;
    }

    public void setIdProyeccion(int idProyeccion) {
        this.idProyeccion = idProyeccion;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public boolean isEs3D() {
        return es3D;
    }

    public void setEs3D(boolean es3D) {
        this.es3D = es3D;
    }

    public boolean isSubtitulada() {
        return subtitulada;
    }

    public void setSubtitulada(boolean subtitulada) {
        this.subtitulada = subtitulada;
    }

    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalDateTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalDateTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalDateTime horaFin) {
        this.horaFin = horaFin;
    }

    public double getPrecioLugar() {
        return precioLugar;
    }

    public void setPrecioLugar(double precioLugar) {
        this.precioLugar = precioLugar;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Proyeccion{" + "idProyeccion=" + idProyeccion + ", pelicula=" + pelicula + ", sala=" + sala + ", idioma=" + idioma + ", es3D=" + es3D + ", subtitulada=" + subtitulada + ", horaInicio=" + horaInicio + ", horaFin=" + horaFin + ", precioLugar=" + precioLugar + ", activo=" + activo + '}';
    }
    
    
    
}

