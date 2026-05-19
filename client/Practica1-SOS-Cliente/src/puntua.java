

public class puntua {

	public puntua() {}
	private int ID_vino;
	private int ID_usuario;
	private int calificacion;
	private String fecha_adicion;
	
	public puntua(int ID_vino, int ID_usuario, int calificacion, String fecha_adicion) {
		this.ID_vino = ID_vino;
		this.ID_usuario = ID_usuario;
		this.calificacion = calificacion;
		this.fecha_adicion = fecha_adicion;
	}

	public int getID_vino() {
		return ID_vino;
	}

	public void setID_vino(int iD_vino) {
		ID_vino = iD_vino;
	}

	public int getID_usuario() {
		return ID_usuario;
	}

	public void setID_usuario(int iD_usuario) {
		ID_usuario = iD_usuario;
	}

	public int getCalificacion() {
		return calificacion;
	}

	public void setCalificacion(int calificacion) {
		this.calificacion = calificacion;
	}

	public String getFecha_adicion() {
		return fecha_adicion;
	}

	public void setFecha_adicion(String fecha_adicion) {
		this.fecha_adicion = fecha_adicion;
	}
	
	
	
}
