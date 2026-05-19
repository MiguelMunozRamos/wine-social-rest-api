

import java.util.List;

public class vino {
	
	private int ID;
	private String nombre;
	private String bodega;
	private String pais;
	private int añada;
	private String tipo_vino;
	private List<tipo_uva> tipo_uva;
	
    public vino(int ID, String nombre, String bodega, String pais, int añada, String tipo_vino, List<tipo_uva> tipo_uva) {
        this.ID = ID;
        this.nombre = nombre;
        this.bodega = bodega;
        this.pais = pais;
        this.añada = añada;
        this.tipo_vino = tipo_vino;
        this.tipo_uva = tipo_uva;
    }

	
	public vino() {}

	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getBodega() {
		return bodega;
	}
	public void setBodega(String bodega) {
		this.bodega = bodega;
	}
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}
	public int getAñada() {
		return añada;
	}
	public void setAñada(int añada) {
		this.añada = añada;
	}
	public String getTipo_vino() {
		return tipo_vino;
	}
	public void setTipo_vino(String tipo_vino) {
		this.tipo_vino = tipo_vino;
	}
	public List<tipo_uva> getTipo_uva() {
		return tipo_uva;
	}
	public void setTipo_uva(List<tipo_uva> tipo_uva) {
		this.tipo_uva = tipo_uva;
	}
}
