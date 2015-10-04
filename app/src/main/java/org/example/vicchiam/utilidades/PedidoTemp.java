package org.example.vicchiam.utilidades;

import org.example.vicchiam.bbdd.Pedido;

/**
 * Created by vicch on 27/09/2015.
 */
public class PedidoTemp {

    public static Pedido pedido;

    public static long idTemp=0;

    public static long getIDTemp(){
        return --idTemp;
    }

}
