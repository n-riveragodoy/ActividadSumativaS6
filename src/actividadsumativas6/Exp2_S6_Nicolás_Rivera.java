package actividadsumativas6;

/**
 *
 * @author nrivera
 */

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

class Teatro {
    
    //Variables de clase
    static int totalTeatrosCreados = 0;
    
    //Variables de instancia (no static)
    String nombre;
    int totalEntradasVendidas;
    int totalIngresos;
    int totalAsientos;
    int totalAsientosOcupados;
    ArrayList<Entrada> listaEntradasVendidas;
    ArrayList<Integer> asientosVendidos;
    ArrayList<Integer> asientosOcupados;
    ArrayList<Integer> asientosDisponibles;
    
    //Constructor de teatro
    Teatro(String nombre) {
        
        this.nombre = nombre;
        this.totalAsientos = 150;
        this.totalAsientosOcupados = 0;
        this.totalEntradasVendidas = 0;
        this.listaEntradasVendidas = new ArrayList<>();
        this.asientosVendidos = new ArrayList <>();
        this.asientosDisponibles = new ArrayList <>();
        this.asientosOcupados = new ArrayList <>();
        this.totalIngresos = 0;
        
        totalTeatrosCreados++;
    }
    

    static int MostrarMenu(Scanner scanner) {
        
        //Variable Local
        int opcion;
        
        System.out.println("\nMenú principal");
        System.out.println("--------------");
        System.out.println("1.- Reservar entradas");
        System.out.println("2.- Comprar entradas");
        System.out.println("3.- Modificar venta existente");
        System.out.println("4.- Imprimir boleta");
        System.out.println("5.- Salir\n");
        System.out.println("Presione el número que corresponde a su elección");
        opcion = Validador.ValidarEntradaInt(scanner.nextInt(), 1, 5, scanner);
        return opcion;
        
    }    
    
    void SeleccionarTipoEntrada (Entrada entrada, Scanner scanner) {

        MostrarTipoEntradasTarifas();
        
        System.out.println("\nIngrese el tipo de entrada que desea");
        entrada.tipoEntrada = Validador.ValidarEntradaString(scanner.next(), scanner);
        
        while ((!(entrada.tipoEntrada.equalsIgnoreCase("VIP")) && (!(entrada.tipoEntrada.equalsIgnoreCase("Platea"))) && !(entrada.tipoEntrada.equalsIgnoreCase("General")) )) {
            
            System.out.println("\nTipo de entrada no válido");
            System.out.println("Por favor ingrese tipo de entrada: Vip, Platea o General");
            entrada.tipoEntrada = Validador.ValidarEntradaString(scanner.next(), scanner);
       
        }    
    }
    
    void SeleccionarAsiento(Entrada entrada, Scanner scanner) {
        
        int rangoInicio = 0;
        int rangoFin = 0;

        int tercio = totalAsientos / 3;

        switch (entrada.tipoEntrada) {
            case "Vip":
                rangoInicio = 1;
                rangoFin = tercio;
                break;
            case "Platea":
                rangoInicio = tercio + 1;
                rangoFin = tercio * 2;
                break;
            case "General":
                rangoInicio = (tercio * 2) + 1;
                rangoFin = totalAsientos;
                break;
        }        
            
        MostrarAsientosDisponiblesPorTipoEntrada(entrada, rangoInicio, rangoFin);
        
        System.out.println("\nIngrese el número de la entrada");
        entrada.numeroEntrada = Validador.ValidarEntradaInt(scanner.nextInt(), rangoInicio, rangoFin, scanner);
        
        while (!asientosDisponibles.contains(entrada.numeroEntrada)) {
            System.out.println("El asiento que escogio no se encuentra disponible");
            System.out.println("Ingrese el numero de un asiento disponible");
            entrada.numeroEntrada = Validador.ValidarEntradaInt(scanner.nextInt(), rangoInicio,rangoFin, scanner);
        }
        
        asientosDisponibles.remove(Integer.valueOf(entrada.numeroEntrada));
        asientosOcupados.add(entrada.numeroEntrada);
        
    }
    
    
    void ReservarAsiento(Cliente cliente, Entrada entrada, Scanner scanner) {

        int[] segundos = {140}; 
        boolean[] confirmado = {false};
        Timer timer = new Timer();
        
        if ( (int) segundos[0]/60 > 1) {
            System.out.println("\nLa reserva de tu entrada tiene un tiempo limite de " + (int) segundos[0]/60 + " minutos");
        } else {
            System.out.println("\nLa reserva de tu entrada tiene un tiempo limite  de " + (int) segundos[0]/60 + " minuto para realizar");
        }
        
        System.out.println("Realiza tu compra antes de que se acabe el tiempo, alguien más podria quedarse con tu entrada!");
        
        TimerTask temporizador = new TimerTask() {
            @Override
            public void run() {
                if (segundos[0] == 5) {
                    System.out.println("\nQuedan solo 5 segundos...");
                }

                if (segundos[0] <= 0 && !confirmado[0]) {
                    System.out.println(asientosOcupados);
                    System.out.println("Tiempo terminado, se anula la reserva.");
                    asientosOcupados.remove(Integer.valueOf(entrada.numeroEntrada));
                    asientosDisponibles.add(entrada.numeroEntrada);
                    System.out.println("Asientos ocupados ahora: " + asientosOcupados);
                    timer.cancel();
                }

                segundos[0]--;
            }
        };

        timer.schedule(temporizador, 1000, 1000);
        
        MostrarPromociones();
        
        SeleccionarTipoEntrada(entrada, scanner);

        SeleccionarAsiento(entrada, scanner);

        System.out.println("\n¿Desea confirmar su compra?");
        System.out.println("1. Confirmar");
        System.out.println("2. Cancelar");

        int opcion = Validador.ValidarEntradaInt(scanner.nextInt(), 1, 2, scanner);

        switch (opcion) {
            case 1:
                VenderEntrada(cliente, entrada);
                System.out.println("\nEntrada vendida con éxito.");
                confirmado[0] = true;
                timer.cancel();
                break;
            case 2:
                System.out.println("Has cancelado la reserva.");
                asientosOcupados.remove(Integer.valueOf(entrada.numeroEntrada));
                asientosDisponibles.add(entrada.numeroEntrada);       
                timer.cancel();
                break;
        }
    }

    void VenderEntrada(Cliente cliente, Entrada entrada) {
        
        
        entrada.DefinirPrecioEntrada();
        AplicarDescuentos(cliente, entrada);
        
        entrada.MostrarEntrada();
        
        Entrada nuevaEntrada = new Entrada();
        nuevaEntrada.numeroEntrada = entrada.numeroEntrada;
        nuevaEntrada.tipoEntrada = entrada.tipoEntrada;
        nuevaEntrada.precioEntrada = entrada.precioEntrada;
        
        asientosVendidos.add(nuevaEntrada.numeroEntrada);
        cliente.entradasCompradas.add(nuevaEntrada);
        listaEntradasVendidas.add(nuevaEntrada);
        totalEntradasVendidas++;
        totalIngresos += entrada.precioEntrada;
    }
    
    void MostrarBoleta(Cliente cliente) {
        
        Entrada copiaEntrada;
        int[] ubicaciones = new int[cliente.entradasCompradas.size()];
        int costoTotal = 0;
        
        for (int i = 0; i < cliente.entradasCompradas.size(); i++) {
            copiaEntrada = cliente.entradasCompradas.get(i);
            ubicaciones[i] = (copiaEntrada.numeroEntrada);
            costoTotal += copiaEntrada.precioEntrada;
        }
        
        cliente.MostrarCliente();
        
        System.out.println("\nBoleta");
        System.out.println("------");
        System.out.println("Ubicación de los asientos: " + Arrays.toString(ubicaciones));
        System.out.println("Cantidad de entradas: " + cliente.entradasCompradas.size());
        System.out.println("Costo total: $" + costoTotal + "CLP");
        
    }

    void MostrarAsientosDisponiblesPorTipoEntrada(Entrada entrada ,int rangoInicio, int rangoFin) {
        
        int contador = 0;
        int asientoDisponible;

        System.out.println("\nAsientos disponibles para la zona " + entrada.tipoEntrada);
        System.out.println("------------------------------------------");

        for (int i = 0; i < asientosDisponibles.size(); i++) {
            asientoDisponible = asientosDisponibles.get(i);

            if (!asientosOcupados.contains(asientoDisponible)
                    && asientoDisponible >= rangoInicio
                    && asientoDisponible <= rangoFin) {

                System.out.print("|" + asientoDisponible + "| ");
                contador++;

                if (contador % 10 == 0) {
                    System.out.println();
                }
            }
        }

        System.out.println();
    }    
    
         
    void MostrarAsientos() {

        //Variable local
        int contador = 0;

        System.out.println("\nAsientos disponibles");
        System.out.println("-----------------");
        for (int j = 0; j < asientosDisponibles.size(); j++) {

            if (!(asientosOcupados.contains(asientosDisponibles.get(j)))) {
                System.out.print("|" + asientosDisponibles.get(j) + "| "); 
                contador++;

                if (contador % 10 == 0 ) {
                    System.out.println(); 
                }
            }
        }
        System.out.println();
    }
    
    void MostrarAsientosVendidos(ArrayList<Integer> asientosVendidos) {

        ArrayList<Integer> asientosOrdenados = new ArrayList<>(asientosVendidos);
        Collections.sort(asientosOrdenados);

        System.out.println("\nAsientos Vendidos");
        System.out.println("---------------------------");

        int contador = 0;
        for (int i = 0; i < asientosOrdenados.size(); i++) {
            int asiento = asientosOrdenados.get(i); 
            System.out.print("|" + asiento + "| ");
            contador++;

            if (contador % 10 == 0) {
                System.out.println();
            }
        }

        System.out.println();
    }
        
        
    void MostrarTipoEntradasTarifas() {

        System.out.println("\nTarifas por tipo de entrada");
        System.out.println("---------------------------");
        for (int j = 0; j < Entrada.tipoEntradas.length; j++) { 
            System.out.print((j + 1) + ".- " + Entrada.tipoEntradas[j] + "   $" + Entrada.tarifas[j]  + " CLP");
            System.out.print("   Asientos del " + ((totalAsientos / Entrada.tipoEntradas.length) * (j) + 1)+ " hasta el " + (totalAsientos / Entrada.tipoEntradas.length) * (j+1));
            System.out.println();

        }
    }
    
    void AplicarDescuentos(Cliente cliente, Entrada entrada) {
        
        //Variable local
        double descuento;
        
        if (cliente.edad >= 60) {
            System.out.println("Felicidades por ser de la tercera edad tienes un 15% de descuento en la compra de tu entrada");
            descuento = 15.0 / 100.0;
            totalIngresos = totalIngresos - (int) (entrada.precioEntrada * descuento);
            entrada.precioEntrada = (int) (entrada.precioEntrada * (1 - descuento));
        } else if (cliente.edad <= 23) {            
            System.out.println("Felicidades por ser estudiante tienes un 10% de descuento en la compra de tu entrada");
            descuento = 10.0 / 100.0;
            totalIngresos = totalIngresos - (int) (entrada.precioEntrada * descuento);
            entrada.precioEntrada = (int) (entrada.precioEntrada * (1 - descuento));
        }  
    }
    
    void MostrarPromociones() {
        
        System.out.println("\nPromociones");
        System.out.println("-----------");
        System.out.println("Tenemos 10% de descuento para estudiantes y 15% de descuento para la tercera edad");
        System.out.println("Si compras 5 o más entradas te llevaras un producto del merch oficial de la banda");
        
    }
    
    Entrada BuscarEntradaVendida(Scanner scanner) {
        int numero;
        boolean ciclo = true;
        Entrada entradaBuscada = new Entrada();
        
        MostrarAsientosVendidos(asientosVendidos);
        
        System.out.println();
        do {
            System.out.println("Ingrese el número de la entrada");
            numero = Validador.ValidarEntradaInt(scanner.nextInt(), 1, totalAsientos, scanner);
            
            MostrarAsientosVendidos(asientosVendidos);
            
            for (int i = 0; i < listaEntradasVendidas.size(); i++) {
                entradaBuscada = listaEntradasVendidas.get(i);
                if (numero == entradaBuscada.numeroEntrada) {
                    System.out.println("\nLa entrada fue encontrada con éxito");
                    entradaBuscada.MostrarEntrada();
                    ciclo = false;
                    break;
                } 
            }
            
            if (ciclo) {
                System.out.println("\nLa entrada indicada no esta registrada en la base de datos");
            }
            
        } while (ciclo);
        return entradaBuscada;
    }
    
    
    void EliminarEntrada(Scanner scanner) {

        int indice;
        int opcion;
        boolean continuar;

        do {
            continuar = false;

            if (listaEntradasVendidas.size() == 0) {
                System.out.println("\nNo hay entradas registradas para eliminar.");
                return;
            }

            System.out.println("\nIngrese el número de la entrada que desea eliminar (posición en la lista)");
            indice = Validador.ValidarEntradaInt(scanner.nextInt(), 1, listaEntradasVendidas.size(), scanner);

            Entrada entradaEliminar = listaEntradasVendidas.get(indice - 1);

            System.out.println("Entrada a eliminar:");
            entradaEliminar.MostrarEntrada(); // 

            System.out.println("\n¿Está seguro que desea eliminar esta entrada?");
            System.out.println("1.- Sí\n2.- No");
            System.out.println("Presione el número de su elección:");
            opcion = Validador.ValidarEntradaInt(scanner.nextInt(), 1, 2, scanner);

            if (opcion == 1) {
                totalEntradasVendidas--;
                totalIngresos -= entradaEliminar.precioEntrada;
                listaEntradasVendidas.remove(indice - 1);
                System.out.println("La entrada fue eliminada con éxito\n");
            } else {
                System.out.println("\n1.- Eliminar otra entrada\n2.- Salir");
                System.out.println("Presione el número de su elección:");
                opcion = Validador.ValidarEntradaInt(scanner.nextInt(), 1, 2, scanner);
                continuar = (opcion == 1);
            }

        } while (continuar);
    }

    void ModificarEntradaExistente(Cliente cliente, Scanner scanner) {
        
        int opcion;
        
        Entrada entradaModificada = new Entrada();
        entradaModificada = BuscarEntradaVendida(scanner);
               
        
        System.out.println("\nModificar:");
        System.out.println("1.- Asiento");
        System.out.println("2.- Tipo de entrada");
        System.out.println("3.- Precio entrada\n");
        System.out.println("Presione el número de su elección");
        opcion = Validador.ValidarEntradaInt(scanner.nextInt(), 1, 3, scanner);
        
        switch (opcion) {
            case 1:
                System.out.println();
                System.out.println("Seleccionar nuevo asiento:");
                SeleccionarTipoEntrada(entradaModificada, scanner);
                SeleccionarAsiento(entradaModificada, scanner);
                entradaModificada.DefinirPrecioEntrada();
                AplicarDescuentos(cliente, entradaModificada);

                entradaModificada.MostrarEntrada();
                break;
            case 2:
                
                break;
            case 3:
                
                break;
        }      
        
    }

    
}

class Cliente {

    //Variables de clase (static)
    static int totalClientes;

    //Variables de instancia (no static)
    String nombre;
    int edad;
    ArrayList<Entrada> entradasCompradas;

    //Constructor de cliente
    Cliente(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;  
        this.entradasCompradas = new ArrayList<>();
        
        totalClientes++;
    }

    //Metodo para un cliente nuevo
    void RegistroCliente(Scanner scanner) {

        System.out.println("\nRegistro del cliente");
        System.out.println("--------------------");
        System.out.println("Ingrese su nombre");
        nombre = Validador.ValidarEntradaString(scanner.next(), scanner);
        System.out.println("\nIngrese su edad");
        edad = Validador.ValidarEntradaInt(scanner.nextInt(), 1,100, scanner);

    }

    void MostrarCliente() {
        System.out.println("\nDatos cliente");
        System.out.println("-------------");
        System.out.println("Nombre: " + nombre);
        System.out.println("Edad: " + edad);
    }

}


class Entrada {
    
    static String[] tipoEntradas = {"VIP      ", "Platea   ", "General  "};
    static int[] tarifas = {30000, 20000, 10000};
    
    //Variables de instancia (no static)
    int numeroEntrada; 
    String tipoEntrada; 
    int precioEntrada; 
    
    // Constructor de entrada
    Entrada() {
        this.numeroEntrada = 0;
        this.tipoEntrada = "";
        this.precioEntrada = 0;
    }
    
    void DefinirPrecioEntrada() {
        if (tipoEntrada.equalsIgnoreCase("VIP")) {
            precioEntrada = tarifas[0];
        } else if (tipoEntrada.equalsIgnoreCase("Platea")) {
            precioEntrada = tarifas[1];
        } else {
            precioEntrada = tarifas[2];
        }
    }   
    
    public void MostrarEntrada() {
        
        System.out.println("\nInformación de la entrada");
        System.out.println("-------------------------");
        System.out.println("Tipo: " + tipoEntrada);
        System.out.println("Número: " + numeroEntrada);
        System.out.println("Precio: $" + precioEntrada + " CLP");
        
    }
 
}

class Validador {

    public static int ValidarEntradaInt(int entradaInt, int limiteMin, int limiteMax, Scanner scanner) {
        
        while (entradaInt < limiteMin || entradaInt > limiteMax) {
            System.out.println("Entrada no válida");
            System.out.println("Ingrese un número entre " + limiteMin + " y " + limiteMax + ":");
            entradaInt = scanner.nextInt();
        }
        return entradaInt;
        
    }
    
    public static String ValidarEntradaString(String entradaString, Scanner scanner) {

        while (!entradaString.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ]+")) {
            System.out.print("Ingrese texto (solo letras, sin espacios): \n");
            entradaString = scanner.next();

            if (!entradaString.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ]+")) {
                System.out.println("\nEntrada no válida. Solo se permiten letras sin espacios.");
            }
        }

        entradaString = entradaString.substring(0, 1).toUpperCase() + entradaString.substring(1).toLowerCase();
        return entradaString;
        
    }
}
    
class Pausar {
    
    public static void Pausa(int segundos) {
        
        try { 
            Thread.sleep(segundos * 1000);
        } catch (InterruptedException e) {
            System.out.println("Error en la pausa");
        }
        
    }
}


public class Exp2_S6_Nicolás_Rivera {

    public static void LlenarListaConNumerosAleatorios(ArrayList <Integer> lista, int cantidad, int maxLista) {
        //Variables locales
        int cantidadAsientosOcupados = (int) (Math.random() * cantidad) + 1;
        int aleatorio;
        
        for (int i = 0; i < cantidadAsientosOcupados; i++) {   
 
            do {
                aleatorio = (int) (Math.random() * maxLista) + 1;  
            } while (lista.contains(aleatorio));  
            
            lista.add(aleatorio);
        }
    
    }

    public static void main(String[] args) {
        
        // Nuevo Scanner
        Scanner scanner = new Scanner(System.in);
        
        //Conttrucción de objetos
        Teatro teatro = new Teatro ("Teatro Moro");
        Cliente cliente = new Cliente (" ", 0);
        Entrada entrada = new Entrada ();

        // Variables del usuario
        int opcion;

        //llenar lista con numeros aleatorios para simular los asientos que ya se vendieron
        LlenarListaConNumerosAleatorios(teatro.asientosOcupados, 100, teatro.totalAsientos);

        //Llenar la lista con los asientos disponibles
        for (int i = 1; i < teatro.totalAsientos + 1; i++) {
            if (!(teatro.asientosOcupados.contains(i))) {
                teatro.asientosDisponibles.add(i);
            }
        }

        // Inicio programa
        System.out.println("Bienvenido al " + teatro.nombre);
        System.out.println("Evento: Concierto de Shakira");

        // Pausa antes de continuar
        Pausar.Pausa(2); 

        // Menú principal
        opcion = Teatro.MostrarMenu(scanner); 

        // Pausa antes de continuar
        Pausar.Pausa(2); 

        for (int i = 0; i < (teatro.totalAsientos - teatro.asientosOcupados.size()); i++) {
            if (i > 0) {

                // Pausa antes de continuar
                Pausar.Pausa(2); 

                opcion = Teatro.MostrarMenu(scanner); 

            }  

            // Salida
            if (opcion == 5) {
                if (teatro.totalIngresos == 0) {

                    System.out.println("\nVuelva pronto");
                    break;

                } else {

                    // Pausa antes de continuar
                    Pausar.Pausa(1); 

                    // Salida
                    System.out.println();
                    System.out.println("\nResumen de compra");
                    cliente.MostrarCliente();
                    System.out.println("Total a pagar: $" + teatro.totalIngresos + " CLP");

                    // Pausa antes de continuar
                    Pausar.Pausa(1); 

                    System.out.println();
                    System.out.println("Muchas gracias por su compra, disfrute la función");
                    System.out.println();

                    break;
                }

            }

            switch (opcion) {
                case 1:
                    if (i == 0) {
                        cliente.RegistroCliente(scanner);
                    }
                    
                    teatro.ReservarAsiento(cliente, entrada, scanner);

                    break;
                case 2:
                    if (i == 0) {
                        cliente.RegistroCliente(scanner);
                    }
                    
                    teatro.MostrarPromociones();
                    
                    teatro.SeleccionarTipoEntrada(entrada, scanner);

                    teatro.SeleccionarAsiento(entrada, scanner);
                    
                    teatro.VenderEntrada(cliente, entrada);
                    
                    System.out.println("\nEntrada vendida con éxito.");

                    break;
                case 3:
                    teatro.ModificarEntradaExistente(cliente, scanner);
                    break;
                case 4:
                    if (cliente.entradasCompradas.size() > 0) {
                       teatro.MostrarBoleta(cliente);
                    } else {
                        System.out.println("\nNo existen entradas registradas en la base de datos");
                    }
                    break;
            }

            // Pausa antes de continuar
            Pausar.Pausa(2); 

        }   
        
        // Cierre Scanner
        scanner.close();         
        
    }
    
}
