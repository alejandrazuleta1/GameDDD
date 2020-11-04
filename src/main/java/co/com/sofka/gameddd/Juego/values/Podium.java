package co.com.sofka.gameddd.Juego.values;

import co.com.sofka.domain.generic.ValueObject;

public class Podium implements ValueObject<Podium.Propiedades> {

    String primerLugar;
    String segundoLugar;
    String tercerLugar;

    public void agregar(String nombreConductor){
        if (primerLugar == null){
            primerLugar = nombreConductor;
        }else if (segundoLugar == null){
            segundoLugar = nombreConductor;
        }else {
            tercerLugar = nombreConductor;
        }
    }

    public interface Propiedades {
        String primerLugar();
        String segundoLugar();
        String tercerLugar();
    }

    @Override
    public Propiedades value() {
        return new Propiedades() {
            @Override
            public String primerLugar() {
                return primerLugar;
            }

            @Override
            public String segundoLugar() {
                return segundoLugar;
            }

            @Override
            public String tercerLugar() {
                return tercerLugar;
            }
        };
    }
}
