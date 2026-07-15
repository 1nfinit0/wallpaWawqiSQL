package com.wallpawawqi.Class;

public class Categoria {

  private Long idCategoria;
  private String nombre;
  private String descripcion;

  public Categoria() {
  }

  public Categoria(Long idCategoria, String nombre, String descripcion) {
    this.idCategoria = idCategoria;
    this.nombre = nombre;
    this.descripcion = descripcion;
  }

  public Long getIdCategoria() {
    return idCategoria;
  }

  public void setIdCategoria(Long idCategoria) {
    this.idCategoria = idCategoria;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public void agregarCategoria() {
    // TODO: implementar persistencia de la categoria
  }

  public void actualizarCategoria() {
    // TODO: implementar actualizacion de la categoria
  }
}
