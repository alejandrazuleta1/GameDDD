package co.com.sofka.gameddd.Juego.events;

import co.com.sofka.domain.generic.DomainEvent;

public class ConductorFinalizoRecorrido extends DomainEvent {
    private String nombreConductor;

    public ConductorFinalizoRecorrido(String nombreConductor) {
        super("gameddd.Juego.events.AdicionadoAlPodium");
        this.nombreConductor = nombreConductor;
    }

    public String getNombreConductor() {
        return nombreConductor;
    }
}
