package franklin.models;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import franklin.models.*;

public class SistemaBanco {
    
    private static final Map<Character, Integer> PRIORIDADES = new HashMap<>();
    static {
        PRIORIDADES.put('A', 1); 
        PRIORIDADES.put('B', 2); 
        PRIORIDADES.put('C', 3); 
        PRIORIDADES.put('D', 4); 
        PRIORIDADES.put('E', 5); 
        PRIORIDADES.put('F', 6);
        PRIORIDADES.put('G', 7); 
    }

    private final Queue<Cliente> filaEspera;
    private final List<Caja> cajas;
    private final List<Cliente> clientesSinAtender;
    private final List<Cliente> todosLosClientes;

    public SistemaBanco() {
       
        this.filaEspera = new PriorityQueue<>(Comparator.comparing(c -> PRIORIDADES.get(c.getPrioridad())));
        this.cajas = new ArrayList<>();
        this.clientesSinAtender = new ArrayList<>();
        this.todosLosClientes = new ArrayList<>();

       
        for (int i = 1; i <= 5; i++) {
            cajas.add(new Caja("Caja " + i, false));
        }
        cajas.add(new Caja("Plataforma de Servicios", true));
    }

    public void iniciarSimulacion() {
        System.out.println("INICIANDO SIMULACIÓN BANCARIA...");
        generarClientes(25);
        int tiempoSimulado = 0;

       
        while (!filaEspera.isEmpty() || cajas.stream().anyMatch(caja -> !caja.estaLibre())) {
            tiempoSimulado++;
            System.out.println("\n--- Minuto de Simulación: " + tiempoSimulado + " ---");
            System.out.println("Clientes en fila: " + filaEspera.size());

            
            actualizarTiemposDeEspera();

            
            asignarClientesACajas();

          
            for (Caja caja : cajas) {
                caja.actualizarAtencion();
            }

         
            try {
                Thread.sleep(100); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("\n\n--- SIMULACIÓN FINALIZADA ---");
        System.out.println("Tiempo total de simulación: " + tiempoSimulado + " minutos.");
        generarReportes();
    }

    private void generarClientes(int cantidad) {
        System.out.println("Generando " + cantidad + " clientes...");
        for (int i = 0; i < cantidad; i++) {
            if (filaEspera.size() >= 25) { 
                System.out.println("Fila llena. No se pueden agregar más clientes.");
                break;
            }
            
            boolean esAdultoMayor = ThreadLocalRandom.current().nextDouble() < 0.1; 
            boolean esEmbarazada = !esAdultoMayor && ThreadLocalRandom.current().nextDouble() < 0.1;
            boolean conDiscapacidad = !esAdultoMayor && !esEmbarazada && ThreadLocalRandom.current().nextDouble() < 0.1;
            boolean multiplesAsuntos = ThreadLocalRandom.current().nextDouble() < 0.15;
            boolean esPlataforma = ThreadLocalRandom.current().nextDouble() < 0.2;
            char genero = ThreadLocalRandom.current().nextBoolean() ? 'F' : 'G';

            Cliente nuevoCliente = new Cliente(esAdultoMayor, esEmbarazada, conDiscapacidad, multiplesAsuntos, esPlataforma, genero);
            filaEspera.add(nuevoCliente);
            todosLosClientes.add(nuevoCliente);
            System.out.println("Llega un nuevo cliente: " + nuevoCliente);
        }
    }

    private void actualizarTiemposDeEspera() {
        Iterator<Cliente> iterador = filaEspera.iterator();
        while (iterador.hasNext()) {
            Cliente cliente = iterador.next();
            cliente.incrementarTiempoEnFila();
            if (cliente.alcanzoTolerancia()) { 
                System.out.println("¡CLIENTE SE FUE! " + cliente.getTicket() + " superó su tiempo de tolerancia de " + cliente.getTiempoTolerancia() + " min.");
                clientesSinAtender.add(cliente);
                iterador.remove();
            }
        }
    }

    private void asignarClientesACajas() {
        for (Caja caja : cajas) {
            if (caja.estaLibre()) {
              
                Iterator<Cliente> iterador = filaEspera.iterator();
                while(iterador.hasNext()) {
                    Cliente cliente = iterador.next();
                    if (caja.puedeAtender(cliente.getPrioridad())) {
                        caja.atenderCliente(cliente);
                        iterador.remove(); 
                        break;
                    }
                }
            }
        }
    }

    private void generarReportes() {
        System.out.println("\n================== REPORTE FINAL ==================");
        System.out.println("Total de clientes que ingresaron al banco: " + todosLosClientes.size());
        System.out.println("");
       

        
        System.out.println("\n--- Clientes Atendidos por Cajero ---");
        int totalAtendidos = 0;
        for (Caja caja : cajas) {
            totalAtendidos += caja.getTotalClientesAtendidos();
            System.out.println(caja.getNombre() + ": " + caja.getTotalClientesAtendidos() + " clientes.");
        }

        
        System.out.println("\n--- Promedio de Tiempo de Espera por Cajero (minutos) ---");
        for (Caja caja : cajas) {
            System.out.printf("%s: %.2f min en promedio.\n", caja.getNombre(), caja.getPromedioEspera());
        }

        
        System.out.println("\n--- Resumen de Atención ---");
        System.out.println("Total de clientes atendidos: " + totalAtendidos);
        System.out.println("Total de clientes que se fueron sin ser atendidos: " + clientesSinAtender.size());

        
        if (!clientesSinAtender.isEmpty()) {
            System.out.println("\n--- Detalle de Clientes no Atendidos ---");
            for (Cliente cliente : clientesSinAtender) {
                System.out.println(" - " + cliente.getTicket() + " (Tolerancia: " + cliente.getTiempoTolerancia() + " min, Esperó: " + cliente.getTiempoEnFila() + " min)");
            }
        }

        
        System.out.println("\n--- Clientes Atendidos por Categoría ---");
        Map<Character, Integer> conteoPorCategoria = new TreeMap<>();
        for (Caja caja : cajas) {
            for (Cliente cliente : caja.getClientesAtendidos()) {
                conteoPorCategoria.merge(cliente.getPrioridad(), 1, Integer::sum);
            }
        }
        for (Map.Entry<Character, Integer> entry : conteoPorCategoria.entrySet()) {
            System.out.println("Categoría " + entry.getKey() + ": " + entry.getValue() + " clientes atendidos.");
        }
        System.out.println("===================================================");
    }
}