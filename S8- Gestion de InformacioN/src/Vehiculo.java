public class Vehiculo {
    private String placa;
    private String tipo;
    private String propietario;
    private int edadPropietario;

    public Vehiculo(String placa, String tipo, String propietario, int edadPropietario) {
        this.placa = placa;
        this.tipo = tipo;
        this.propietario = propietario;
        this.edadPropietario = edadPropietario;
    }

    public String getPlaca() { return placa; }
    public String getTipo() { return tipo; }
    public String getPropietario() { return propietario; }
    public int getEdadPropietario() { return edadPropietario; }

    public boolean esAdultoMayor() {
        return edadPropietario >= 60;
    }
}