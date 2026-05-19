import java.net.URI;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;


public class Cliente {
	
	//Creamos el cliente
	static ClientConfig config = new ClientConfig();
	static Client client = ClientBuilder.newClient(config);
	static WebTarget target = client.target(getBaseURI());

	public static void main(String[] args) {
		usuario usuario = new usuario (11,"Francisco","usuario13@gmail.com","2002-12-25");
		
		Response valida = postUsuario(usuario);
		System.out.println("POST de usuario - Código de respuesta: " + valida.getStatus());
        System.out.println("Respuesta del servidor: " + valida.readEntity(String.class));
		
		Response getUsuario = getUsuario(usuario.getID());
		System.out.println("GET de usuario - Código de respuesta: " + getUsuario.getStatus());
        System.out.println("Respuesta del servidor: " + getUsuario.readEntity(String.class));
		
		Response putUsuario = putUsuario(usuario);
		System.out.println("PUT de usuario - Código de respuesta: " + putUsuario.getStatus());
        System.out.println("Respuesta del servidor: " + putUsuario.readEntity(String.class));
		
		Response deleteUsuario = deleteUsuario(usuario.getID());
		System.out.println("DELETE de usuario - Código de respuesta: " + deleteUsuario.getStatus());
        System.out.println("Respuesta del servidor: " + deleteUsuario.readEntity(String.class));
		
		Response getUsuario_patron = getUsuario_patron("m");
		System.out.println("GET de usuario con patrón - Código de respuesta: " + getUsuario_patron.getStatus());
        System.out.println("Respuesta del servidor: " + getUsuario_patron.readEntity(String.class));
		
		
		puntua puntua = new puntua(1,4,8,"2020-01-01");
		
		Response postVino = postVino(puntua);
		System.out.println("POST de vino - Código de respuesta: " + postVino.getStatus());
        System.out.println("Respuesta del servidor: " + postVino.readEntity(String.class));
        
		Response putVino = putVino(puntua);
		System.out.println("PUT de vino - Código de respuesta: " + putVino.getStatus());
        System.out.println("Respuesta del servidor: " + putVino.readEntity(String.class));
		
		Response deleteVino = deleteVino(puntua.getID_vino(),puntua.getID_usuario());
		System.out.println("DELETE de vino - Código de respuesta: " + deleteVino.getStatus());
        System.out.println("Respuesta del servidor: " + deleteVino.readEntity(String.class));
		
		Response getVinos = getVinos(2,"2023-10-07",10);
		System.out.println("GET de vinos - Código de respuesta: " + getVinos.getStatus());
        System.out.println("Respuesta del servidor: " + getVinos.readEntity(String.class)); 
		
		Response getVinos_filtro = getVinos_filtro(2,"Rosé di Toscana","Antinori", 2020, "Italia", "Rosado", 10);
		System.out.println("GET de vinos con filtro - Código de respuesta: " + getVinos_filtro.getStatus());
        System.out.println("Respuesta del servidor: " + getVinos_filtro.readEntity(String.class)); 
		
		Response postSeguidor = postSeguidor(3,4);
		System.out.println("POST de un seguidor - Código de respuesta: " + postSeguidor.getStatus());
        System.out.println("Respuesta del servidor: " + postSeguidor.readEntity(String.class));
		
		Response deleteSeguidor = deleteSeguidor(3,4);
		System.out.println("DELETE de seguidor - Código de respuesta: " + deleteSeguidor.getStatus());
        System.out.println("Respuesta del servidor: " + deleteSeguidor.readEntity(String.class));
		
		Response getSeguidores = getSeguidores(1,"m",2);
		System.out.println("GET de seguidores - Código de respuesta: " + getSeguidores.getStatus());
        System.out.println("Respuesta del servidor: " + getSeguidores.readEntity(String.class));
		
		Response getVinos_seguidor = getVinos_seguidor(1,2,"Rosé di Toscana","Antinori", 2020, "Italia", "Rosado", "2023-10-07", 10);
		System.out.println("GET de vinos seguidor - Código de respuesta: " + getVinos_seguidor.getStatus());
        System.out.println("Respuesta del servidor: " + getVinos_seguidor.readEntity(String.class));
		
		Response getRecomendacion = getRecomendacion(1);
		System.out.println("GET recomendacion - Código de respuesta: " + getRecomendacion.getStatus());
        System.out.println("Respuesta del servidor: " + getRecomendacion.readEntity(String.class));
		
		
		
		
		
		
	}
	
	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:8080/Practica1-SOS/api").build();
	}
	
	private static Response postUsuario(usuario usuario) {
	    // Crear un objeto JSON con los datos del usuario
	    JsonObject jsonUsuario = Json.createObjectBuilder()
	            .add("ID", usuario.getID())
	            .add("nombre", usuario.getNombre())
	            .add("correo_electronico", usuario.getCorreo_electronico())
	            .add("fecha_nacimiento", usuario.getFecha_nacimiento())
	            .build();

	    // Realizar la solicitud POST con el objeto JSON en el cuerpo
	    Response response = target.path("usuario")
	            .request()
	            .accept(MediaType.APPLICATION_JSON)
	            .post(Entity.json(jsonUsuario.toString()));

	    return response;
	}
	
	private static Response getUsuario(int id) {
		String ID_usuario = String.valueOf(id);
		Response response = target.path("usuario").path(ID_usuario)
			    .request()
			    .accept(MediaType.APPLICATION_JSON)
			    .get();
		return response;
	}
	
	private static Response putUsuario(usuario usuario) {
		
	    JsonObject jsonUsuario = Json.createObjectBuilder()
	            .add("nombre", usuario.getNombre())
	            .add("correo_electronico", usuario.getCorreo_electronico())
	            .add("fecha_nacimiento", usuario.getFecha_nacimiento())
	            .build();
	    
		String ID_usuario = String.valueOf(usuario.getID());
		Response response = target.path("usuario").path(ID_usuario)
			    .request()
			    .accept(MediaType.APPLICATION_JSON)
			    .put(Entity.json(jsonUsuario.toString()));
		return response;
	}
	
	private static Response deleteUsuario(int id) {
		String ID_usuario = String.valueOf(id);
		Response response = target.path("usuario").path(ID_usuario)
			    .request()
			    .accept(MediaType.APPLICATION_JSON)
			    .delete();
		return response;
	}
	
	private static Response getUsuario_patron(String patron) {
		Response response = target.path("usuario")
				.queryParam("patron", patron)
			    .request()
			    .accept(MediaType.APPLICATION_JSON)
			    .get();
		return response;
	}
	
	private static Response postVino(puntua puntua) {
	    JsonObject jsonPuntua = Json.createObjectBuilder()
	            .add("ID_vino", puntua.getID_vino())
	            .add("ID_usuario", puntua.getID_usuario())
	            .add("calificacion", puntua.getCalificacion())
	            .add("fecha_adicion", puntua.getFecha_adicion())
	            .build();
		String ID_usuario = String.valueOf(puntua.getID_usuario());
		Response response = target.path("usuario").path(ID_usuario).path("vino")
			    .request()
			    .accept(MediaType.APPLICATION_JSON)
			    .post(Entity.json(jsonPuntua.toString()));
		return response;
	}
	
	private static Response putVino(puntua puntua) {
	    JsonObject jsonPuntua = Json.createObjectBuilder()
	            .add("ID_vino", puntua.getID_vino())
	            .add("calificacion", puntua.getCalificacion())
	            .add("fecha_adicion", puntua.getFecha_adicion())
	            .build();
		String ID_usuario = String.valueOf(puntua.getID_usuario());
		String ID_vino = String.valueOf(puntua.getID_vino());
		Response response = target.path("usuario").path(ID_usuario).path("vino").path(ID_vino)
			    .request()
			    .accept(MediaType.APPLICATION_JSON)
			    .put(Entity.json(jsonPuntua.toString()));
		return response;
	}
	
	private static Response deleteVino(int id_vino, int id_usuario) {
		String ID_vino = String.valueOf(id_vino);
		String ID_usuario = String.valueOf(id_usuario);
		Response response = target.path("usuario").path(ID_usuario).path("vino").path(ID_vino)
			    .request()
			    .accept(MediaType.APPLICATION_JSON)
			    .delete();
		return response;
	}
	
	private static Response getVinos(int id,String fecha_adicion, int limite) {
		String ID_usuario = String.valueOf(id);
		Response response = target.path("usuario").path(ID_usuario).path("vino")
				.queryParam("fecha_adicion", fecha_adicion)
				.queryParam("Limite", limite)
			    .request()
			    .accept(MediaType.APPLICATION_JSON)
			    .get();
		return response;
	}
	
	private static Response getVinos_filtro(int id,String nombre,String bodega, int añada, String pais, String tipo_vino, int limite) {
		String ID_usuario = String.valueOf(id);
		Response response = target.path("usuario").path(ID_usuario).path("vino")
				.queryParam("Limite", limite)
				.queryParam("nombre", nombre)
				.queryParam("bodega", bodega)
				.queryParam("pais", pais)
				.queryParam("tipo_vino", tipo_vino)
				.queryParam("añada", añada)
			    .request()
			    .accept(MediaType.APPLICATION_JSON)
			    .get();
		return response;
	}
	
	private static Response postSeguidor(int ID, int ID_seguidor) {
	    JsonObject jsonSigue = Json.createObjectBuilder()
	            .add("ID_seguidor", ID_seguidor)
	            .add("ID_seguido", ID)
	            .build();
		String ID_usuario = String.valueOf(ID);
		Response response = target.path("usuario").path(ID_usuario).path("seguidor")
			    .request()
			    .accept(MediaType.APPLICATION_JSON)
			    .post(Entity.json(jsonSigue.toString()));
		return response;
	}
	
	private static Response deleteSeguidor(int id, int id_seguidor) {
		String ID_usuario = String.valueOf(id);
		String ID_seguidor = String.valueOf(id_seguidor);
		Response response = target.path("usuario").path(ID_usuario).path("seguidor").path(ID_seguidor)
			    .request()
			    .accept(MediaType.APPLICATION_JSON)
			    .delete();
		return response;
	}
	
	private static Response getSeguidores(int id,String patron, int limite) {
		String ID_usuario = String.valueOf(id);
		Response response = target.path("usuario").path(ID_usuario).path("seguidor")
				.queryParam("patron", patron)
				.queryParam("limite", limite)
			    .request()
			    .accept(MediaType.APPLICATION_JSON)
			    .get();
		return response;
	}
	
	private static Response getVinos_seguidor(int id, int id_seguidor,String nombre,String bodega, int añada, String pais, String tipo_vino,String fecha_adicion, int limite) {
		String ID_usuario = String.valueOf(id);
		String ID_seguidor = String.valueOf(id_seguidor);
		Response response = target.path("usuario").path(ID_usuario).path("seguidor").path(ID_seguidor).path("vinos")
				.queryParam("limite", limite)
				.queryParam("nombre", nombre)
				.queryParam("bodega", bodega)
				.queryParam("pais", pais)
				.queryParam("tipo_vino", tipo_vino)
				.queryParam("añada", añada)
			    .request()
			    .accept(MediaType.APPLICATION_JSON)
			    .get();
		return response;
	}
	
	private static Response getRecomendacion(int id) {
		String ID_usuario = String.valueOf(id);
		Response response = target.path("usuario").path(ID_usuario).path("recomendacion")
			    .request()
			    .accept(MediaType.APPLICATION_JSON)
			    .get();
		return response;
	}
	
}
