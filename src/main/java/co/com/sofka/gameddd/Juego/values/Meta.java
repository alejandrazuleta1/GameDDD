package co.com.sofka.gameddd.Juego.values;

import co.com.sofka.domain.generic.ValueObject;

import java.util.Objects;

public class Meta implements ValueObject<Integer> {
    private final Integer distancia;

    public Meta(Integer value) {
        this.distancia = Objects.requireNonNull(value, "La distancia meta no puede ser nula");
    }

    @Override
    public Integer value() {
        return distancia;
    }
}
