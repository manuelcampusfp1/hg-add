import java.util.Scanner;

public class helpers {
    public static int pedirEntero(Scanner sc){
        boolean salir = false;
        int miNumero = 0;
        do{
            String num = sc.nextLine();
            try{
                miNumero = Integer.parseInt(num);
                salir = true;
                return miNumero;
            }catch(NumberFormatException e){
                System.out.println("formato de número no válido, introduzca un número válido");
                salir = false;
            }
        }while(!salir);
        return miNumero;
    }
}
