package com.scortelemed.azlife;

import java.io.FileInputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.zoho.services.Expediente;
import com.zoho.services.Frontal;
import com.zoho.services.RespuestaCRMInforme;
import com.zoho.services.Usuario;
import org.apache.log4j.Logger;

import com.scortelemed.util.Util;

public class GenerarFicheros implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final Logger log = Logger.getLogger(GenerarFicheros.class);
	private static Properties propiedades = new Properties();
	private static Properties coberturas = new Properties();
	private static Properties propiedadesMail = new Properties();

	public static void main(final String[] args) {

		Usuario usuario;
		Frontal frontal;
		String fechaInicio = null;
		String fechaFin = null;

		try {

			propiedades.load(new FileInputStream("conexionLOCAL.properties"));
			coberturas.load(new FileInputStream("coberturas.properties"));
			propiedadesMail.load(new FileInputStream("direccionesMail.properties"));

			if (args.length == 2) {
				fechaInicio = args[0];
				fechaFin = args[1];
			} else if (args.length == 1) {
				fechaInicio = args[0];
				fechaFin = args[0];
			} else if (args.length == 0) {
				fechaInicio = new SimpleDateFormat("yyyyMMdd").format(new Date());
				fechaFin = new SimpleDateFormat("yyyyMMdd").format(new Date());
			} else {
				log.error("Parametros: 0, 1 o 2 fechas en formato YYYYMMDD. La compañia la saca del properties");
				System.exit(1);
			}

			log.info("###Inicio proceso generacion ficheros AZ Life a fecha " + new Date().toString());

			Util.archivarInterfacesAnteirores(propiedades.getProperty("rutaSalida"),
					propiedades.getProperty("rutaArchivado"));

			frontal = Util.instanciarFrontal(propiedades.getProperty("frontalPort_address"));

			usuario = Util.obtenerUsuario(propiedades.getProperty("clave"), propiedades.getProperty("dominio"),
					propiedades.getProperty("unidadOrganizativa"), propiedades.getProperty("usuario"));

			log.info("Consultado expediente para fechas: " + fechaInicio + "-" + fechaFin);
			RespuestaCRMInforme respuestaCRM = frontal.informeExpedientes(usuario,
					propiedades.getProperty("codigo_cia"), null, 1, fechaInicio, fechaFin);

			log.info("Informe expediente ha devuelto " + respuestaCRM.getListaExpedientesInforme().size() + " expedientes");
			List<Expediente> expedientesPagos = obtenerListaExpedientesAprocesar(respuestaCRM);

			log.info("Genero fichero de resultados");
			GenerarResultados generarAltas = new GenerarResultados(propiedades, coberturas);
			generarAltas.generarFichero(expedientesPagos, fechaFin);

		} catch (Exception e) {
			log.error("ERROR: Excepcion en main de la clase GenerarFicheros.class ", e);
		}

		log.info("###Fin proceso de revision a fecha " + new Date().toString());

	}

	private static List<Expediente> obtenerListaExpedientesAprocesar(RespuestaCRMInforme respuestaCRM) {
		List<Expediente> listaExpedientesPagos = new ArrayList<>();
		if (respuestaCRM != null && respuestaCRM.getListaExpedientesInforme() != null) {
			listaExpedientesPagos.addAll(respuestaCRM.getListaExpedientesInforme());
		}
		return listaExpedientesPagos;
	}
}
