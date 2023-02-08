import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.logging.Level;

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
            System.out.println("4. Info tecnicos");
            System.out.println("5. Salir");
            System.out.print("Opcion: ");
            opcion = Integer.parseInt(sc.nextLine());

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
                    infoTecnicos(clientedb, historialClientedb, sc);
                    break;
                case 5:
                    System.out.println("Saliendo...");
                    break;
            }
        } while (opcion != 4);
        
        // Cerrar la conexión
        mongoClient.close();
    }

    private static void infoTecnicos(MongoCollection<Document> clientedb, MongoCollection<Document> historialClientedb, Scanner sc) {

        System.out.println("--- Dirección ---");
        System.out.println("1. Numero de llamadas recibidas totales");
        System.out.println("2. Llamadas en una fecha dada");
            System.out.println("mostrar numero de hardware");
            System.out.println("mostrar numero de software");
            System.out.println("mostrar numero solucionaron problema");
            System.out.println("mostrar numero necesitaron reparacion");
        System.out.print("Opcion: ");
        int op = Integer.parseInt(sc.nextLine());
        if(op == 1){
            //llamadas recibidas totales
            long llamadasTotales = historialClientedb.countDocuments();
            System.out.println("El número de llamadas totales ha sido: "+llamadasTotales);
        } else if(op == 2){
            //llamadas en una fecha dada

            boolean parseado = true;
            do {
                System.out.println("introduce una fecha válida");
                String date = sc.next();
                if (date.equals("salir")){
                    parseado = false;
                }
                try{
                    /*LocalDate fecha = LocalDate.parse(date);
                    LocalDate timestamp = LocalDate.from(fecha);
                    System.out.println(timestamp);*/
                    for (Document doc : historialClientedb.find(eq("fecha",date))) {
                        System.out.println(doc.toJson());
                    }
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
        String telefonoCliente = sc.nextLine();
        
        // Crear un nuevo contacto
        Document cliente = new Document("_id", "" + dniCliente)
                .append("nombre", "" + nombreCliente)
                .append("apellidos", "" + apellidoCliente)
                .append("telefono",Integer.parseInt(telefonoCliente));
            clientedb.insertOne(cliente);
        

        System.out.println("--- Historial de "+ nombreCliente + " ----");

        System.out.print("Id de la incidencia: ");
        String idHistorial = sc.nextLine();

        System.out.println("Fecha de hoy :"+LocalDate.now());

        System.out.print("Motivo: ");
        String motivoHistorial = sc.nextLine();
        
        String problemaHistorial = null;
        System.out.println("--- Problema --- ");
        System.out.println("1. hardware");
        System.out.println("2. software");
        System.out.print("Opcion: ");
        int op = Integer.parseInt(sc.nextLine());
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
        op = Integer.parseInt(sc.nextLine());;
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
        op = Integer.parseInt(sc.nextLine());
        if(op == 1){
            solucionadoHistorial = "true";
        } else if(op == 2) {
            solucionadoHistorial = "false";
        }else {
            System.err.println("Error");
        }
       
         // Crear un nuevo contacto
         Document historialCliente = new Document("_id",+ Integer.parseInt(idHistorial))
         .append("fecha",""+ LocalDate.now())
         .append("dni", "" + dniCliente)
         .append("motivo", "" + motivoHistorial)
         .append("problema", "" + problemaHistorial)
         .append("reparacion", "" + repacionHistorial)
         .append("solucionado", "" + solucionadoHistorial);
         historialClientedb.insertOne(historialCliente);
    }

    public static void verCliente(MongoCollection<Document> clientedb,MongoCollection<Document> historialClientedb, Scanner sc){
        System.out.print("Diga el dni del cliente que quiere consultar: ");
        String dniCliente = sc.nextLine();

        Document nombreBuscar = clientedb.find(eq("_id", ""+dniCliente)).first();
        System.out.println("-------- Datos de "+ dniCliente +" --------");
        System.out.println(nombreBuscar.toJson());
        

        System.out.println("-------- Historial de "+ dniCliente +" --------");
        for (Document doc : historialClientedb.find(eq("dni", ""+dniCliente))) {
            System.out.println(doc.toJson());
        }
    }

    public static void agregarHistorial(MongoCollection<Document> clientedb,MongoCollection<Document> historialClientedb, Scanner sc){
        
        System.out.print("Dni del cliente para poner la incidencia: ");
        String dniHistorial = sc.nextLine();

        System.out.print("Id de la incidencia: ");
        String idHistorial = sc.nextLine();

        System.out.print("Motivo: ");
        String motivoHistorial = sc.nextLine();
        
        String problemaHistorial = null;
        System.out.println("--- Problema --- ");
        System.out.println("1. hardware");
        System.out.println("2. software");
        System.out.print("Opcion: ");
        int op = Integer.parseInt(sc.nextLine());
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
        op = Integer.parseInt(sc.nextLine());;
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
        op = Integer.parseInt(sc.nextLine());
        if(op == 1){
            solucionadoHistorial = "true";
        } else if(op == 2) {
            solucionadoHistorial = "false";
        }else {
            System.err.println("Error");
        }
       
         // Crear un nuevo contacto
         Document historialCliente = new Document("_id",+ Integer.parseInt(idHistorial))
         .append("fecha", LocalDate.now())
         .append("dni", "" + dniHistorial)
         .append("motivo", "" + motivoHistorial)
         .append("problema", "" + problemaHistorial)
         .append("reparacion", "" + repacionHistorial)
         .append("solucionado", "" + solucionadoHistorial);
         historialClientedb.insertOne(historialCliente);
    }
}
