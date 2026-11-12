package br.com.socialconnect.api.exception;

import java.time.LocalDate;

/**
 * Período invertido na listagem de doações (dataInicio depois de dataFim):
 * o GlobalExceptionHandler responde 400.
 * <p>
 * Guarda as datas (e não o texto pronto) para a mensagem vir do
 * messages.properties.
 */
public class PeriodoInvalidoException extends RuntimeException {

    private final LocalDate dataInicio;
    private final LocalDate dataFim;

    public PeriodoInvalidoException(LocalDate dataInicio, LocalDate dataFim) {
        super("Período inválido: dataInicio " + dataInicio + " depois de dataFim " + dataFim); // texto para o log
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }
}
