package practica1;

public class vino_filtro {
	
	private String nombre;
	private String bodega;
	private String pais;
	private int añada;
	private String tipo_vino;
	private String fecha_adicion;
	
	public vino_filtro() {}

	public vino_filtro(String nombre, String bodega, String pais, int añada, String tipo_vino, String fecha_adicion) {
		this.nombre = nombre;
		this.bodega = bodega;
		this.pais = pais;
		this.añada = añada;
		this.tipo_vino = tipo_vino;
		this.fecha_adicion = fecha_adicion;
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

	public String getFecha_adicion() {
		return fecha_adicion;
	}

	public void setFecha_adicion(String fecha_adicion) {
		this.fecha_adicion = fecha_adicion;
	}
	
	
	
	
}
