package com.wallpawawqi.Class;

import java.time.LocalDate;

public class Inventario {

  private Long idInventario;
  private Long productoId;
  private Integer cantidadDisponible;
  private Integer stockMinimo;
  private LocalDate fechaActualizacion;
  private String estado;

  public Inventario() {
    this.fechaActualizacion = LocalDate.now();
  }

  public Inventario(Long idInventario, Long productoId, Integer cantidadDisponible, Integer stockMinimo) {
    this.idInventario = idInventario;
    this.productoId = productoId;
    this.cantidadDisponible = cantidadDisponible;
    this.stockMinimo = stockMinimo;
    this.fechaActualizacion = LocalDate.now();
    actualizarEstado();
  }

  public Long getIdInventario() {
    return idInventario;
  }

  public void setIdInventario(Long idInventario) {
    this.idInventario = idInventario;
  }

  public Long getProductoId() {
    return productoId;
  }

  public void setProductoId(Long productoId) {
    this.productoId = productoId;
  }

  public Integer getCantidadDisponible() {
    return cantidadDisponible;
  }

  public void setCantidadDisponible(Integer cantidadDisponible) {
    this.cantidadDisponible = cantidadDisponible;
    actualizarEstado();
  }

  public Integer getStockMinimo() {
    return stockMinimo;
  }

  public void setStockMinimo(Integer stockMinimo) {
    this.stockMinimo = stockMinimo;
    actualizarEstado();
  }

  public LocalDate getFechaActualizacion() {
    return fechaActualizacion;
  }

  public void setFechaActualizacion(LocalDate fechaActualizacion) {
    this.fechaActualizacion = fechaActualizacion;
  }

  public String getEstado() {
    return estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

  public void aumentarStock(Integer cantidad) {
    if (cantidad == null || cantidad <= 0) {
      return;
    }

    if (cantidadDisponible == null) {
      cantidadDisponible = 0;
    }

    cantidadDisponible += cantidad;
    fechaActualizacion = LocalDate.now();
    actualizarEstado();
  }

  public void disminuirStock(Integer cantidad) {
    if (cantidad == null || cantidad <= 0) {
      return;
    }

    if (cantidadDisponible == null) {
      cantidadDisponible = 0;
    }

    cantidadDisponible = Math.max(0, cantidadDisponible - cantidad);
    fechaActualizacion = LocalDate.now();
    actualizarEstado();
  }

  public Boolean verificarDisponibilidad() {
    return cantidadDisponible != null && cantidadDisponible > 0;
  }

  public void actualizarStock(Integer cantidad) {
    if (cantidad == null || cantidad < 0) {
      return;
    }

    cantidadDisponible = cantidad;
    fechaActualizacion = LocalDate.now();
    actualizarEstado();
  }

  public String obtenerEstadoStock() {
    actualizarEstado();
    return estado;
  }

  private void actualizarEstado() {
    if (cantidadDisponible == null || cantidadDisponible <= 0) {
      estado = "SIN_STOCK";
      return;
    }

    if (stockMinimo != null && cantidadDisponible <= stockMinimo) {
      estado = "STOCK_BAJO";
      return;
    }

    estado = "DISPONIBLE";
  }
}
