package practica1;



public class usuario {
	private int ID;
	private String correo_electronico;
	private String fecha_nacimiento;
	private String nombre;
	
    public usuario(int ID, String nombre, String correo_electronico, String fecha_nacimiento) {
        this.ID = ID;
        this.correo_electronico = correo_electronico;
        this.fecha_nacimiento = fecha_nacimiento;
        this.nombre = nombre;
    }
    
	public usuario(){
	}
	
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getCorreo_electronico() {
		return correo_electronico;
	}

	public void setCorreo_electronico(String correo_electronico) {
		this.correo_electronico = correo_electronico;
	}

	public String getFecha_nacimiento() {
		return fecha_nacimiento;
	}

	public void setFecha_nacimiento(String fecha_nacimiento) {
		this.fecha_nacimiento = fecha_nacimiento;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
