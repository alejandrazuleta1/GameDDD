package co.com.sofka.gameddd.Juego.values;

import co.com.sofka.domain.generic.ValueObject;
import co.com.sofka.gameddd.Jugadores.entities.Conductor;

import java.util.Objects;

public class Carril implements ValueObject<Conductor> {
    private final Conductor conductor;

    public Carril(Conductor conductor) {
        this.conductor = Objects.requireNonNull(conductor,"El conductor no puede ser nulo");
    }

    @Override
    public Conductor value() {
        return conductor;
    }
}
