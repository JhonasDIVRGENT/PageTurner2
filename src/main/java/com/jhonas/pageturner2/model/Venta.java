package com.jhonas.pageturner2.model;

import java.time.LocalDate;



public class Venta {
    private  LocalDate fecha;
    private  int  cantidad;

    private  Cliente cliente;
    private  Libro libro;

    //constructores
    public Venta () {


    }
    public Venta(LocalDate fecha, int cantidad, Cliente cliente, Libro libro) {
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.cliente = cliente;
        this.libro = libro;
    }

    //getter y setter


    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
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

    //metodo para calcular
    public double  calcularTotal () {
        return libro.getPrecio() * cantidad;

    }
}
