package modelo;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import processing.core.PApplet;

public class AutomataFinito {

    private ArrayList<Estado> listaEstados = new ArrayList<Estado>();
    private ArrayList<Conexion> listaConexiones = new ArrayList<Conexion>();
    private PApplet parent;
    private String[][] matrizDeCondiciones;
    private int[] estadosConCoicidencia;

    public AutomataFinito(PApplet p) {
        parent = p;
        matrizDeCondiciones = new String[50][50];
        estadosConCoicidencia = new int[50];
        inicializarMatriz(matrizDeCondiciones, 50, "-");
    }

    public void agregarEstado(Estado estado) {
        listaEstados.add(estado);
    }

    public void imprimirEstados() {
        for (int i = 0; i < listaEstados.size(); i++) {
            listaEstados.get(i).show();
        }
    }

    public void imprimirConexiones() {
        for (int i = 0; i < listaConexiones.size(); i++) {
            listaConexiones.get(i).show();
        }
    }

    public void imprimirMatriz() {
        for (int i = 0; i < listaEstados.size(); i++) {
            for (int j = 0; j < listaEstados.size(); j++) {
                System.out.print(matrizDeCondiciones[i][j] + " ");
            }
            System.out.println("");
        }
    }

    public int getEstadoClickeado() {
        int estadoClickeado = -1;
        for (int i = 0; i < getListaEstados().size(); i++) {
            if (distanciaEntrePuntos(getListaEstados().get(i).getCoordenadas().getX(), getListaEstados().get(i).getCoordenadas().getY(),
                    parent.mouseX, parent.mouseY) <= getListaEstados().get(i).getRadio()) {
                estadoClickeado = i;
                break;
            }
        }
        return estadoClickeado;
    }

    public void eliminarEstado(int estado) {
        getListaEstados().remove(estado);
    }

    public void eliminarConexiones(int estado) {
        /**/

        for (int i = 0; i < getListaConexiones().size(); i++) {
            PApplet.println("Estado: " + getListaEstados().get(estado) + "  |   Conexion destino: " + getListaConexiones().get(i).getDestino());
            if (getListaEstados().get(estado).equals(getListaConexiones().get(i).getDestino())) {
                getListaConexiones().remove(i);
                i--;
            }
        }
        for (int i = 0; i < getListaConexiones().size(); i++) {
            PApplet.println("Estado: " + getListaEstados().get(estado) + "  |   Conexion origen: " + getListaConexiones().get(i).getOrigen());
            if (getListaEstados().get(estado).equals(getListaConexiones().get(i).getOrigen())) {
                getListaConexiones().remove(i);
                i--;
            }
        }
        if (estado >= 0) {
            moverColumnasMatriz(getListaEstados().size(), getListaEstados().size(), estado);
            System.out.println("El estado " + getListaEstados().get(estado).getNombre() + " ha sido eliminado");
            moverFilasMatriz(getListaEstados().size(), getListaEstados().size(), estado);
            eliminarEstado(estado);
        }
    }

    public void moverFilasMatriz(int filasMatriz, int columnasMatriz, int posicion) {
        if (posicion >= 0 && posicion < filasMatriz) {

            for (int i = posicion; i < filasMatriz; i++) {
                for (int j = 0; j < columnasMatriz; j++) {
                    if (i == filasMatriz - 1) {
                        matrizDeCondiciones[i][j] = "-";
                    } else {
                        matrizDeCondiciones[i][j] = matrizDeCondiciones[i + 1][j];
                    }

                }
            }
        }
    }

    public void moverColumnasMatriz(int filasMatriz, int columnasMatriz, int posicion) {
        if (posicion >= 0 && posicion < columnasMatriz) {

            for (int i = 0; i < filasMatriz; i++) {
                for (int j = posicion; j < columnasMatriz; j++) {
                    if (j == columnasMatriz - 1) {
                        matrizDeCondiciones[i][j] = "-";
                    } else {
                        matrizDeCondiciones[i][j] = matrizDeCondiciones[i][j + 1];
                    }

                }
            }
        }
    }

    public int distanciaEntrePuntos(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
    }

    public void agregarConexion(int estado1, int estado2, String condicion) {
        // Si la conexion ya existe
        if (!matrizDeCondiciones[estado1][estado2].equals("-")) {

        } else {
            matrizDeCondiciones[estado1][estado2] = condicion;
            // Conexion normal
            if (estado1 != estado2) {
                PApplet.println("Conexion: Estado1: " + estado1 + " | Estado2: " + estado2 + " | " + "Condicion: " + matrizDeCondiciones[estado1][estado2]);
                Condicion condicionNueva = new Condicion(parent, new Punto(0, 0), condicion);
                listaConexiones.add(new ConexionNormal(parent, listaEstados.get(estado1), listaEstados.get(estado2), condicionNueva));
            } // Conexion bucle
            else {
                PApplet.println("Conexion bucle: Estado1: " + estado1 + " | Estado2: " + estado2 + " | " + "Condicion: " + matrizDeCondiciones[estado1][estado2]);
                Condicion condicionNueva = new Condicion(parent, new Punto(0, 0), condicion);
                listaConexiones.add(new ConexionBucle(parent, listaEstados.get(estado1), listaEstados.get(estado2), condicionNueva));
            }

        }
    }

    public void inicializarMatriz(String matriz[][], int tam, String x) {
        for (int fila = 0; fila < tam; fila++) {
            for (int columna = 0; columna < tam; columna++) {
                matriz[fila][columna] = x;
            }
        }
    }

    public void iniciarAutomata(String palabra) {
        if (this.getListaEstados().size() > 0) {
            /*Variable que controla las filas, las cuales representan el estado en el que se encuentra el automata*/
            int estado = encontrarEstadoInicial();

            /*Ciclo para comparar cada caracter*/
            int cont = 0;
            while (palabra.length() != 0 && cont < getListaEstados().size()) {
                if (getMatrizDeCondiciones()[estado][cont].length() > 2) {

                    String[] caracteres = getMatrizDeCondiciones()[estado][cont].split("[,]");
                    for (int i = 0; i < caracteres.length; i++) {
                        if (caracteres[i].equals(palabra.charAt(0) + "")) {
                            System.out.print(palabra.charAt(0) + " ");
                            palabra = palabra.substring(1, palabra.length());
                            estado = cont;
                            cont = 0;
                            break;
                        }
                    }

                    cont++;

                } else {
                    if (getMatrizDeCondiciones()[estado][cont].equals(palabra.charAt(0) + "")) {//comprueba el primer caracter
                        System.out.print(palabra.charAt(0) + " ");
                        palabra = palabra.substring(1, palabra.length()); //Desplaza la cadena quitando el primer caracter
                        estado = cont;
                        cont = 0;

                    } else {
                        cont++;
                    }

                }
                /*Pregunta si la palabra es vacia de ser asi se aceptara la palabra,
                 pero si el estado en el que se encuentra no es final no se aceptara.*/
                if (palabra.equals("")) {
                    if (getListaEstados().get(estado) instanceof EstadoFinal) {
                        JOptionPane.showMessageDialog(null, "Palabra aceptada");
                        System.out.println("Palabra aceptada");
                    } else {
                        System.out.println("Palabra no aceptada por no ser estado final");
                        JOptionPane.showMessageDialog(null, "Palabra no aceptada");
                    }
                    break;
                }

            }


            /*Si la palabra no es vacía no es aceptada*/
            if (!palabra.equals("")) {
                JOptionPane.showMessageDialog(null, "Palabra no aceptada");
                System.out.println("Palabra no aceptada por tener condiciones");
            }

        } else {
            JOptionPane.showMessageDialog(null, "No hay estados");
        }

    }

    public void llenarEstadosConCoicidencia(String palabra) {//funcion para llenar el array de estados, donde pasa por cada caracter que lee

        /*Variable que controla las filas, las cuales representan el estado en el que se encuentra el automata*/
        int estado = encontrarEstadoInicial();

        /*Ciclo para comparar cada caracter*/
        int cont = 0;
        int cont2 = 0;//contador para llenar el arrray

        getEstadosConCoicidencia()[cont2] = estado;//le asgina al primer miembro del arrya el estado inicial
        cont2++;
        while (palabra.length() != 0 && cont < getListaEstados().size()) {//el mismo algoritmo que el de arriba
            if (getMatrizDeCondiciones()[estado][cont].length() > 2) {

                String[] caracteres = getMatrizDeCondiciones()[estado][cont].split("[,]");
                for (int i = 0; i < caracteres.length; i++) {
                    if (caracteres[i].equals(palabra.charAt(0) + "")) {

                        palabra = palabra.substring(1, palabra.length());
                        estado = cont;
                        getEstadosConCoicidencia()[cont2] = estado;
                        cont2++;
                        cont = 0;
                        break;
                    }
                }

                cont++;

            } else {
                if (getMatrizDeCondiciones()[estado][cont].equals(palabra.charAt(0) + "")) {//comprueba el primer caracter

                    palabra = palabra.substring(1, palabra.length()); //Desplaza la cadena quitando el primer caracter
                    estado = cont;
                    getEstadosConCoicidencia()[cont2] = estado;
                    cont2++;
                    cont = 0;

                } else {
                    cont++;
                }

            }

        }
    }

    public int encontrarEstadoInicial() {//funcion para encontrar el estado inicial

        int estado = 0;

        /*Encontrar el estado inicial*/
        for (int i = 0; i < getListaEstados().size(); i++) {
            if (getListaEstados().get(i) instanceof EstadoInicial || getListaEstados().get(i) instanceof EstadoInicialFinal) {
                estado = i;
            }
        }

        return estado;
    }

    public void resetColor() {//vuelve a su color original a los estados dentro del array EstadoConCoicidencia
        for (int i = 0; i < getEstadosConCoicidencia().length; i++) {
            getListaEstados().get(getEstadosConCoicidencia()[i]).setColorBackground(parent.color(81, 237, 236));
        }
    }

    public int[] getEstadosConCoicidencia() {
        return estadosConCoicidencia;
    }

    public void setEstadosConCoicidencia(int[] estadosConCoicidencia) {
        this.estadosConCoicidencia = estadosConCoicidencia;
    }

    public ArrayList<Estado> getListaEstados() {
        return listaEstados;
    }

    public ArrayList<Conexion> getListaConexiones() {
        return listaConexiones;
    }

    public String[][] getMatrizDeCondiciones() {
        return matrizDeCondiciones;
    }

    public void setListaEstados(ArrayList<Estado> listaEstados) {
        this.listaEstados = listaEstados;
    }

    public void setListaConexiones(ArrayList<Conexion> listaConexiones) {
        this.listaConexiones = listaConexiones;
    }

    public void setMatrizDeCondiciones(String[][] matrizDeCondiciones) {
        this.matrizDeCondiciones = matrizDeCondiciones;
    }

}
