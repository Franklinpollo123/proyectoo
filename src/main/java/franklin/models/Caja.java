package franklin.models;
import java.util.ArrayList;
import java.util.List;
import franklin.models.*;

public class Caja {
    private final String nombre;
    private final boolean esPlataforma;
    private final List<Cliente> clientesAtendidos;
    private Cliente clienteActual = null;
    private int tiempoRestanteAtencion = 0;

    public Caja(String nombre, boolean esPlataforma) {
        this.nombre = nombre;
        this.esPlataforma = esPlataforma;
        this.clientesAtendidos = new ArrayList<>();
    }

    public boolean estaLibre() {
        return clienteActual == null;
    }

    public void atenderCliente(Cliente cliente) {
        this.clienteActual = cliente;
        this.tiempoRestanteAtencion = cliente.getTiempoTransaccion();
        System.out.println("--> " + nombre + " comienza a atender a " + cliente.getTicket() + " (Duración: " + cliente.getTiempoTransaccion() + " min).");
    }

    public void actualizarAtencion() {
        if (!estaLibre()) {
            tiempoRestanteAtencion--;
            if (tiempoRestanteAtencion <= 0) {
                System.out.println("<-- " + nombre + " finalizó de atender a " + clienteActual.getTicket() + ". Caja libre.");
                clientesAtendidos.add(clienteActual);
                clienteActual = null;
            }
        }
    }

    public boolean puedeAtender(char prioridad) {
        if (esPlataforma) {
            return prioridad == 'E';
        }
        return prioridad != 'E'; 
    }


    public String getNombre() { return nombre; }
    public int getTotalClientesAtendidos() { return clientesAtendidos.size(); }
    public double getPromedioEspera() {
        if (clientesAtendidos.isEmpty()) return 0.0;
        int sumaTiempos = 0;
        for (Cliente c : clientesAtendidos) {
            sumaTiempos += c.getTiempoEnFila();
        }
        return (double) sumaTiempos / clientesAtendidos.size();
    }
    public List<Cliente> getClientesAtendidos() { return clientesAtendidos; }
}