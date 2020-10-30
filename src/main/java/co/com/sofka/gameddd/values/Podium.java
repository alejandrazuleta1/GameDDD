package co.com.sofka.gameddd.values;

import co.com.sofka.domain.generic.ValueObject;
import co.com.sofka.gameddd.entities.Conductor;

public class Podium implements ValueObject<Podium.Propiedades> {

    Conductor primerLugar;
    Conductor segundoLugar;
    Conductor tercerLugar;

    public interface Propiedades {
        Conductor primerLugar();
        Conductor segundoLugar();
        Conductor tercerLugar();
    }

    @Override
    public Propiedades value() {
        return new Propiedades() {
            @Override
            public Conductor primerLugar() {
                return primerLugar;
            }

            @Override
            public Conductor segundoLugar() {
                return segundoLugar;
            }

            @Override
            public Conductor tercerLugar() {
                return tercerLugar;
            }
        };
    }
}
