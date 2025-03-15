package com.discussionforum.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServidorRMI {
    public static void main(String[] args) {
        try {
            ForoRemoto foro = new ForoRemotoImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("ForoRemoto", foro);
            System.out.println("Servidor RMI listo.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
