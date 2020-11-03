package co.com.sofka.gameddd.Jugadores.events;

import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.gameddd.Jugadores.entities.Carro;
import co.com.sofka.gameddd.Jugadores.values.IdConductor;

public class JugadorAdicionado extends DomainEvent {
    private final IdConductor idConductor;
    private final String nombre;
    private final Carro carro;

     public JugadorAdicionado(IdConductor idConductor, String nombre, Carro carro) {
        super("gameddd.ConductorAdicionado");
        this.nombre = nombre;
        this.carro = carro;
        this.idConductor = idConductor;
    }

    public IdConductor getIdConductor() {
        return idConductor;
    }

    public String getNombre() {
        return nombre;
    }

    public Carro getCarro() {
        return carro;
    }
}
