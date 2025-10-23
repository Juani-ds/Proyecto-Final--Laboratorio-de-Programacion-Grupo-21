package modelo;

import java.time.LocalDate;

/**
 *
 * @author Juan Manuel, Nerina, Nahuel, Alaina, Juan Ignacio, Tiziana
 */
public class Pelicula {
    
    private int idPelicula;
    private String titulo;
    private String director;
    private String actores;
    private String origen;
    private String genero;
    private LocalDate estreno;
    private boolean enCartelera;
    private boolean activo;

    public Pelicula() {
        this.enCartelera = true;
        this.activo = true;
    }

    public Pelicula(int idPelicula, String titulo, String director, String actores, String origen, String genero, LocalDate estreno, boolean enCartelera, boolean activo) {
        this.idPelicula = idPelicula;
        this.titulo = titulo;
        this.director = director;
        this.actores = actores;
        this.origen = origen;
        this.genero = genero;
        this.estreno = estreno;
        this.enCartelera = enCartelera;
        this.activo = activo;
    }

    public Pelicula(String titulo, String director, String actores, String origen, String genero, LocalDate estreno, boolean enCartelera, boolean activo) {
        this.titulo = titulo;
        this.director = director;
        this.actores = actores;
        this.origen = origen;
        this.genero = genero;
        this.estreno = estreno;
        this.enCartelera = enCartelera;
        this.activo = activo;
    }

    public int getIdPelicula() {
        return idPelicula;
    }

    public void setIdPelicula(int idPelicula) {
        this.idPelicula = idPelicula;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActores() {
        return actores;
    }

    public void setActores(String actores) {
        this.actores = actores;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public LocalDate getEstreno() {
        return estreno;
    }

    public void setEstreno(LocalDate estreno) {
        this.estreno = estreno;
    }

    public boolean isEnCartelera() {
        return enCartelera;
    }

    public void setEnCartelera(boolean enCartelera) {
        this.enCartelera = enCartelera;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Pelicula{" + "idPelicula=" + idPelicula + ", titulo=" + titulo + ", director=" + director + ", actores=" + actores + ", origen=" + origen + ", genero=" + genero + ", estreno=" + estreno + ", enCartelera=" + enCartelera + ", activo=" + activo + '}';
    }
    
    
    
}
