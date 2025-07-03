package franklin.models;


import java.util.concurrent.ThreadLocalRandom;

public class Cliente {
    private static int proximoId = 1;
    private final int id;
    private final String ticket;
    private final int tiempoTransaccion; 
    private int tiempoTolerancia;      
    private int tiempoEnFila = 0;

    public Cliente(boolean esAdultoMayor, boolean esEmbarazada, boolean conDiscapacidad, boolean multiplesAsuntos, boolean esPlataforma, char genero) {
        this.id = proximoId++;
        this.tiempoTransaccion = ThreadLocalRandom.current().nextInt(10, 121);
        this.tiempoTolerancia = ThreadLocalRandom.current().nextInt(5, 151);
        this.ticket = asignarTicket(esAdultoMayor, esEmbarazada, conDiscapacidad, multiplesAsuntos, esPlataforma, genero);
    }

    private String asignarTicket(boolean esAdultoMayor, boolean esEmbarazada, boolean conDiscapacidad, boolean multiplesAsuntos, boolean esPlataforma, char genero) {
        if (esAdultoMayor) return id + "-A";
        if (esEmbarazada) return id + "-B";
        if (conDiscapacidad) return id + "-C";
        if (multiplesAsuntos) return id + "-D";
        if (esPlataforma) return id + "-E";
        return id + (genero == 'F' ? "-F" : "-G");
    }

    // Getters
    public int getId() { return id; }
    public String getTicket() { return ticket; }
    public char getPrioridad() { return ticket.charAt(ticket.length() - 1); }
    public int getTiempoTransaccion() { return tiempoTransaccion; }
    public int getTiempoTolerancia() { return tiempoTolerancia; }
    public int getTiempoEnFila() { return tiempoEnFila; }

    // Métodos para la simulación
    public void incrementarTiempoEnFila() { this.tiempoEnFila++; }
    public boolean alcanzoTolerancia() { return this.tiempoEnFila >= this.tiempoTolerancia; }

    @Override
    public String toString() {
        return "Cliente{" + "ticket='" + ticket + '\'' + ", transaccion=" + tiempoTransaccion + " min, tolerancia=" + tiempoTolerancia + " min}";
    }
}