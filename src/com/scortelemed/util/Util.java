package com.scortelemed.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.xml.rpc.ServiceException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import com.zoho.services.Frontal;
import com.zoho.services.FrontalService;
import com.zoho.services.Usuario;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class Util {

	public static final Logger log = Logger.getLogger(Util.class);

	/**
	   * ARCHIVAMOS LOS INTERFACES DEL DIA ANTERIOR
	   * 
	   */
	  public static void archivarInterfacesAnteirores(String rutaSalida, String rutaArchivado) {

	    Collection <File> ficheros = null;
	    File carpetaArchivado = null;
	    Iterator<File> lista = null;
	    File fichero = null;
	    try {

	      /**
	       * Archivado de ficheros enviados
	       */
		  log.info("ruta de salida: " + rutaSalida);
	      ficheros = FileUtils.listFiles(new File(rutaSalida), null, false);
	      if (ficheros.size() > 0) {
	        carpetaArchivado = new File(rutaArchivado + "\\" + new SimpleDateFormat("yyyyMMdd").format(new Date()));
	        lista = ficheros.iterator();
	        while (lista.hasNext()) {
	          fichero = lista.next();
	            FileUtils.moveFileToDirectory(fichero, carpetaArchivado, true);
	            log.info("Se ha movido el fichero " + fichero.getName() + " a la carpeta de archivado " + carpetaArchivado.getName());
	        }
	      } else {
			  log.info("No hay ficheros que archivar en " + rutaSalida);
		  }

	    } catch (Exception e) {
	      log.info("ERROR: Excepcion en archivarInterfacesAnteirores. " + e.getMessage());
	    }

	  }

	  /**
	   * INSTANCIAMOS EL FRONTAL
	   * 
	   * @throws ServiceException
	   * @throws Exception
	   */
	  public static Frontal instanciarFrontal(String frontalPortAddress) throws Exception {
		  Frontal frontal = null;
		  try {
			  // Crear URL del WSDL
			  URL url = new URL(frontalPortAddress + "?WSDL");

			  // Instanciar el servicio
			  FrontalService fs = new FrontalService(url);
			  frontal = fs.getFrontalPort();

			  if (frontal == null) {
				  log.error("No se pudo instanciar el port Frontal");
				  throw new Exception("No se pudo instanciar el port Frontal");
			  }

			  // Cambiar dinámicamente el endpoint
			  Map<String, Object> context = ((BindingProvider) frontal).getRequestContext();
			  context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, frontalPortAddress);

			  // Opcional: configurar timeouts (en milisegundos)
			  context.put("com.sun.xml.internal.ws.connect.timeout", 5000); // tiempo de conexión
			  context.put("com.sun.xml.internal.ws.request.timeout", 60000); // tiempo de respuesta

			  log.info("Frontal SOAP inicializado correctamente en: " + frontalPortAddress);

		  } catch (MalformedURLException e) {
			  log.error("URL del WSDL inválida -> " + frontalPortAddress);
			  throw e;
		  } catch (WebServiceException e) {
			  log.error("No se pudo conectar con el servicio SOAP -> " + e.getMessage());
			  throw e;
		  } catch (Exception e) {
			  log.error("Error inesperado al instanciar Frontal -> " + e.getMessage());
			  throw e;
		  }

		  return frontal;
	  }

	  public static Usuario obtenerUsuario(String clave, String dominio, String unidadOrganizativa, String nombreUsuario) throws Exception {

		  Usuario user = new Usuario();
		  user.setClave(clave);
		  user.setDominio(dominio);
		  user.setUnidadOrganizativa(unidadOrganizativa);
		  user.setUsuario(nombreUsuario);
		  return user;

	  }

  
}
