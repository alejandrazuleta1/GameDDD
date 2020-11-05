package co.com.sofka.gameddd.Juego.values;

import co.com.sofka.domain.generic.Identity;

public class IdPista extends Identity {
    public IdPista(String value){
        super(value);
    }
    public static IdPista of(String value){
        return new IdPista(value);
    }
}
