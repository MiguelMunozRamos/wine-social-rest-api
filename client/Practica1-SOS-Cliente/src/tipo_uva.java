

public class tipo_uva {
	private String nombre;
	private int ID;
	
	public tipo_uva(String nombre, int ID) {
		this.nombre = nombre;
		this.ID = ID;
	}
	public tipo_uva() {}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	
}
