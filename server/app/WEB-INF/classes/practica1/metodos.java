package practica1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.naming.NamingContext;

//Path para los metodos
@Path("/usuario")
public class metodos {
	
	private UriInfo uriInfo;
	private DataSource ds;
	private Connection conn;
	
	//Constructor que inicia la conexion
    public metodos() {
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			NamingContext envCtx = (NamingContext) ctx.lookup("java:comp/env");
			ds = (DataSource) envCtx.lookup("jdbc/Practica1_SOS");
			conn = ds.getConnection();
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUsuario(@Context UriInfo uriInfo, usuario usuario) {
        try {
            //Consulta SQL que inserta el usuario en la tabla
            String sql = "INSERT INTO usuario (ID, nombre, correo_electronico, fecha_nacimiento) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, usuario.getID());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getCorreo_electronico());
            ps.setString(4, usuario.getFecha_nacimiento());
            int affectedRows = ps.executeUpdate();

            //Comprobamos si el usuario es mayor de edad
            if (affectedRows > 0 && esMayorDeEdad(usuario.getFecha_nacimiento())) {
                String location = uriInfo.getAbsolutePath() + "/" + usuario.getID();
                return Response.status(Response.Status.CREATED).entity("Usuario creado correctamente con la siguiente URI: " + location).header("Location", location).header("Content-Location", location).build();
            } else {
                return Response.status(Response.Status.CONFLICT).entity("El usuario que estás intentando crear es menor de edad").build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el usuario\n" + e.getStackTrace()).build();
        }
    }

  //Metodo que actualiza un usuario
    @PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{ID_usuario}")
	public Response updateUsuario(@Context UriInfo uriInfo, @PathParam("ID_usuario") String id, usuario usuarioActualizado) {
		try {
			usuario usuario;
			//Pasamos el ID a entero
			int int_id = Integer.parseInt(id);
			//Consulta SQL que selecciona el usuario a modificar
			String sql = "SELECT * FROM usuario where id=" + int_id + ";";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			//Si hay un usuario con el id buscado se le asigna a la variable usuario
			if (rs.next()) {
				usuario =  usuarioFromRS(rs);
			}
			//Si no se encuentra se devuelve 404
			else {
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
			}
			//Se actualizan los datos del usuario
			usuario.setNombre(usuarioActualizado.getNombre());
			usuario.setCorreo_electronico(usuarioActualizado.getCorreo_electronico());
			usuario.setFecha_nacimiento(usuarioActualizado.getFecha_nacimiento());
			//Comprobamos que es mayor de edad
			if(!esMayorDeEdad(usuario.getFecha_nacimiento())) {
				return Response.status(Response.Status.CONFLICT).entity("El usuario que estas intentando actualizar es menor de edad").build();
			}
			//Consulta SQL que actualiza los datos
			sql = "UPDATE usuario SET "
			        + "nombre='" + usuario.getNombre() 
			        + "', correo_electronico='" + usuario.getCorreo_electronico() 
			        + "', fecha_nacimiento='" + usuario.getFecha_nacimiento() 
			        + "' WHERE id=" + int_id + ";";
			ps = conn.prepareStatement(sql);
			int affectedRows = ps.executeUpdate();
			if(affectedRows > 0) {
				//Se devuelve la localizacion del usuario modificado
				String location = uriInfo.getBaseUri() + "usuario/" + usuario.getID();
				return Response.status(Response.Status.OK).entity(usuario).header("Content-Location", location).build();
			}
			else {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("La consulta SQL no se ha ejecutado correctamente").build();
			}
			//Si hay error se devuelve codigo 500
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo actualizar el usuario\n" + e.getStackTrace()).build();
		}
	}
    @GET
	@Path("{ID_usuario}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsuario(@Context UriInfo uriInfo, @PathParam("ID_usuario") String id) {
		try {
			//Pasamos el ID a entero
			int int_id = Integer.parseInt(id);
			//Consulta SQL que selecciona el usuario con el ID pedido
			String sql = "SELECT * FROM usuario where id=" + int_id + ";";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			//Si existe usuario con el ID pedido se devuelve el json con los datos del usuario
			if (rs.next()) {
				usuario usuario =  usuarioFromRS(rs);
				return Response.status(Response.Status.OK).entity(usuario).build();
			}
			//Si no se encuentra codigo 404
			else {
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
			}
		}
		//Si hay algún error codigo 500
		catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No puedo parsear a entero").build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}
    
	@DELETE
	@Path("{ID_usuario}")
	public Response deleteUsuario(@Context UriInfo uriInfo, @PathParam("ID_usuario") String id) {
		try {
			//Pasamos el ID a entero
			int int_id = Integer.parseInt(id);
			//Consulta SQL que elimina el usuario con el id pedido
			String sql = "DELETE FROM usuario WHERE ID ='" + int_id + "';";
			PreparedStatement ps = conn.prepareStatement(sql);
			int affectedRows = ps.executeUpdate();
			//Si se encuentra codigo 202
			if (affectedRows == 1) {
				return Response.status(Response.Status.NO_CONTENT).entity("Usuario eliminado correctamente").build();
			}
			//Si no se encuentra codigo 404
			else { 
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
			}
		} 
		//Si hay algun error codigo 500
		catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo eliminar el usuario\n" + e.getStackTrace()).build();
		}
	}
    
    @POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Path("{ID_usuario}/vino")
    public Response addVino(@Context UriInfo uriInfo, @PathParam("ID_usuario") String ID_usuario, puntua puntua) {		
    	try {
    		//Consulta SQL que añade el vino a la tabla puntua con la calificacion del usuario
			String sql = "INSERT INTO puntua (ID_vino, ID_usuario, calificacion, fecha_adicion) VALUES (?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		    ps.setInt(1, puntua.getID_vino());
		    ps.setInt(2, puntua.getID_usuario());
		    ps.setInt(3, puntua.getCalificacion());
		    ps.setString(4, puntua.getFecha_adicion());
			int affectedRows = ps.executeUpdate();
			
            //Si se ha ejecutado la consulta SQL se devuelve 201 + la uri del vino
            if (affectedRows > 0) {
                String location = uriInfo.getBaseUri() + "usuario/" + ID_usuario + "/vino/" + puntua.getID_vino();
                return Response.status(Response.Status.CREATED).entity("La URI del vino creado es: " + location)
                        .header("Location", location)
                        .header("Content-Location", location)
                        .build();
            }
            //Si hay algun error codigo 500
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo añadir el vino").build();

        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el vino\n" + e.getStackTrace()).build();
        }
    }

	@DELETE
    @Path("{ID_usuario}/vino/{ID_vino}")
	public Response deleteVino(@Context UriInfo uriInfo, @PathParam("ID_vino") String id, @PathParam("ID_usuario") String id_u) {
		try {
			//Pasamos el ID del vino y el ID del usuario a enteros
			int int_id = Integer.parseInt(id);
			int int_id_u= Integer.parseInt(id_u);
			//Codigo SQL que elimina un vino puntuado por un usuario de la tabla puntua
			String sql = "DELETE FROM puntua WHERE ID_vino='" + int_id + "' AND ID_usuario='" + int_id_u + "';";
			PreparedStatement ps = conn.prepareStatement(sql);
			int affectedRows = ps.executeUpdate();
			//Si se encuentra el vino se devuelve el codigo 202
			if (affectedRows == 1) {
				return Response.status(Response.Status.NO_CONTENT).entity("Se elimino el vino correctamente").build();
			}
			//Si no se encuentra dicho vino se devuelve codigo 404
			else {
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();		
			}
		}
		//Si hay algun error codigo 500
		catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo eliminar el vino de la lista del usuario \n" + e.getStackTrace()).build();
		}
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{ID_usuario}/vino/{ID_vino}")
	public Response updateVino(@Context UriInfo uriInfo, @PathParam("ID_usuario") String id, @PathParam("ID_vino") String id_v, puntua puntua) {
	    try {
	        // Pasamos los IDs del vino y del usuario a enteros
	        int int_id = Integer.parseInt(id);
	        int int_id_v = Integer.parseInt(id_v);
	        // Consulta SQL que modifica el vino de la lista de un usuario
	        String sql = "SELECT * FROM puntua WHERE ID_usuario = ? AND ID_vino = ?";
	        PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setInt(1, int_id);
	        ps.setInt(2, int_id_v);
	        ResultSet rs = ps.executeQuery();
	        // Si se encuentra se actualiza el vino y se devuelve codigo 200 + la uri del vino
	        if (rs.next()) {
	            sql = "UPDATE puntua SET calificacion = ?, fecha_adicion = ? WHERE ID_usuario = ? AND ID_vino = ?";
	            ps = conn.prepareStatement(sql);
	            ps.setInt(1, puntua.getCalificacion());
	            ps.setString(2, puntua.getFecha_adicion());
	            ps.setInt(3, int_id);
	            ps.setInt(4, int_id_v);
	            int affectedRows = ps.executeUpdate();
	            if (affectedRows > 0) {
	                // Location a partir del URI base (host + root de la aplicación + ruta del servlet)
					String location = uriInfo.getBaseUri() + "usuario/" + id + "/vino/" + id_v;
	                return Response.status(Response.Status.OK).entity("Calificacion actualizada correctamente")
	                        .header("Location", location)
	                        .header("Content-Location", location)
	                        .build();
	            } else {
	                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("La consulta SQL no se ha ejecutado correctamente").build();
	            }
	        } else {
	            return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
	        }
	    }
	    // Si hay algun error devuelve codigo 500
	    catch (SQLException e) {
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo actualizar la calificacion\n" + e.getStackTrace()).build();
	    }
	}
    
    @POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Path("{ID_seguido}/seguidor")
    public Response addSeguidor(@Context UriInfo uriInfo, @PathParam("ID_seguido") String ID_seguido, sigue sigue) {
		try {
	        // Consulta SQL que inserta un seguidor a un usuario
	        String sql = "INSERT INTO sigue (ID_seguidor, ID_seguido) VALUES (?, ?)";
	        PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setInt(1, sigue.getID_seguidor());
	        ps.setString(2, ID_seguido);
			int affectedRows = ps.executeUpdate();
			
            //Si inserta el seguidor se devuelve la uri del seguidor + codigo 201
            if (affectedRows > 0) {
                String location = uriInfo.getBaseUri() + "usuario/" + ID_seguido + "/seguidor/" + sigue.getID_seguidor();
                return Response.status(Response.Status.CREATED).entity("Seguidor añadido correctamente con la siguiente URI: " + location)
                        .header("Location", location)
                        .header("Content-Location", location)
                        .build();
            }
            // Si no hay affectedRows es que alguno de los 2 usuarios no existe
            else {
	            return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
            }

        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo añadir el seguidor \n" + e.getStackTrace()).build();
        }
    }
    
	@DELETE
    @Path("{ID_seguido}/seguidor/{ID_seguidor}")
	public Response deleteSeguidor(@Context UriInfo uriInfo, @PathParam("ID_seguido") String ID_seguido, @PathParam("ID_seguidor") String ID_seguidor) {
		try {
			//Pasamos los IDs del usuario y del seguidor a enteros
			int int_id = Integer.parseInt(ID_seguido);
			int int_id_s = Integer.parseInt(ID_seguidor);
			//Consulta SQL que elimina el seguidor de un usuario
			String sql = "DELETE FROM sigue WHERE ID_seguidor='" + int_id_s + "' AND ID_seguido='" + int_id + "';";
			PreparedStatement ps = conn.prepareStatement(sql);
			int affectedRows = ps.executeUpdate();
			//Si se encuentra codigo 202
			if (affectedRows == 1) {
				return Response.status(Response.Status.NO_CONTENT).entity("Seguidor eliminado correctamente").build();
			}
			//Si no se encuentra codigo 404
			else { 
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();		
			}
		}
		//Si hay algun error codigo 500
		catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo eliminar el seguidor de la lista de seguidores del usuario \n" + e.getStackTrace()).build();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsuario_patron(@Context UriInfo uriInfo, @QueryParam("patron") String patron) {
	    try {
	        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM usuario");
	        // Si se proporciona un patrón, agregar la cláusula WHERE
	        if (patron != null && !patron.isEmpty()) {
	            sqlBuilder.append(" WHERE nombre LIKE ?");
	        }

	        // Crear la consulta SQL final
	        String sql = sqlBuilder.toString();
	        PreparedStatement ps = conn.prepareStatement(sql);

	        // Si se proporciona un patrón, establecer el parámetro correspondiente
	        if (patron != null && !patron.isEmpty()) {
	            ps.setString(1, "%" + patron + "%");
	        }

	        ResultSet rs = ps.executeQuery();
	        List<usuario> usuarios = new ArrayList<>();
	        // Si hay usuarios con el patrón en el nombre, añadirlos a la lista de usuarios
	        while (rs.next()) {
	            usuario usuario = usuarioFromRS(rs);
	            usuarios.add(usuario);
	        }

	        // Devolver la lista de usuarios o código de error apropiado según la situación
	        if (!usuarios.isEmpty()) {
	            return Response.status(Response.Status.OK).entity(usuarios).build();
	        } else {
	            if (patron != null && !patron.isEmpty()) {
	                // Si se proporciona un patrón pero no se encuentran usuarios, devolver código 404
	                return Response.status(Response.Status.NOT_FOUND).entity("No se encontraron usuarios con el patrón especificado").build();
	            } else {
	                // Si no se proporciona un patrón y no hay usuarios, devolver código 404
	                return Response.status(Response.Status.NOT_FOUND).entity("No se encontraron usuarios").build();
	            }
	        }
	    } catch (SQLException e) {
	        // Si hay un error de acceso a la base de datos, devolver código de error interno del servidor
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a la base de datos").build();
	    }
	}
    
    @GET
	@Path("{ID_usuario}/vino")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVinos(@Context UriInfo uriInfo, @PathParam("ID_usuario") String id, @QueryParam("Limite") int limite, @QueryParam("fecha_adicion") String fecha_adicion, @QueryParam("nombre") String nombre, @QueryParam("bodega") String bodega, @QueryParam("pais") String pais, @QueryParam("añada") int añada, @QueryParam("tipo_vino") String tipo_vino ) {
		try {
			//Pasamos el ID del usuario a entero
			int int_id = Integer.parseInt(id);
			//Consulta SQL que devuelve los vinos de un usuario
            //Consulta SQL base
            StringBuilder sqlBuilder = new StringBuilder("SELECT v.* FROM puntua p JOIN vino v ON p.ID_vino = v.ID WHERE p.ID_usuario = ?");
            List<Object> params = new ArrayList<>();
            params.add(id);
            
            if (fecha_adicion != null && !fecha_adicion.isEmpty()) {
                sqlBuilder.append(" AND p.fecha_adicion = ?");
                params.add(fecha_adicion);
            }
            
         // Añadir las condiciones de filtrado según los parámetros proporcionados
            if (nombre != null && !nombre.isEmpty()) {
                sqlBuilder.append(" AND v.nombre = ?");
                params.add(nombre);
            }
            if (bodega != null && !bodega.isEmpty()) {
                sqlBuilder.append(" AND v.bodega = ?");
                params.add(bodega);
            }
            if (pais != null && !pais.isEmpty()) {
                sqlBuilder.append(" AND v.pais = ?");
                params.add(pais);
            }
            if (tipo_vino != null && !tipo_vino.isEmpty()) {
                sqlBuilder.append(" AND v.tipo_vino = ?");
                params.add(tipo_vino);
            }
            if (añada > 0) {
                sqlBuilder.append(" AND v.añada = ?");
                params.add(añada);
            }

            if (limite > 0) {
            	sqlBuilder.append(" LIMIT ? ");
            	params.add(limite);
            }
            
            
         // Crear la consulta SQL final
            String sql = sqlBuilder.toString();
            PreparedStatement ps = conn.prepareStatement(sql);

            // Establecer los parámetros en el PreparedStatement
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            // Ejecutar la consulta SQL y procesar los resultados
            ResultSet rs = ps.executeQuery();
            //Lista de vinos
            List<vino> vinos = new ArrayList<>();
            //Si hay elementos en el ResultSet se añade el vino a la lista
            while (rs.next()) {
                vino vino = vinoFromRS(rs);
                vinos.add(vino);
            }

            //Si la lista no esta vacia codigo 200 + lista json de vinos
            if (!vinos.isEmpty()) {
                return Response.status(Response.Status.OK).entity(vinos).build();
            }
            //Si la lista esta vacia codigo 404
            else {
                return Response.status(Response.Status.NOT_FOUND).entity("No se encontraron vinos con los criterios especificados para este usuario").build();
            }
        }
        //Si hay algun error codigo 500
        catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Los parámetros de ID deben ser números enteros válidos").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a la base de datos").build();
        }
    }
    
    @GET
	@Path("{ID_usuario}/seguidor")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSeguidores(@Context UriInfo uriInfo, @PathParam("ID_usuario") String id, @QueryParam("patron") String patron, @QueryParam("limite") int limite) {
		try {
			//Pasamos el ID del usuario a entero
			int int_id = Integer.parseInt(id);
			//Consulta SQL base
			StringBuilder sqlBuilder = new StringBuilder("SELECT u.* FROM sigue s JOIN usuario u ON s.ID_seguidor = u.ID WHERE s.ID_seguido = ?");
	        // Lista para mantener los parámetros de PreparedStatement
            List<Object> params = new ArrayList<>();
            params.add(int_id);

            // Añadir las condiciones de filtrado según los parámetros proporcionados
            if (patron != null && !patron.isEmpty()) {
                sqlBuilder.append(" AND u.nombre LIKE ?");
                params.add("%" + patron + "%");
            }
            
            if(limite > 0) {
            	sqlBuilder.append(" LIMIT ?");
            	params.add(limite);
            }
            
            // Crear la consulta SQL final
            String sql = sqlBuilder.toString();
            PreparedStatement ps = conn.prepareStatement(sql);

            // Establecer los parámetros en el PreparedStatement
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            // Ejecutar la consulta SQL y procesar los resultados
            ResultSet rs = ps.executeQuery();
            //Lista para añadir los seguidores
            List<usuario> seguidores = new ArrayList<>();
            //Si hay elementos en el ResultSet se añade a la lista de seguidores
            while (rs.next()) {
                usuario seguidor = usuarioFromRS(rs);
                seguidores.add(seguidor);
            }

            //Si la lista no esta vacia codigo 200 + json de la lista de seguidores
            if (!seguidores.isEmpty()) {
                return Response.status(Response.Status.OK).entity(seguidores).build();
            }
            //Si la lista esta vacia codigo 404
            else {
                return Response.status(Response.Status.NOT_FOUND).entity("No se han encontrado seguidores para este usuario").build();
            }
        }
		//Si hay algun error codigo 500
		catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No puedo parsear a entero").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
        }
	}
    
    @GET
    @Path("{ID_usuario}/seguidor/{ID_seguidor}/vinos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVinosSeguidor(@Context UriInfo uriInfo, @PathParam("ID_usuario") String idUsuario, @PathParam("ID_seguidor") String idSeguidor, @QueryParam("limite") int limite, @QueryParam("fecha_adicion") String fecha_adicion, @QueryParam("nombre") String nombre, @QueryParam("bodega") String bodega, @QueryParam("pais") String pais, @QueryParam("añada") int añada, @QueryParam("tipo_vino") String tipo_vino) {
        try {
        	//Pasamos los IDs del usuario y el vino a enteros
            int intIdUsuario = Integer.parseInt(idUsuario);
            int intIdSeguidor = Integer.parseInt(idSeguidor);

            StringBuilder sqlBuilder = new StringBuilder("SELECT v.* FROM puntua p " +
                    "JOIN vino v ON p.ID_vino = v.ID " +
                    "JOIN sigue s ON p.ID_usuario = s.ID_seguidor " +
                    "WHERE p.ID_usuario = ? AND s.ID_seguido = ?");
            List<Object> params = new ArrayList<>();
            params.add(intIdSeguidor);
            params.add(intIdUsuario);

            // Agregar condiciones de filtrado según los parámetros proporcionados
            if (bodega != null && !bodega.isEmpty()) {
                sqlBuilder.append(" AND v.bodega = ?");
                params.add(bodega);
            }
            if (añada > 0) {
                sqlBuilder.append(" AND v.añada = ?");
                params.add(añada);
            }
            if (pais!= null && !pais.isEmpty()) {
                sqlBuilder.append(" AND v.pais = ?");
                params.add(pais);
            }
            if (nombre != null && !nombre.isEmpty()) {
                sqlBuilder.append(" AND v.nombre = ?");
                params.add(nombre);
            }
            if (tipo_vino != null && !tipo_vino.isEmpty()) {
                sqlBuilder.append(" AND v.tipo_vino = ?");
                params.add(tipo_vino);
            }
            if (fecha_adicion != null && !fecha_adicion.isEmpty()) {
                sqlBuilder.append(" AND p.fecha_adicion = ?");
                params.add(fecha_adicion);
            }

            if (limite > 0) {
            	sqlBuilder.append(" LIMIT ? ");
            	params.add(limite);
            }
            // Crear la consulta SQL final
            String sql = sqlBuilder.toString();
            PreparedStatement ps = conn.prepareStatement(sql);

            // Establecer los parámetros en el PreparedStatement
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            // Ejecutar la consulta SQL y procesar los resultados
            ResultSet rs = ps.executeQuery();
            //Lista de vinos
            List<vino> vinos = new ArrayList<>();
            //Si hay elementos en el ResultSet se añade el vino a la lista
            while (rs.next()) {
                vino vino = vinoFromRS(rs);
                vinos.add(vino);
            }

            //Si la lista no esta vacia codigo 200 + lista json de vinos
            if (!vinos.isEmpty()) {
                return Response.status(Response.Status.OK).entity(vinos).build();
            }
            //Si la lista esta vacia codigo 404
            else {
                return Response.status(Response.Status.NOT_FOUND).entity("No se encontraron vinos con los criterios de búsqueda especificados").build();
            }
        }
        //Si hay algun error codigo 500
        catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Los parámetros de ID deben ser números enteros válidos").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a la base de datos").build();
        }
    }
    
    // Método para obtener el sistema de recomendaciones personalizado para un usuario
    @GET
    @Path("{ID_usuario}/recomendacion")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecomendacion(@Context UriInfo uriInfo, @PathParam("ID_usuario") String idUsuario) {
        try {
        	//Pasamos el ID del usuario a entero
            int intIdUsuario = Integer.parseInt(idUsuario);
            // Lista para almacenar los resultados
            List<Object> resultados = new ArrayList<>();

            // Consulta para obtener la información del usuario
            String sqlInfoUsuario = "SELECT * FROM usuario WHERE ID = ?";
            PreparedStatement psInfoUsuario = conn.prepareStatement(sqlInfoUsuario);
            psInfoUsuario.setInt(1, intIdUsuario);
            ResultSet rsInfoUsuario = psInfoUsuario.executeQuery();
            //Si hay elementos en el ResultSet se añade el usuario a la lista resultado
            if (rsInfoUsuario.next()) {
                usuario usuario = usuarioFromRS(rsInfoUsuario);
                resultados.add(usuario);
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Usuario no encontrado").build();
            }

            // Consulta para obtener los 5 últimos vinos añadidos por el usuario
            String sqlUltimosVinos = "SELECT v.* FROM puntua p JOIN vino v ON p.ID_vino = v.ID WHERE p.ID_usuario = ? ORDER BY p.fecha_adicion DESC LIMIT 5";
            PreparedStatement psUltimosVinos = conn.prepareStatement(sqlUltimosVinos);
            psUltimosVinos.setInt(1, intIdUsuario);
            ResultSet rsUltimosVinos = psUltimosVinos.executeQuery();
            //Lista para almacenar los ultimos vinos
            List<vino> ultimosVinos = new ArrayList<>();
            //Si hay elementos en el ResultSet se añade el vino a la lista de ultimosvinos
            while (rsUltimosVinos.next()) {
                vino vino = vinoFromRS(rsUltimosVinos);
                ultimosVinos.add(vino);
            }
            //Se añade la lista de ultimosvinos a la lista resultados
            resultados.add(ultimosVinos);

            // Consulta para obtener los 5 vinos con mayor puntuación del usuario
            String sqlMejoresPuntuados = "SELECT v.* FROM puntua p JOIN vino v ON p.ID_vino = v.ID WHERE p.ID_usuario = ? ORDER BY p.calificacion DESC LIMIT 5";
            PreparedStatement psMejoresPuntuados = conn.prepareStatement(sqlMejoresPuntuados);
            psMejoresPuntuados.setInt(1, intIdUsuario);
            ResultSet rsMejoresPuntuados = psMejoresPuntuados.executeQuery();
            //Lista para añadir los vinos mejores puntuados
            List<vino> mejoresPuntuados = new ArrayList<>();
            //Si hay vinos en el ResultSet se añade a la lista mejoresPuntuados
            while (rsMejoresPuntuados.next()) {
                vino vino = vinoFromRS(rsMejoresPuntuados);
                mejoresPuntuados.add(vino);
            }
            //Se añade mejoresPuntuados a resultados
            resultados.add(mejoresPuntuados);

            // Consulta para obtener los 5 mejores vinos de todos sus amigos
            String sqlMejoresVinosAmigos = "SELECT v.* FROM puntua p JOIN vino v ON p.ID_vino = v.ID JOIN sigue s ON p.ID_usuario = s.ID_seguidor WHERE s.ID_seguido = ? ORDER BY p.calificacion DESC LIMIT 5";
            PreparedStatement psMejoresVinosAmigos = conn.prepareStatement(sqlMejoresVinosAmigos);
            psMejoresVinosAmigos.setInt(1, intIdUsuario);
            ResultSet rsMejoresVinosAmigos = psMejoresVinosAmigos.executeQuery();
            //Lista para almacenar los mejores vinos de tus amigos
            List<vino> mejoresAmigos = new ArrayList<>();
            //Si hay vinos en el ResultSet se añade a la lista mejoresAmigos
            while (rsMejoresVinosAmigos.next()) {
                vino vino = vinoFromRS(rsMejoresVinosAmigos);
                mejoresAmigos.add(vino);
            }
            //Se añade la lista mejoresAmigos a resultados
            resultados.add(mejoresAmigos);
            //Se devuelve el json de la lista resultados + codigo 200
            return Response.status(Response.Status.OK).entity(resultados).build();
        }
        //Si hay algun error codigo 500
        catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("El ID de usuario debe ser un número entero válido").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a la base de datos").build();
        }
    }
    
    private vino vinoFromRS (ResultSet rs) throws SQLException {
		vino vino = new vino ();
		vino.setID(rs.getInt("ID"));
	    vino.setNombre(rs.getString("nombre"));
	    vino.setBodega(rs.getString("bodega"));
	    vino.setPais(rs.getString("pais"));
	    vino.setTipo_vino(rs.getString("tipo_vino"));
	    vino.setAñada(rs.getInt("añada"));
	    
    // Inicializar la lista de tipo_uva
	    List<tipo_uva> tipoUvas = new ArrayList<>();

	    // Consultar la tabla formado para obtener los tipos de uva asociados a este vino
	    PreparedStatement ps = null;
	    ResultSet rsFormado = null;
	        String sql = "SELECT * FROM formado WHERE ID_vino = ?";
	        ps = conn.prepareStatement(sql);
	        ps.setInt(1, vino.getID());
	        rsFormado = ps.executeQuery();

	        while (rsFormado.next()) {
	            // Crear un objeto tipo_uva y configurarlo con los datos del ResultSet
	            tipo_uva uva = new tipo_uva();
	            uva.setID(rsFormado.getInt("ID_tipo_uva"));
	            // Agregar el objeto tipo_uva a la lista tipoUvas
	            tipoUvas.add(uva);
	        }
	    // Asignar la lista tipoUvas al objeto vino
	    vino.setTipo_uva(tipoUvas);
	    return vino;	
    }
    
    
    private usuario usuarioFromRS (ResultSet rs) throws SQLException {
		usuario usuario = new usuario ();
		usuario.setID(rs.getInt("id"));
	    usuario.setNombre(rs.getString("nombre"));
	    usuario.setCorreo_electronico(rs.getString("correo_electronico"));
	    usuario.setFecha_nacimiento(rs.getString("fecha_nacimiento"));
		return usuario;
	}
    
    //Metodo auxiliar que comprueba si una desde una fecha han pasado 18 años, para saber si un usuario es mayor de edad o no
    public static boolean esMayorDeEdad(String fechaNacimiento) {
        // Convertir el String de fecha en un objeto LocalDate
        LocalDate fechaNac = LocalDate.parse(fechaNacimiento);
        // Obtener la fecha actual
        LocalDate fechaActual = LocalDate.now();
        // Calcular la diferencia de años entre la fecha de nacimiento y la fecha actual
        Period periodo = Period.between(fechaNac, fechaActual);
        // Comparar si la persona tiene al menos 18 años
        return periodo.getYears() >= 18;
    }
}
