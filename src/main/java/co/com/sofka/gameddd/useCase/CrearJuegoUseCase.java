package co.com.sofka.gameddd.useCase;

import co.com.sofka.business.generic.UseCase;
import co.com.sofka.business.support.RequestCommand;
import co.com.sofka.business.support.ResponseEvents;
import co.com.sofka.gameddd.Juego.Juego;
import co.com.sofka.gameddd.Juego.commands.IniciarJuego;
import co.com.sofka.gameddd.Juego.values.IdJuego;

public class CrearJuegoUseCase extends UseCase<RequestCommand<IniciarJuego>, ResponseEvents> {
    @Override
    public void executeUseCase(RequestCommand<IniciarJuego> iniciarJuegoRequestCommand) {
        var command  = iniciarJuegoRequestCommand.getCommand();
        var juego = new Juego(new IdJuego(command.getIdJuego().value()),command.getPista(), command.getMeta(), command.getPodium());
        juego.asignarPista(command.getPista());
        emit().onSuccess(new ResponseEvents(juego.getUncommittedChanges()));
    }
}
