package com.jhonas.pageturner2.repository;

import com.jhonas.pageturner2.model.Venta;

import java.util.List;



public interface VentaRepository {
    void guardar (Venta venta);
    List <Venta> listarTodos();

}
