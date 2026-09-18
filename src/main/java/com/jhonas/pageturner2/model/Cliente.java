package com.jhonas.pageturner2.model;

public class Cliente {
    private String nombre ;
    private String  dni ;
    private String correo ;


    //Constructores
    public Cliente () {

    }
    public  Cliente (String nombre, String dni, String correo) {
        this.nombre = nombre ;
        this.dni = dni ;
        this.correo = correo ;
    }

    //Getter Setter


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}

