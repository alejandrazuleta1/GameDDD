# GameDDD
Implementación de un juego de carreras usando el enfoque de diseño guiado por el dominio DDD.

Con base en el siguiente modelo:

![RamdomGameCar-Alejandra2 (1)](https://user-images.githubusercontent.com/58190520/98257579-db78c500-1f4d-11eb-88c6-031918a45c3c.png)

package co.com.sofka.horasextras.usecase;

import co.com.sofka.horasextras.Registro;
import co.com.sofka.horasextras.usecase.model.JornadaModel;
import co.com.sofka.horasextras.usecase.model.RegistroModel;
import co.com.sofka.horasextras.usecase.repository.RegistroRepository;
import co.com.sofka.horasextras.usecase.service.RegistrosPorLiderService;
import co.com.sofka.horasextras.value.HorarioTrabajado;
import com.google.gson.Gson;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static co.com.sofka.sofkianos.commons.DateUtility.getMonth;
import static co.com.sofka.sofkianos.commons.DateUtility.isHoliday;

public class RegistrosPorLiderUseCase implements RegistrosPorLiderService {

    private final RegistroRepository repository;

    public RegistrosPorLiderUseCase(RegistroRepository repository) {
        this.repository = repository;
    }

    /**
     * [Obtener todos los registros que un lider ha ingresado en una sesión para clasificar los horarios trabajados
     * añadidos.
     *
     *   El método executor efetúa toda la clasificacion de los horarios ingresados por un lider según su ID.
     *   En primer lugar consulta los registros creados por el lider según la fecha de corte del mes en el que se ejecute.
     *   Luego, clasifica los horarios trabajados y por último organiza la información en un DTO tipo RegistroModel para
     *   cada registro.
     * ]
     *
     * @param liderId ID del lider que está ingresando los registros para clasificar.
     * @return Flux<RegistroModel>
     *
     * @author
     * Junior Sierra Gómez - junior.sierra@sofka.com.co
     * Alejandra Zuleta González - alejandra.zuleta@sofka.com.co
     * Cristian Medina Gómez - cristian.medina@sofka.com.co
     */
    @Override
    public Flux<RegistroModel> executor(String liderId) {
        Date fechaActual = new Date();
        int mesActual = getMonth(fechaActual);
        mesActual += 1;

        return repository.getFechaCorte(mesActual)
                .flatMapMany(fecha -> repository.getRegistrosPorLider(liderId,fecha))
                .flatMap(clasificarHorariosTrabajados())
                .concatMap(getRegistroModelFunction());
    }

    /**
     * [Orquestar la clasificación, es decir, el paso por todos los filtros de tipos de horas extras y recargos, haciendo
     * la modificación en cada agregado Registro.
     * Según cada horarioTrabajdo se crea una instancia de la clase JornadaModel, que contiene la lista de horas entre
     * la hora inicio y hora fin del horarioTrabajado, que luego pasa por cada uno de los filtros.
     * ]
     *
     * @return Mono<Registro>
     *
     * @author
     * Junior Sierra Gómez - junior.sierra@sofka.com.co
     * Alejandra Zuleta González - alejandra.zuleta@sofka.com.co
     * Cristian Medina Gómez - cristian.medina@sofka.com.co
     */
    public Function<Registro, Mono<Registro>> clasificarHorariosTrabajados() {
        return registro ->{
            var horasSpliteadas = Flux.mergeSequential(registro.getHorariosTrabajados().stream()
                    .map(separarHorasHorarioTrabajo(registro)).collect(Collectors.toList()));

            var horasOrdinarias = horasSpliteadas.map(obtenerHorasOrdinarias());

            var jornadaModelConRecargos = horasOrdinarias.map(filtrarRecargoNocturno())
                    .map(filtrarRDominicalDiurnoConCompensatorio())
                    .map(filtrarRDominicalNocturnoConCompensatorio())
                    .map(filtrarRDominicalFestivoNocturnoSinCompensatorio())
                    .map(filtrarRDominicalFestivoDiurnoSinCompensatorio());

            var registroConRecargos = jornadaModelConRecargos
                    .map(JornadaModel::getRegistro)
                    .last();

            var horasExtras = horasSpliteadas.map(obtenerHorasExtras());

            var jornadaModelConRecargosYExtras = obtenerJornadaModelConRecargos().apply(horasExtras,registroConRecargos)
                    .map(filtrarHEDiurnas())
                    .map(filtrarHENocturnas())
                    .map(filtrarHEDiurnasDF())
                    .map(filtrarHENocturnasDF())
                    .map(removerHoraAlimentacion());

            return jornadaModelConRecargosYExtras
                    .map(JornadaModel::getRegistro)
                    .last();
        };
    }

    /**
     * [Según el horarioTrabajdo en que se aplica la función se crea una instancia de la clase JornadaModel, que contiene
     * la lista de horas entre la hora inicio y hora fin del horarioTrabajado.
     * Dependiendo de la hora de inicio, se incrementa de a 60 minutos para generar el array. Si entre la fecha de inicio
     * y la fecha fin hay horas y minutos (<60) entonces se adiciona un elemento final al array que contiene la hora final, ejemplo:
     * Entre las 2 y las 5:30 se generan: 2:00, 3:00, 4:00, 5:00, 5:30.
     * ]
     *
     * @return Mono<JornadaModel>
     *
     * @author
     * Junior Sierra Gómez - junior.sierra@sofka.com.co
     */
    protected Function<HorarioTrabajado, Mono<JornadaModel>> separarHorasHorarioTrabajo(Registro registro){
        return horarioTrabajado -> {
            if (horarioTrabajado.value().estaClasificado().equals(false)) {
                Calendar calendar = Calendar.getInstance();
                Date fechaInicio = horarioTrabajado.value().fechaHoraInicio();
                Date fechaFin = horarioTrabajado.value().fechaHoraFin();
                List<Date> splitedHoras = new ArrayList<>();
                Date horas = fechaInicio;
                while (!horas.equals(fechaFin)) {
                    if (horas.after(fechaFin)) {
                        horas = fechaFin;
                        splitedHoras.add(horas);
                    } else {
                        splitedHoras.add(horas);
                        calendar.setTime(horas);
                        calendar.add(Calendar.HOUR, 1);
                        horas = calendar.getTime();
                    }
                }
                return repository.getSofkianoPorId(registro.getEmpleadoId())
                        .map(sofkiano -> {
                            var jornadaModel = new JornadaModel(
                                    registro,
                                    splitedHoras,
                                    sofkiano.isHiBot(),
                                    horarioTrabajado.value().esCompensado());
                            jornadaModel.setCantidadHorasTrabajadas(splitedHoras.size());
                            jornadaModel.setFechaHoraInicioJornada(jornadaModel.getListaHoras().get(0));
                            return jornadaModel;
                        });
            }
            return Mono.just(new JornadaModel(
                    registro,
                    new ArrayList<>(),
                    false,
                    false)).map(jornadaModel -> {
                jornadaModel.setCantidadHorasTrabajadas(0);
                jornadaModel.setFechaHoraInicioJornada(new Date());
                return jornadaModel;
            });
        };
    }

    /**
     * [En JornadModel se tiene la lista de horas completa por horarioTrabajado de allí según la lógica de negocio se
     * filtran y se devuelve en la lista de horas de jornadaModel solo la lista de horas filtrada que corresponden
     * a las horas ordinarias (correspondientes a la jornada laboral) que son susceptibles a tener recargos.
     * ]
     *
     * @return JornadaModel
     *
     * @author
     * Junior Sierra Gómez - junior.sierra@sofka.com.co
     * Alejandra Zuleta González - alejandra.zuleta@sofka.com.co
     * Cristian Medina Gómez - cristian.medina@sofka.com.co
     */
    protected Function<JornadaModel, JornadaModel> obtenerHorasOrdinarias() {
        return jornadaModel->{
            var horas = jornadaModel.getListaHoras();
            List<Date> listHorasOrdinarias = new ArrayList<>();
            int cantidadhorasOrdinarias=0;

            var calendar = Calendar.getInstance();
            if(!horas.isEmpty()) {
                calendar.setTime(horas.get(0));
                boolean isSunday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
                boolean isSaturday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
                boolean isHoliday = false;
                try {
                    isHoliday = verifyIsHoliday(horas.get(0));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                var ultimaHora = Calendar.getInstance();
                ultimaHora.setTime(horas.get(horas.size()-1));

                if (isSunday || isHoliday) {
                    cantidadhorasOrdinarias = 8;
                } else if (jornadaModel.getEsHibot().equals(true)) {
                    if (isSaturday) {
                        cantidadhorasOrdinarias = 8;
                    } else {
                        cantidadhorasOrdinarias = 9;
                    }
                } else if (!isSaturday) {
                    cantidadhorasOrdinarias = 10;
                }

                if (horas.size() >= cantidadhorasOrdinarias) {
                        for (int i = 0; i < cantidadhorasOrdinarias; i++){
                            listHorasOrdinarias.add(horas.get(i));
                        }
                        if(horas.size() > cantidadhorasOrdinarias && cantidadhorasOrdinarias > 0) {
                            var auxhora1 = horas.get(cantidadhorasOrdinarias).getTime();
                            var auxhora2 = horas.get(cantidadhorasOrdinarias - 1).getTime();
                            if (auxhora1 - auxhora2 < 3600000) {
                                listHorasOrdinarias.add(horas.get(horas.size()-1));
                            }
                        }
                } else {
                    listHorasOrdinarias.addAll(horas);
                }
            }

            var jornada =  new JornadaModel(
                    jornadaModel.getRegistro(),
                    listHorasOrdinarias,
                    jornadaModel.getEsHibot(),
                    jornadaModel.getEsCompensado());
            jornada.setCantidadHorasTrabajadas(jornadaModel.getCantidadHorasTrabajadas());
            jornada.setFechaHoraInicioJornada(jornadaModel.getFechaHoraInicioJornada());
            return jornada;
        };
    }

    /**
     * [En JornadModel se tiene la lista de horas completa por horarioTrabajado de allí según la lógica de negocio se
     * filtran y se devuelve en la lista de horas de jornadaModel solo la lista de horas filtrada que corresponden
     * a las horas extras.
     * ]
     *
     * @return JornadaModel
     *
     * @author
     * Junior Sierra Gómez - junior.sierra@sofka.com.co
     * Alejandra Zuleta González - alejandra.zuleta@sofka.com.co
     * Cristian Medina Gómez - cristian.medina@sofka.com.co
     */
    protected Function<JornadaModel, JornadaModel> obtenerHorasExtras() {
        return jornadaModel->{
            var horas = jornadaModel.getListaHoras();
            List<Date> listHorasExtras = new ArrayList<>();

            int cantidadhorasOrdinarias = 0;

            var primeraHora = Calendar.getInstance();

            if(!horas.isEmpty()) {
                primeraHora.setTime(horas.get(0));
                boolean isSunday = primeraHora.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
                boolean isSaturday = primeraHora.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
                boolean isHoliday = false;
                try {
                    isHoliday = verifyIsHoliday(horas.get(0));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                var ultimaHora = Calendar.getInstance();
                ultimaHora.setTime(horas.get(horas.size()-1));

                if (isSunday || isHoliday) {
                    cantidadhorasOrdinarias = 8;
                } else if (jornadaModel.getEsHibot().equals(true)) {
                    if (isSaturday) {
                        cantidadhorasOrdinarias = 8;
                    } else {
                        cantidadhorasOrdinarias = 9;
                    }
                } else if (!isSaturday) {
                    cantidadhorasOrdinarias = 10;
                }

                if (horas.size() >= cantidadhorasOrdinarias) {
                        if(cantidadhorasOrdinarias > 1 && horas.size() > cantidadhorasOrdinarias) {
                            var auxhora1 = horas.get(cantidadhorasOrdinarias).getTime();
                            var auxhora2 = horas.get(cantidadhorasOrdinarias - 1).getTime();
                            if (auxhora1 - auxhora2 == 3600000) {
                                for (int i = cantidadhorasOrdinarias; i < horas.size(); i++) {
                                    listHorasExtras.add(horas.get(i));
                                }
                            }
                        } else {
                            for (int i = cantidadhorasOrdinarias; i < horas.size(); i++) {
                                listHorasExtras.add(horas.get(i));
                            }
                        }
                }
            }

            var jornada =  new JornadaModel(
                    jornadaModel.getRegistro(),
                    listHorasExtras,
                    jornadaModel.getEsHibot(),
                    jornadaModel.getEsCompensado());
            jornada.setCantidadHorasTrabajadas(jornadaModel.getCantidadHorasTrabajadas());
            jornada.setFechaHoraInicioJornada(jornadaModel.getFechaHoraInicioJornada());
            return jornada;
        };
    }

    /**
     * [Tras realizar todos los filtros de cada uno de los recargos, en el agregado registro que hay en jornadaModel
     * ya se tienen agregados las cantidades de horas con recargos, el método obtenerJornadaModelConRecargos utiliza la
     * lista de horas extras para crear un nuevo jornadaModel que contenta dicha lista y el registro con los recargos ya
     * aplicados.
     * ]
     *
     * @return Flux<JornadaModel>
     *
     * @author
     * Junior Sierra Gómez - junior.sierra@sofka.com.co
     * Alejandra Zuleta González - alejandra.zuleta@sofka.com.co
     * Cristian Medina Gómez - cristian.medina@sofka.com.co
     */
    private BiFunction<Flux<JornadaModel>,Mono<Registro>,Flux<JornadaModel>> obtenerJornadaModelConRecargos() {
        return (jornadaModelhorasExtras, monoRegistro) -> jornadaModelhorasExtras
                .flatMap(jornadaModel -> monoRegistro.map(registro -> {
                    jornadaModel.setRegistro(registro);
                    return jornadaModel;
                }));
    }

    /**
     * [Filtra de la lista de horas que trae el jornadaModel cuales pertenecen a esta categoría, las cuenta y elimina de
     * la lista. Además, añade en el agregado registro el total de horas pertenecientes a recargo dominical diurno con
     * Compensatorio.
     * ]
     *
     * @return JornadaModel
     *
     * @author
     * Alejandra Zuleta González - alejandra.zuleta@sofka.com.co
     *
     * @see <a href="https://git.sofka.com.co/oursofka/oursofka-back-horas-extras/-/blob/develop/README.md">Lógica de negocio</a>
     */
    public Function<JornadaModel, JornadaModel> filtrarRDominicalDiurnoConCompensatorio() {
        return jornadaModel -> {
            if (jornadaModel.getEsCompensado().equals(false)){
                return jornadaModel;
            }

            Supplier<Stream<Date>> streamSupplierListaHoras
                    = () -> jornadaModel.getListaHoras().stream();

            Supplier<Stream<Date>> streamSupplierlistaHorasFiltrada
                    = () -> streamSupplierListaHoras.get().filter(date-> {
                var calendar = Calendar.getInstance();
                calendar.setTime(date);
                var horaInicio = calendar.get(Calendar.HOUR_OF_DAY);
                boolean sundayBool = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
                return sundayBool && horaInicio >= 6 && horaInicio < 21;
            });

            var tupla = getHorasConFraccion(jornadaModel, streamSupplierListaHoras, streamSupplierlistaHorasFiltrada);
            float horas = tupla.getT1();
            var jornadaModelActulizada = tupla.getT2();

            jornadaModelActulizada.getRegistro().agregarRecargoFDConCompensatorio(horas);

            var listaHorasF = streamSupplierlistaHorasFiltrada.get().collect(Collectors.toList());
            var listaHorasActualizada = streamSupplierListaHoras.get().filter(date -> !listaHorasF.contains(date)).collect(Collectors.toList());

            jornadaModelActulizada.setListaHoras(listaHorasActualizada);

            return jornadaModelActulizada;
        };
    }

    /**
     * [Filtra de la lista de horas que trae el jornadaModel cuales pertenecen a esta categoría, las cuenta y las elimina
     * de la lista. Además, añade en el agregado registro el total de horas pertenecientes a recargo dominical nocturno con
     * Compensatorio.
     * ]
     *
     * @return JornadaModel
     *
     * @author
     * Alejandra Zuleta González - alejandra.zuleta@sofka.com.co
     *
     * @see <a href="https://git.sofka.com.co/oursofka/oursofka-back-horas-extras/-/blob/develop/README.md">Lógica de negocio</a>
     */
    protected Function<JornadaModel, JornadaModel> filtrarRDominicalNocturnoConCompensatorio() {
        return jornadaModel -> {
            if (jornadaModel.getEsCompensado().equals(false)){
                return jornadaModel;
            }

            Supplier<Stream<Date>> streamSupplierListaHoras
                    = () -> jornadaModel.getListaHoras().stream();

            Supplier<Stream<Date>> streamSupplierlistaHorasFiltrada
                    = () -> streamSupplierListaHoras.get().filter(date -> {

                var calendar = Calendar.getInstance();
                calendar.setTime(date);
                var horaInicio = calendar.get(Calendar.HOUR_OF_DAY);
                boolean isSunday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
                return ((isSunday &&(horaInicio >= 21))||(isSunday && (horaInicio < 6)));
            });

            var tupla = getHorasConFraccion(jornadaModel, streamSupplierListaHoras, streamSupplierlistaHorasFiltrada);
            float horas = tupla.getT1();
            var jornadaModelActulizada = tupla.getT2();

            jornadaModelActulizada.getRegistro().agregarRecargoDNocturnoConCompensatorio(horas);

            var listaHorasFiltrada = streamSupplierlistaHorasFiltrada.get().collect(Collectors.toList());
            var listaHorasActualizada = streamSupplierListaHoras.get()
                    .filter(date -> !listaHorasFiltrada.contains(date)).collect(Collectors.toList());

            jornadaModelActulizada.setListaHoras(listaHorasActualizada);
            return jornadaModelActulizada;
        };
    }

    /**
     * [Filtra de la lista de horas que trae el jornadaModel cuales pertenecen a esta categoría, las cuenta y elimina de
     * la lista. Además, añade en el agregado registro el total de horas pertenecientes a recargo dominical  o festivo
     * nocturno sin ompensatorio.
     * ]
     *
     * @return Mono<Registro>,Flux<JornadaModel>
     *
     * @author
     * Junior Sierra Gómez - junior.sierra@sofka.com.co
     *
     * @see <a href="https://git.sofka.com.co/oursofka/oursofka-back-horas-extras/-/blob/develop/README.md">Lógica de negocio</a>
     */
    protected Function<JornadaModel, JornadaModel> filtrarRDominicalFestivoNocturnoSinCompensatorio() {
        return jornadaModel -> {
            Supplier<Stream<Date>> streamSupplierListaHoras
                    = () -> jornadaModel.getListaHoras().stream();

            Supplier<Stream<Date>> streamSupplierlistaHorasFiltrada
                    = () -> streamSupplierListaHoras.get().filter(date -> {
                var calendar = Calendar.getInstance();
                calendar.setTime(date);
                var horaInicio = calendar.get(Calendar.HOUR_OF_DAY);
                boolean isSunday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
                boolean isHoliday = false;
                try {
                    isHoliday = verifyIsHoliday(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return (((isSunday && !jornadaModel.getEsCompensado())|| isHoliday) && (horaInicio >= 21)) || (((isSunday && !jornadaModel.getEsCompensado()) || isHoliday) && (horaInicio < 6));
            });

            var tupla = getHorasConFraccion(jornadaModel, streamSupplierListaHoras, streamSupplierlistaHorasFiltrada);
            float horas = tupla.getT1();
            var jornadaModelActulizada = tupla.getT2();

            jornadaModelActulizada.getRegistro().agregarRecargoFDNocturnoSinCompensatorio(horas);

            var listaHorasF = streamSupplierlistaHorasFiltrada.get().collect(Collectors.toList());
            var listaHorasActualizada = streamSupplierListaHoras.get().filter(date -> !listaHorasF.contains(date)).collect(Collectors.toList());

            jornadaModelActulizada.setListaHoras(listaHorasActualizada);
            return jornadaModelActulizada;
        };
    }

    /**
     * [Filtra de la lista de horas que trae el jornadaModel cuales pertenecen a esta categoría, las cuenta y elimina de
     * la lista. Además, añade en el agregado registro el total de horas pertenecientes a recargo dominical  o festivo
     * diurno sin compensatorio.
     * ]
     *
     * @return Mono<Registro>,Flux<JornadaModel>
     *
     * @author
     * Junior Sierra Gómez - junior.sierra@sofka.com.co
     *
     * @see <a href="https://git.sofka.com.co/oursofka/oursofka-back-horas-extras/-/blob/develop/README.md">Lógica de negocio</a>
     */
    protected Function<JornadaModel, JornadaModel> filtrarRDominicalFestivoDiurnoSinCompensatorio() {
        return jornadaModel -> {
            if (jornadaModel.getEsCompensado().equals(true)){
                return jornadaModel;
            }
            var calendar = Calendar.getInstance();
            Supplier<Stream<Date>> streamSupplierListaHoras
                    = () -> jornadaModel.getListaHoras().stream();

            Supplier<Stream<Date>> streamSupplierlistaHorasFiltrada
                    = () -> streamSupplierListaHoras.get().filter(date -> {
                calendar.setTime(date);
                var horaInicio = calendar.get(Calendar.HOUR_OF_DAY);
                boolean isSunday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
                boolean isHoliday = false;
                try {
                    isHoliday = verifyIsHoliday(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return (isSunday || isHoliday) && ((horaInicio >= 6) && (horaInicio < 21));
            });

            var tupla = getHorasConFraccion(jornadaModel, streamSupplierListaHoras, streamSupplierlistaHorasFiltrada);
            float horas = tupla.getT1();
            var jornadaModelActulizada = tupla.getT2();

            jornadaModelActulizada.getRegistro().agregarRecargoFDDiurnoSinCompensatorio(horas);

            var listaHorasF = streamSupplierlistaHorasFiltrada.get().collect(Collectors.toList());
            var listaHorasActualizada = streamSupplierListaHoras.get().filter(date -> !listaHorasF.contains(date)).collect(Collectors.toList());

            jornadaModelActulizada.setListaHoras(listaHorasActualizada);

            return jornadaModelActulizada;
        };
    }

    /**
     * [Filtra de la lista de horas que trae el jornadaModel cuales pertenecen a esta categoría, las cuenta y las elimina
     * de la lista. Además, añade en el agregado registro el total de horas pertenecientes a recargo nocturno.
     * ]
     *
     * @return Mono<Registro>,Flux<JornadaModel>
     *
     * @author
     * Cristian Medina Gómez - cristian.medina@sofka.com.co
     *
     * @see <a href="https://git.sofka.com.co/oursofka/oursofka-back-horas-extras/-/blob/develop/README.md">Lógica de negocio</a>
     */
    protected Function<JornadaModel, JornadaModel> filtrarRecargoNocturno() {
        return jornadaModel ->{
            var calendar = Calendar.getInstance();
            Supplier<Stream<Date>> streamSupplierListaHoras
                    = () -> jornadaModel.getListaHoras().stream();

            Supplier<Stream<Date>> streamSupplierlistaHorasFiltrada
                    = () -> streamSupplierListaHoras.get().filter(date -> {
                calendar.setTime(date);
                var horaInicio = calendar.get(Calendar.HOUR_OF_DAY);
                boolean isSunday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
                boolean isSaturday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;

                boolean isHoliday = false;
                try {
                    isHoliday = verifyIsHoliday(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                var casoNoHibot = !jornadaModel.getEsHibot() && !isSaturday && !isSunday && !isHoliday && ((horaInicio >= 21) || (horaInicio < 6));
                var casoHibot = jornadaModel.getEsHibot() && !isSunday && !isHoliday && ((horaInicio >= 21) || (horaInicio < 6));
                return casoHibot || casoNoHibot;
            });

            var tupla = getHorasConFraccion(jornadaModel, streamSupplierListaHoras, streamSupplierlistaHorasFiltrada);
            float horas = tupla.getT1();
            var jornadaModelActulizada = tupla.getT2();

            jornadaModelActulizada.getRegistro().agregarRecargoNocturno(horas);

            var listaHorasF = streamSupplierlistaHorasFiltrada.get().collect(Collectors.toList());
            var listaHorasActualizada = streamSupplierListaHoras.get().filter(date -> !listaHorasF.contains(date)).collect(Collectors.toList());

            jornadaModelActulizada.setListaHoras(listaHorasActualizada);

            return jornadaModelActulizada;
        };
    }

    /**
     * [Filtra de la lista de horas que trae el jornadaModel cuales pertenecen a esta categoría, las cuenta y las elimina
     * de la lista. Además, añade en el agregado registro el total de horas pertenecientes a horas extras diurnas.
     * ]
     *
     * @return JornadaModel
     *
     * @author
     * Alejandra Zuleta González - alejandra.zuleta@sofka.com.co
     *
     * @see <a href="https://git.sofka.com.co/oursofka/oursofka-back-horas-extras/-/blob/develop/README.md">Lógica de negocio</a>
     */
    protected Function<JornadaModel, JornadaModel> filtrarHEDiurnas() {
        return jornadaModel -> {
            Supplier<Stream<Date>> streamSupplierListaHoras
                    = () -> jornadaModel.getListaHoras().stream();

            Supplier<Stream<Date>> streamSupplierlistaHorasFiltrada
                    = () -> streamSupplierListaHoras.get().filter(date -> {
                var calendar = Calendar.getInstance();
                calendar.setTime(date);
                var horaInicio = calendar.get(Calendar.HOUR_OF_DAY);

                boolean isSunday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
                boolean isHoliday = false;
                try {
                    isHoliday = verifyIsHoliday(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return (horaInicio>=6 && horaInicio<21)  && (!isSunday && !isHoliday);
            });

            var tupla = getHorasConFraccion(jornadaModel, streamSupplierListaHoras, streamSupplierlistaHorasFiltrada);
            float horas = tupla.getT1();
            var jornadaModelActulizada = tupla.getT2();

            jornadaModelActulizada.getRegistro().agregarExtraDiurna(horas);

            var listaHorasF = streamSupplierlistaHorasFiltrada.get().collect(Collectors.toList());
            var listaHorasActualizada = streamSupplierListaHoras.get().filter(date -> !listaHorasF.contains(date)).collect(Collectors.toList());

            jornadaModelActulizada.setListaHoras(listaHorasActualizada);
            return jornadaModelActulizada;
        };
    }

    /**
     * [Filtra de la lista de horas que trae el jornadaModel cuales pertenecen a esta categoría, las cuenta y las elimina
     * de la lista. Además, añade en el agregado registro el total de horas pertenecientes a horas extras nocturnas.
     * ]
     *
     * @return JornadaModel
     *
     * @author
     * Alejandra Zuleta González - alejandra.zuleta@sofka.com.co
     *
     * @see <a href="https://git.sofka.com.co/oursofka/oursofka-back-horas-extras/-/blob/develop/README.md">Lógica de negocio</a>
     */
    protected Function<JornadaModel, JornadaModel> filtrarHENocturnas() {
        return jornadaModel -> {
            Supplier<Stream<Date>> streamSupplierListaHoras
                    = () -> jornadaModel.getListaHoras().stream();

            Supplier<Stream<Date>> streamSupplierlistaHorasFiltrada
                    = () -> streamSupplierListaHoras.get().filter(date -> {
                var calendar = Calendar.getInstance();
                calendar.setTime(date);
                var horaInicio = calendar.get(Calendar.HOUR_OF_DAY);
                boolean isSunday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
                boolean isHoliday = false;
                try {
                    isHoliday = verifyIsHoliday(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return (horaInicio>=21 || horaInicio < 6) && (!isSunday && !isHoliday);
            });

            var tupla = getHorasConFraccion(jornadaModel, streamSupplierListaHoras, streamSupplierlistaHorasFiltrada);
            float horas = tupla.getT1();
            var jornadaModelActulizada = tupla.getT2();

            jornadaModelActulizada.getRegistro().agregarExtraNocturna(horas);

            var listaHorasF = streamSupplierlistaHorasFiltrada.get().collect(Collectors.toList());
            var listaHorasActualizada = streamSupplierListaHoras.get().filter(date -> !listaHorasF.contains(date)).collect(Collectors.toList());

            jornadaModelActulizada.setListaHoras(listaHorasActualizada);
            return jornadaModelActulizada;
        };
    }

    /**
     * [Filtra de la lista de horas que trae el jornadaModel cuales pertenecen a esta categoría, las cuenta y las elimina
     * de la lista. Además, añade en el agregado registro el total de horas pertenecientes a horas extras diurnas en
     * domingo o festivo.
     * ]
     *
     * @return JornadaModel
     *
     * @author
     * Alejandra Zuleta González - alejandra.zuleta@sofka.com.co
     *
     * @see <a href="https://git.sofka.com.co/oursofka/oursofka-back-horas-extras/-/blob/develop/README.md">Lógica de negocio</a>
     */
    protected Function<JornadaModel, JornadaModel> filtrarHEDiurnasDF() {
        return jornadaModel -> {
            Supplier<Stream<Date>> streamSupplierListaHoras
                    = () -> jornadaModel.getListaHoras().stream();

            Supplier<Stream<Date>> streamSupplierlistaHorasFiltrada
                    = () -> streamSupplierListaHoras.get().filter(date -> {
                var calendar = Calendar.getInstance();
                calendar.setTime(date);
                var horaInicio = calendar.get(Calendar.HOUR_OF_DAY);
                boolean isSunday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
                boolean isHoliday = false;
                try {
                    isHoliday = verifyIsHoliday(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return (isHoliday || isSunday) && horaInicio>=6 && horaInicio<21;
            });

            var tupla = getHorasConFraccion(jornadaModel, streamSupplierListaHoras, streamSupplierlistaHorasFiltrada);
            float horas = tupla.getT1();
            var jornadaModelActulizada = tupla.getT2();

            jornadaModelActulizada.getRegistro().agregarExtraDiurnaFestivoDomingo(horas);

            var listaHorasF = streamSupplierlistaHorasFiltrada.get().collect(Collectors.toList());
            var listaHorasActualizada = streamSupplierListaHoras.get().filter(date -> !listaHorasF.contains(date)).collect(Collectors.toList());

            jornadaModelActulizada.setListaHoras(listaHorasActualizada);
            return jornadaModelActulizada;
        };
    }

    /**
     * [Filtra de la lista de horas que trae el jornadaModel cuales pertenecen a esta categoría, las cuenta y las
     * elimina de la lista. Además, añade en el agregado registro el total de horas pertenecientes a horas extras
     * nocturnas en domingo o festivo.
     * ]
     *
     * @return JornadaModel
     *
     * @author
     * Alejandra Zuleta González - alejandra.zuleta@sofka.com.co
     *
     * @see <a href="https://git.sofka.com.co/oursofka/oursofka-back-horas-extras/-/blob/develop/README.md">Lógica de negocio</a>
     */
    protected Function<JornadaModel, JornadaModel> filtrarHENocturnasDF() {
        return jornadaModel -> {
            Supplier<Stream<Date>> streamSupplierListaHoras
                    = () -> jornadaModel.getListaHoras().stream();

            Supplier<Stream<Date>> streamSupplierlistaHorasFiltrada
                    = () -> streamSupplierListaHoras.get().filter(date -> {
                var calendar = Calendar.getInstance();
                calendar.setTime(date);
                var horaInicio = calendar.get(Calendar.HOUR_OF_DAY);
                boolean isSunday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
                boolean isHoliday = false;
                try {
                    isHoliday = verifyIsHoliday(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return (isHoliday || isSunday) && (horaInicio>=21 || horaInicio<6);
            });

            var tupla = getHorasConFraccion(jornadaModel, streamSupplierListaHoras, streamSupplierlistaHorasFiltrada);
            float horas = tupla.getT1();
            var jornadaModelActulizada = tupla.getT2();

            jornadaModelActulizada.getRegistro().agregarExtraNocturnaFestivoDomingo(horas);

            var listaHorasF = streamSupplierlistaHorasFiltrada.get().collect(Collectors.toList());
            var listaHorasActualizada = streamSupplierListaHoras.get().filter(date -> !listaHorasF.contains(date)).collect(Collectors.toList());

            jornadaModelActulizada.setListaHoras(listaHorasActualizada);
            return jornadaModelActulizada;
        };
    }

    /**
     * [Se realiza el ajuste de la fraccion de hora guardada entre los dos ultimos elementos de la lista de horas en el
     * jornadaModel, si es el caso, cada método que filtra utiliza este método para tener el total de horas con la parte decimal.
     * ]
     *
     * @return JornadaModel
     *
     * @author
     * Alejandra Zuleta González - alejandra.zuleta@sofka.com.co
     *
     * @see <a href="https://git.sofka.com.co/oursofka/oursofka-back-horas-extras/-/blob/develop/README.md">Lógica de negocio</a>
     */
    private Tuple2<Float, JornadaModel> getHorasConFraccion(JornadaModel jornadaModel, Supplier<Stream<Date>> streamSupplierListaHoras, Supplier<Stream<Date>> streamSupplierlistaHorasFiltrada) {
        var totalHoras = (int) streamSupplierlistaHorasFiltrada.get().count();
        float horas=totalHoras;
        if(totalHoras>=2) {
            var horaUltima = streamSupplierlistaHorasFiltrada.get().collect(Collectors.toList()).get(totalHoras - 1);
            var horaPenultima = streamSupplierlistaHorasFiltrada.get().collect(Collectors.toList()).get(totalHoras - 2);
            horas = getHoras(totalHoras, horaUltima, horaPenultima);
        } else if (totalHoras == 1){
            if (streamSupplierListaHoras.get().count() > 1){
                var horaPenultima = streamSupplierlistaHorasFiltrada.get().collect(Collectors.toList()).get((int) streamSupplierlistaHorasFiltrada.get().count()-1);
                var calendar = Calendar.getInstance();
                calendar.setTime(horaPenultima);
                calendar.add(Calendar.MINUTE,-1 * calendar.get(Calendar.MINUTE));
                calendar.add(Calendar.HOUR,1);
                var horaUltima = calendar.getTime();
                horas = getHoras(totalHoras, horaUltima, horaPenultima);
                if(horas != 1) {
                    //obtener de nuevo las horas spliteadas a partir de las hora penultima hasta el final.
                    Date fechaFin = jornadaModel.getListaHoras().get(jornadaModel.getListaHoras().size()-1);
                    List<Date> splitedHoras = new ArrayList<>();
                    Date newHoras = horaUltima;
                    while (!newHoras.equals(fechaFin)) {
                        if (newHoras.after(fechaFin)) {
                            newHoras = fechaFin;
                            splitedHoras.add(newHoras);
                        } else {
                            splitedHoras.add(newHoras);
                            calendar.setTime(newHoras);
                            calendar.add(Calendar.HOUR, 1);
                            newHoras = calendar.getTime();
                        }
                    }
                    if(splitedHoras.size() > 0) {
                        jornadaModel.getListaHoras().clear();
                        jornadaModel.setListaHoras(splitedHoras);
                    }
                }
            } else {
                var horaUltima = streamSupplierlistaHorasFiltrada.get().collect(Collectors.toList()).get((int) streamSupplierlistaHorasFiltrada.get().count()-1);
                var calendar = Calendar.getInstance();
                calendar.setTime(horaUltima);
                calendar.add(Calendar.MINUTE, -1 * calendar.get(Calendar.MINUTE));
                var horaPenultima = calendar.getTime();

                if(!horaPenultima.equals(horaUltima)) {
                    horas = getHoras(totalHoras, horaUltima, horaPenultima);
                }
            }
        }
        return Tuples.of(horas,jornadaModel);
    }

    /**
     * [Analiza si entre la última hora y la penultima hora de la lista hay más de 15 minutos y se utiliza en el método
     * getHorasConFraccion. Se acordó utilizar un formado de redondeo tipico (>5 se aproxima al siguiente digito) con dos
     * decimales. Ejemplos:
     * 1.534 se aproxima a 1.53
     * 1.535 se aproxima a 1.54
     * ]
     *
     * @return float
     *
     * @author
     * Alejandra Zuleta González - alejandra.zuleta@sofka.com.co
     *
     * @see <a href="https://git.sofka.com.co/oursofka/oursofka-back-horas-extras/-/blob/develop/README.md">Lógica de negocio</a>
     */
    private float getHoras(int totalHoras, Date horaUltima, Date horaPenultima) {
        float transcurrido = horaUltima.getTime() - horaPenultima.getTime();
        transcurrido = transcurrido/3600000.0F;
        float cantidadhoras = totalHoras;
        if (totalHoras == 1) {
            totalHoras = 2;
        }
        if (transcurrido != 1){
            if (transcurrido>=0.25) {
                cantidadhoras = (totalHoras - 2) + transcurrido;
            }else{
                cantidadhoras = cantidadhoras - 2;
            }
        }
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        DecimalFormat formatFloat = new DecimalFormat("#.00",decimalFormatSymbols);
        return Float.parseFloat(formatFloat.format(cantidadhoras));
    }

    /**
     * [Según los recargos y horas extradas encontradas en la jornada se elimina la hora de alimentacion (excepto fines
     * de semana o festivos). Devuelve en la jornadaModel el registro actulizado.
     * ]
     *
     * @return JornadaModel
     *
     * @author
     * Junior Sierra Gómez - junior.sierra@sofka.com.co
     *
     * @see <a href="https://git.sofka.com.co/oursofka/oursofka-back-horas-extras/-/blob/develop/README.md">Lógica de negocio</a>
     */
    protected Function<JornadaModel, JornadaModel> removerHoraAlimentacion() {
        return jornadaModel -> {
            Map<String, Float> recargosyExtras =  getRecargosyExtras(jornadaModel);
            float cantidadHorasConRecargosyExtras = 0F;
            for (float cantidad: recargosyExtras.values()) {
                cantidadHorasConRecargosyExtras += cantidad;
            }
            var calendar = Calendar.getInstance();
            calendar.setTime(jornadaModel.getFechaHoraInicioJornada());
            boolean isSunday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
            boolean isSaturday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
            boolean isHoliday = false;
            try {
                isHoliday = verifyIsHoliday(jornadaModel.getFechaHoraInicioJornada());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if ((cantidadHorasConRecargosyExtras == jornadaModel.getCantidadHorasTrabajadas())
                    && !isSunday && !isSaturday && !isHoliday)
            {
                /*
                 Porcentajes de recargos:
                    RN      35%
                    RFDC    75%
                    RDNSC   110%
                    HED     125%
                    HEN     175%
                    RFDSC   175%
                    HEDDF   200%
                    HENDF   250%
                 */
                if(recargosyExtras.get("RN") > 0){
                    jornadaModel.getRegistro().quitarRecargoNocturno();
                }else if(recargosyExtras.get("RFDC") > 0){
                    jornadaModel.getRegistro().quitarRecargoDNocturnoConCompensatorio();
                }else if(recargosyExtras.get("RDNSC") > 0){
                    jornadaModel.getRegistro().quitarRecargoFDNocturnoSinCompensatorio();
                }else if(recargosyExtras.get("HED") > 0){
                    jornadaModel.getRegistro().quitarExtraDiurna();
                }else if(recargosyExtras.get("HEN") > 0){
                    jornadaModel.getRegistro().quitarExtraNocturna();
                }else if(recargosyExtras.get("RFDSC") > 0){
                    jornadaModel.getRegistro().quitarRecargoFDDiurnoSinCompensatorio();
                }else if(recargosyExtras.get("HEDDF") > 0){
                    jornadaModel.getRegistro().quitarExtraDiurnaFestivoDomingo();
                }else if(recargosyExtras.get("HENDF") > 0){
                    jornadaModel.getRegistro().quitarExtraNocturnaFestivoDomingo();
                }
            }

            return jornadaModel;
        };
    }

    /**
     * [Devuelve en un mapa tipo llave valor, la cantidad de recargos y horas extras almacenadas en el registro.
     * ]
     *
     * @param jornadaModel ingresa la jornada model con el agregado registro que contiene las horas extras y los recargos.
     * @return Map<String, Float>
     *
     * @author
     * Junior Sierra Gómez - junior.sierra@sofka.com.co
     *
     * @see <a href="https://git.sofka.com.co/oursofka/oursofka-back-horas-extras/-/blob/develop/README.md">Lógica de negocio</a>
     */
    private  Map<String, Float> getRecargosyExtras(JornadaModel jornadaModel) {
        Map<String, Float> recargosyExtras = new HashMap<>();
        recargosyExtras.put("RDNC", jornadaModel.getRegistro().getRecargo().value().dominicalNocturnoConCompesatorio());
        recargosyExtras.put("RFDC",jornadaModel.getRegistro().getRecargo().value().festivoDominicalConCompessatorio());
        recargosyExtras.put("RDNSC",jornadaModel.getRegistro().getRecargo().value().festivoDominicalNocturnoSinCompesatorio());
        recargosyExtras.put("RFDSC",jornadaModel.getRegistro().getRecargo().value().festivoDominicalSinCompesatorio());
        recargosyExtras.put("RN",jornadaModel.getRegistro().getRecargo().value().nocturno());
        recargosyExtras.put("HED", jornadaModel.getRegistro().getHoraExtra().value().diurna());
        recargosyExtras.put("HEN", jornadaModel.getRegistro().getHoraExtra().value().nocturna());
        recargosyExtras.put("HEDDF", jornadaModel.getRegistro().getHoraExtra().value().diurnaDominicalOFestivo());
        recargosyExtras.put("HENDF", jornadaModel.getRegistro().getHoraExtra().value().nocturnaDominicalOFestivo());
        return recargosyExtras;
    }

    /**
     * [Se verifica dada una fecha si el día fue festivo, usando la librería Commons de Oursofka.
     * ]
     *
     * @param date fecha a validar
     * @return boolean
     * @throws ParseException se separan mes año y dia de la fecha y se parsea por lo que puede generarse una excepcion.
     *
     * @author
     * Alejandra Zuleta González - alejandra.zuleta@sofka.com.co
     *
     * @see <a href="https://git.sofka.com.co/oursofka/oursofka-back-horas-extras/-/blob/develop/README.md">Lógica de negocio</a>
     */
    private boolean verifyIsHoliday(Date date) throws ParseException {
        var calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH) + 1;
        int year =  calendar.get(Calendar.YEAR);
        String strDate = dia + "/" + mes + "/" + year;
        Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(strDate);
        return isHoliday(date1);
    }

    /**
     * [Se mapea el registro con las clasificacaciones en un DTO que contenga la informacion de las clasificaciones
     * y datos personales del sofkiano.
     * ]
     *
     * @return Mono<RegistroModel>
     *
     * @author
     * Junior Sierra Gómez - junior.sierra@sofka.com.co
     * Alejandra Zuleta González - alejandra.zuleta@sofka.com.co
     * Cristian Medina Gómez - cristian.medina@sofka.com.co
     */
    private Function<Registro, Mono<RegistroModel>> getRegistroModelFunction() {
        return registro -> {
            registro.actualizarEstadoHorariosTrabajados();
            return repository.updateRegistro(registro)
                    .map(str->new Gson().fromJson(str,Registro.class))
                    .flatMap(registro1-> repository.getSofkianoPorId(registro1.getEmpleadoId())
                            .map(sofkiano -> new RegistroModel(sofkiano.identity().value(),
                                    sofkiano.nombre(),
                                    sofkiano.cedula(),
                                    registro1.getHoraExtra().value().nocturna(),
                                    registro1.getHoraExtra().value().diurna(),
                                    registro1.getHoraExtra().value().diurnaDominicalOFestivo(),
                                    registro1.getHoraExtra().value().nocturnaDominicalOFestivo(),
                                    registro1.getRecargo().value().nocturno(),
                                    registro1.getRecargo().value().dominicalNocturnoConCompesatorio(),
                                    registro1.getRecargo().value().festivoDominicalConCompessatorio(),
                                    registro1.getRecargo().value().festivoDominicalNocturnoSinCompesatorio(),
                                    registro1.getRecargo().value().festivoDominicalSinCompesatorio())));
        };
    }
}
