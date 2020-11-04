package co.com.sofka.gameddd.Jugadores.entities;

import co.com.sofka.domain.generic.Entity;
import co.com.sofka.gameddd.Jugadores.values.IdCarro;
import co.com.sofka.gameddd.Jugadores.values.Recorrido;

import java.util.Objects;

public class Carro extends Entity<IdCarro> {

    private Recorrido recorrido;

    public Carro(IdCarro entityId, Recorrido recorrido) {
        super(entityId);
        this.recorrido = Objects.requireNonNull(recorrido);
    }

    public void avanzar(Integer distanciaAvanzar){
        recorrido = new Recorrido(recorrido.value() + distanciaAvanzar);
    }

    public Recorrido getRecorrido() {
        return recorrido;
    }
}
