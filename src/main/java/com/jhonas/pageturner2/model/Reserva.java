package com.jhonas.pageturner2.model;

import java.time.LocalDate;




public class Reserva {
    private LocalDate fecha;
    private  Cliente cliente;
    private Libro libro;

    //Consturctores

    public Reserva(){

    }

    public Reserva(LocalDate fecha, Cliente cliente, Libro libro) {
        this.fecha = fecha;
        this.cliente = cliente;
        this.libro = libro;
    }

    //Getter y Setter

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }
}
