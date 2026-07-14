package com.wallpawawqi.Class;

public class EmpleadoSesion {
    private long id;
    private String nombre;
    private String apellido;
    private String cargo;

    public EmpleadoSesion(long id, String nombre, String apellido, String cargo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cargo = cargo;
    }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getCargo() { return cargo; }
}