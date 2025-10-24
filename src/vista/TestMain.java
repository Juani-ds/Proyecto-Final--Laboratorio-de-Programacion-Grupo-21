package vista;

import java.time.LocalDate;
import modelo.Comprador;
import persistencia.CompradorData;

/**
 *
 * @author Juan
 */
public class TestMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Testeo CinemaCentro desde Main\n");
        
        CompradorData compradorData = new CompradorData();

        System.out.println("Integrantes del grupo 17");

        Comprador integrante1 = new Comprador("12345678", "Juan Fernandez", LocalDate.of(1998, 3, 30), "contrasenia123", "Tarjeta", true);
        Comprador integrante2 = new Comprador("87654321", "Nerina Abrigo", LocalDate.of(1999, 8, 20), "jaja123hola", "Efectivo", true);
        Comprador integrante3 = new Comprador("11223344", "Alaina Reyes", LocalDate.of(1992, 7, 3), "44332211", "Débito",true);
        Comprador integrante4 = new Comprador("15151515", "Nahuel Guerra", LocalDate.of(1996, 10, 28), "51515151", "Débito", false);


        System.out.println("\n1. Guardar Integrantes.");
        compradorData.guardarComprador(integrante1);
        compradorData.guardarComprador(integrante2);
        compradorData.guardarComprador(integrante3);
        compradorData.guardarComprador(integrante4);

        System.out.println("\n2. Listar todos:");
        for (Comprador c : compradorData.listarCompradores()) {
            System.out.println(c);
        }

        System.out.println("\n3. Buscar a Juan Fernandez DNI: 12345678):");
        Comprador buscado = compradorData.buscarComprador("12345678");
        if (buscado != null) {
            System.out.println(buscado);
        }

        System.out.println("\n4. Actualizar medio de pago.");
        integrante1.setMedioPago("Débito");
        integrante1.setNombre("Nerina Abrido Actualizado");
        compradorData.actualizarComprador(integrante2);

        System.out.println("Se actualizo el comprador:");
        System.out.println(compradorData.buscarComprador("87654321"));

        System.out.println("\n5. Damos de baja a Alaina DNI: 11223344:");
        compradorData.bajaComprador("11223344");

        System.out.println("Compradores activos:");
        for (Comprador c : compradorData.listarCompradores()) {
            System.out.println(c);
        }

        System.out.println("\n6. Damos de alta a Nahuel DNI: 15151515:");
        compradorData.altaComprador("15151515");

        System.out.println("Compradores activos después de alta:");
        for (Comprador c : compradorData.listarCompradores()) {
            System.out.println(c);
        }

        System.out.println("\n7. Borramos a Juan DNI: 12345678:");
        compradorData.borrarComprador("12345678");

        System.out.println("Compradores actuales:");
        for (Comprador c : compradorData.listarCompradores()) {
            System.out.println(c);
        }

    }
    
}
