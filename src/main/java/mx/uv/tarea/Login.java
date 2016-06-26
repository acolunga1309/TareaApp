package mx.uv.tarea;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import mx.uv.tarea.models.Usuario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import static spark.Spark.get;
import static spark.Spark.post;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;



/**
 * Login
 *
 * @author José Alejandro Colunga Moreno
 */
public class Login {

    private static final Logger LOGGER = LogManager.getLogger("Login");
    
    
    public static void main(String[] args) {
       
        /**
         * Path inicial
         */
        get("/", (req, res) -> {
            Map<String, Object> attributes = new HashMap<>();
            String validador = "";
            String nombre = "";
            String mensaje = "";
            
            attributes.put("validador", validador);
            attributes.put("nombre", nombre);
            attributes.put("mensaje", mensaje);
            
            return new ModelAndView(attributes, "registrar.ftl");
        }, new FreeMarkerEngine());

        /**
         * Path Registro
         */
        post("/registro", new TemplateViewRoute() {
            @Override
            public ModelAndView handle(Request req, Response res) throws Exception {
                Map<String, Object> attributes = new HashMap<>();
                
                String nombre = req.queryParams("nombre");
                String email = req.queryParams("email");
                String password = req.queryParams("password");
                String apellidos = req.queryParams("apellidos");
                String validador = "";
                String mensaje = "";
                
                if(nombre.equals("")){                        
                    validador = "Complete el campo nombre";
                }else if (email.equals("")){
                    validador = "Complete el campo email";
                }else if (password.equals("")){
                    validador = "Complete el campo password";
                }
                // Guardar en BD
                
                try{
                    EntityManagerFactory emf = Persistence.
                            createEntityManagerFactory("TareaAppPU");
                    
                    EntityManager em = emf.createEntityManager();
                    Usuario usuario = new Usuario(email, nombre, apellidos, password);
                    
                    em.getTransaction().begin();
                    em.persist(usuario);
                    em.getTransaction().commit();
                    em.close();
                    
         
                }catch(Exception ex){
                    validador="No se pudo registrar";
                    LOGGER.error(String.format("El usuario %s no se pudo guardar. %s", email,ex.getMessage()));
                }
                
                // Fin guardar en BD
                attributes.put("validador", validador);
                attributes.put("nombre", nombre);
                attributes.put("mensaje", mensaje);
                //TODO Guardar en Base de datos.
                
                return new ModelAndView(attributes, "registrar.ftl");
            }
        }, new FreeMarkerEngine());
        
        /**
         * Path verificar inicio de sesión.
         */
        post("/login", (req, res) -> {
            Map<String, Object> attributes = new HashMap<>();
            String email = req.queryParams("email");
            String password = req.queryParams("password");
            
            String validador = "";
            String nombre = "";
            String mensaje = "";
            
            String usuario = "n/a";
            
            attributes.put("validador", validador);
            attributes.put("nombre", nombre);
            
            
            try{
                    EntityManagerFactory emf = Persistence.
                            createEntityManagerFactory("TareaAppPU");
                    EntityManager em = emf.createEntityManager();
                    
                    Query q = em.createQuery("SELECT u from Usuario u WHERE u.email=:arg1");
                    q.setParameter("arg1", email);
                    Usuario usuarioToValidate = (Usuario) q.getSingleResult();
                    
                    if(usuarioToValidate.getPassword().equals(usuarioToValidate.getPassword())){
                        LOGGER.trace(String.format("El usuario %s ha iniciado sesión.", email));
                        usuario = email;
                        attributes.put("mensaje", mensaje);
                        attributes.put("usuario", usuario);
                        return new ModelAndView(attributes, "home.ftl");
                    }else{
                        mensaje = "Usuario y/o Password son incorrectos.";
                    }
                    
   
                    
            }catch(Exception ex){
                   // validador="No se pudo registrar";
                    //LOGGER.error(String.format("El usuario %s no se pudo guardar. %s", email,ex.getMessage()));
             }
            
            attributes.put("mensaje", mensaje);
            
            return new ModelAndView(attributes, "registrar.ftl");
        }, new FreeMarkerEngine());
    }

}
