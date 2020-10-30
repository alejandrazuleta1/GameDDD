package co.com.sofka.gameddd.values;

import co.com.sofka.domain.generic.Identity;

public class IdConductor extends Identity {
    public IdConductor(String value){
        super(value);
    }
    public static IdConductor of(String value){
        return new IdConductor(value);
    }
}
