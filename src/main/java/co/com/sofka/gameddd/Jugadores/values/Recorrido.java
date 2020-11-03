package co.com.sofka.gameddd.Jugadores.values;

import co.com.sofka.domain.generic.ValueObject;

import java.util.Objects;

public class Recorrido implements ValueObject<Integer> {
    private final Integer value;

    public Recorrido(Integer value) {
        this.value = Objects.requireNonNull(value,
                "La distancia recorrida no puede ser nula");
    }

    @Override
    public Integer value() {
        return value;
    }
}
