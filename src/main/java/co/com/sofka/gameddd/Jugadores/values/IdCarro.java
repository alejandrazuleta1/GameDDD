package co.com.sofka.gameddd.Jugadores.values;

import co.com.sofka.domain.generic.Identity;

public class IdCarro extends Identity {
    public IdCarro(String value){
        super(value);
    }
    public static IdCarro of(String value){
        return new IdCarro(value);
    }
}
