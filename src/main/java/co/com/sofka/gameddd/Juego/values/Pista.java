package co.com.sofka.gameddd.Juego.values;

import co.com.sofka.domain.generic.ValueObject;

import java.util.Objects;

public class Pista implements ValueObject<IdPista> {
    private final IdPista idPista;

    public Pista(IdPista idPista) {
        this.idPista = Objects.requireNonNull(idPista, "La pista no puede ser nula");
    }

    @Override
    public IdPista value() {
        return idPista;
    }
}
