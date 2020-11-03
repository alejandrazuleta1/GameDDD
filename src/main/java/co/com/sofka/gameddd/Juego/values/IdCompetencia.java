package co.com.sofka.gameddd.Juego.values;

import co.com.sofka.domain.generic.Identity;

public class IdCompetencia extends Identity {
    public IdCompetencia(String value){
        super(value);
    }
    public static IdCompetencia of(String value){
        return new IdCompetencia(value);
    }
}
