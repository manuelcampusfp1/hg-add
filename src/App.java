import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.logging.Level;

import com.mongodb.MongoWriteException;
import com.mongodb.client.*;
import org.bson.Document;

import static com.mongodb.client.model.Filters.*;

public class App {
    public static void main(String[] args) {
        // Crear una conexión a MongoDB
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        
        // Seleccionar la base de datos y la colección
        MongoDatabase database = mongoClient.getDatabase("hitoGrupal");
        MongoCollection<Document> clientedb = database.getCollection("Clientes");
        MongoCollection<Document> historialClientedb = database.getCollection("HistorialClientes");
        java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);
        Scanner sc = new Scanner(System.in);
        int opcion = 0;

        do {
            System.out.println("--- MENU ---");
            System.out.println("1. Agregar un nuevo Cliente");
            System.out.println("2. Agregar una incidencia");
            System.out.println("3. Ver un Cliente");
            System.out.println("4. Info direccion");
            System.out.println("5. Salir");
            System.out.print("Opcion: ");
            opcion = helpers.pedirEntero(sc);

            switch (opcion) {
                case 1:
                    agregarCliente(clientedb, historialClientedb, sc);
                    break;
                case 2:
                    agregarHistorial(clientedb, historialClientedb, sc);
                    break;
                case 3:
                    verCliente(clientedb, historialClientedb, sc);
                    break;
                case 4:
                    infoDireccion(clientedb, historialClientedb, sc);
                    break;
                case 5:
                    System.out.println("Saliendo...");
                    break;
            }
        } while (opcion != 5);
        
        // Cerrar la conexión
        mongoClient.close();
    }

    private static void infoDireccion(MongoCollection<Document> clientedb, MongoCollection<Document> historialClientedb, Scanner sc) {

        System.out.println("--- Dirección ---");
        System.out.println("1. Numero de llamadas recibidas totales");
        System.out.println("2. Llamadas en una fecha dada");
        System.out.print("Opcion: ");
        int op = helpers.pedirEntero(sc);
        if(op == 1){
            //llamadas recibidas totales
            long llamadasTotales = historialClientedb.countDocuments();
            System.out.println("El número de llamadas totales ha sido: "+llamadasTotales);
        } else if(op == 2){
            //llamadas en una fecha dada

            boolean parseado = true;
            do {
                System.out.println("introduce una fecha válida");
                String date = sc.nextLine();
                if (date.equals("salir")){
                    parseado = false;
                }
                try{
                    long numLlamadas=0;
                    long countHardware=0;
                    long countSoftware=0;
                    long countSolucionadoTrue=0;
                    long countSolucionadoFalse=0;
                    for (Document doc : historialClientedb.find(eq("fecha",date))) {
                        numLlamadas++;
                    }
                    for (Document doc : historialClientedb.find(eq("problema","hardware"))) {
                        countHardware++;
                    }
                    for (Document doc : historialClientedb.find(eq("problema","software"))) {
                        countSoftware++;
                    }
                    for (Document doc : historialClientedb.find(eq("solucionado","true"))) {
                        countSolucionadoTrue++;
                    }
                    for (Document doc : historialClientedb.find(eq("solucionado","false"))) {
                        countSolucionadoFalse++;
                    }
                    System.out.println("número llamadas: "+numLlamadas);
                    System.out.println("numero hardware: "+countHardware);
                    System.out.println("numero software: "+countSoftware);
                    System.out.println("numero solucionadas: "+countSolucionadoTrue);
                    System.out.println("numero no solucionadas: "+countSolucionadoFalse);
                    parseado = false;
                }catch(DateTimeParseException e){
                    parseado = true;
                    System.out.println("Error, introduce una fecha válida o escribe salir");
                }
            }while(parseado);

        } else {
            System.err.println("Error");
        }

    }

    public static void agregarCliente(MongoCollection<Document> clientedb,MongoCollection<Document> historialClientedb, Scanner sc) {
        
        System.out.print("DNI: ");
        String dniCliente = sc.nextLine();
        System.out.print("Nombre: ");
        String nombreCliente = sc.nextLine();
        System.out.print("Apellido: ");
        String apellidoCliente = sc.nextLine();
        System.out.print("Telefono: ");
        int telefonoCliente = helpers.pedirEntero(sc);
        
        // Crear un nuevo contacto
        Document cliente = new Document("_id", "" + dniCliente)
                .append("nombre", "" + nombreCliente)
                .append("apellidos", "" + apellidoCliente)
                .append("telefono",telefonoCliente);
            try{
                clientedb.insertOne(cliente);
            }catch(MongoWriteException e){
            System.out.println("error en la inserción, revise la no duplicidad de los datos del señor");
        }

        System.out.println("--- Historial de "+ nombreCliente + " ----");

        System.out.print("Id de la incidencia: ");
        int idHistorial = helpers.pedirEntero(sc);

        System.out.println("Fecha de hoy :"+LocalDate.now());

        System.out.print("Motivo: ");
        String motivoHistorial = sc.nextLine();
        
        String problemaHistorial = null;
        System.out.println("--- Problema --- ");
        System.out.println("1. hardware");
        System.out.println("2. software");
        System.out.print("Opcion: ");
        int op = helpers.pedirEntero(sc);
        if(op == 1){
          problemaHistorial = "hardware";
        } else if(op == 2){
            problemaHistorial = "software";
        } else {
            System.err.println("Error");
        }

        String repacionHistorial = null;;
        System.out.println("--- Reparacion --- ");
        System.out.println("1. Fisica");
        System.out.println("2. Telefonica");
        System.out.print("Opcion: ");
        op = helpers.pedirEntero(sc);
        if(op == 1){
            repacionHistorial = "fisica";
        } else if(op == 2) {
            repacionHistorial = "telefonica";
        }else {
            System.err.println("Error");
        }

        String solucionadoHistorial = null;;
        System.out.println("--- Solucionado --- ");
        System.out.println("1. Si");
        System.out.println("2. No");
        System.out.print("Opcion: ");
        op = helpers.pedirEntero(sc);
        if(op == 1){
            solucionadoHistorial = "true";
        } else if(op == 2) {
            solucionadoHistorial = "false";
        }else {
            System.err.println("Error");
        }
       
         // Crear un nuevo contacto
         Document historialCliente = new Document("_id",+ idHistorial)
         .append("fecha",""+ LocalDate.now())
         .append("dni", "" + dniCliente)
         .append("motivo", "" + motivoHistorial)
         .append("problema", "" + problemaHistorial)
         .append("reparacion", "" + repacionHistorial)
         .append("solucionado", "" + solucionadoHistorial);
         try{
             historialClientedb.insertOne(historialCliente);
         }catch (MongoWriteException e){
             System.out.println("error en la insercion, revise la no duplicidad de los datos");
         }
    }

    public static void verCliente(MongoCollection<Document> clientedb,MongoCollection<Document> historialClientedb, Scanner sc){
        System.out.print("Diga el dni del cliente que quiere consultar: ");
        String dniCliente = sc.nextLine();

        Document nombreBuscar = clientedb.find(eq("_id", ""+dniCliente)).first();
        System.out.println("-------- Datos de "+ dniCliente +" --------");
        if (nombreBuscar==null){
            System.out.println("no se ha encontrado el señor en la base de datos");
        }else{
            System.out.println(nombreBuscar.toJson());
        }
        

        System.out.println("-------- Historial de "+ dniCliente +" --------");
        for (Document doc : historialClientedb.find(eq("dni", ""+dniCliente))) {
            System.out.println(doc.toJson());
        }
    }

    public static void agregarHistorial(MongoCollection<Document> clientedb,MongoCollection<Document> historialClientedb, Scanner sc){
        
        System.out.print("Dni del cliente para poner la incidencia: ");
        String dniHistorial = sc.nextLine();

        System.out.print("Id de la incidencia: ");
        int idHistorial = helpers.pedirEntero(sc);

        System.out.print("Motivo: ");
        String motivoHistorial = sc.nextLine();
        
        String problemaHistorial = null;
        System.out.println("--- Problema --- ");
        System.out.println("1. hardware");
        System.out.println("2. software");
        System.out.print("Opcion: ");
        int op = helpers.pedirEntero(sc);
        if(op == 1){
          problemaHistorial = "hardware";
        } else if(op == 2){
            problemaHistorial = "software";
        } else {
            System.err.println("Error");
        }

        String repacionHistorial = null;
        System.out.println("--- Reparacion --- ");
        System.out.println("1. Fisica");
        System.out.println("2. Telefonica");
        System.out.print("Opcion: ");
        op = helpers.pedirEntero(sc);
        if(op == 1){
            repacionHistorial = "fisica";
        } else if(op == 2) {
            repacionHistorial = "telefonica";
        }else {
            System.err.println("Error");
        }

        String solucionadoHistorial = null;
        System.out.println("--- Solucionado --- ");
        System.out.println("1. Si");
        System.out.println("2. No");
        System.out.print("Opcion: ");
        op = helpers.pedirEntero(sc);
        if(op == 1){
            solucionadoHistorial = "true";
        } else if(op == 2) {
            solucionadoHistorial = "false";
        }else {
            System.err.println("Error");
        }
       
         // Crear un nuevo contacto
         Document historialCliente = new Document("_id",+ idHistorial)
         .append("fecha", ""+LocalDate.now())
         .append("dni", "" + dniHistorial)
         .append("motivo", "" + motivoHistorial)
         .append("problema", "" + problemaHistorial)
         .append("reparacion", "" + repacionHistorial)
         .append("solucionado", "" + solucionadoHistorial);
         try{
             historialClientedb.insertOne(historialCliente);
         }catch(Exception e){
             System.out.println("error en la inserción, revise la no duplicidad de los datos");
         }
    }
}
