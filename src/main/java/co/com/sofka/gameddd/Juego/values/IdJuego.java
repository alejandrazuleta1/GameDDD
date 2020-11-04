package co.com.sofka.gameddd.Juego.values;

import co.com.sofka.domain.generic.Identity;
import co.com.sofka.gameddd.Jugadores.values.IdCarro;

public class IdJuego extends Identity {

    public IdJuego(String value){
        super(value);
    }
    public static IdJuego of(String value){
        return new IdJuego(value);
    }

}
