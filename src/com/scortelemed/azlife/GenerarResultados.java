package com.scortelemed.azlife;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import com.zoho.services.CoberturaExpediente;
import com.zoho.services.Expediente;

import com.zoho.services.ExpedienteServicio;
import org.apache.log4j.Logger;
import com.scortelemed.util.Constants;
import com.scortelemed.entidades.Cobertura;


public class GenerarResultados {

	private Properties propiedades;
	private Properties coberturas;
	public static final Logger log = Logger.getLogger(GenerarResultados.class);

	public GenerarResultados(Properties propiedades, Properties coberturas) {
		super();
		this.propiedades = propiedades;
		this.coberturas = coberturas;
	}

	void generarFichero(List<Expediente> expedientes, String fechaFin) {

		try (BufferedWriter ficheroSalidaAux = new BufferedWriter(new FileWriter(propiedades.getProperty("rutaSalida") + fechaFin + propiedades.getProperty("ficheroResultados")))) {
			ficheroSalidaAux.append(procesaCabecera());
			ficheroSalidaAux.newLine();

			// OBTENEMOS TODAS LAS FECHAS COMPRENDIDAS ENTRE UNA FECHA DE INICIO Y UNA FECHA. ESTAS SON LAS FECHAS PARA CONSULTAR
			if (expedientes != null) {
				for (Expediente actual : expedientes) {
					log.info("## Tratando expediente candidato " + actual.getCodigoST());
					ficheroSalidaAux.append(procesarLineaDetalle(actual));
					ficheroSalidaAux.newLine();
				}
			}

		} catch (IOException e) {
			log.error("Fallo al procesar el fichero. ", e);
		}
	}

	private List<Cobertura> crearListaCoberturas() {
		List<Cobertura> salida = new ArrayList<>();
		for (Entry<Object, Object> entrada : coberturas.entrySet()) {
			salida.add(0, new Cobertura((String) entrada.getKey(), (String) entrada.getValue()));
		}
		return salida;
	}

	private String procesarLineaDetalle(Expediente expediente) {
		StringBuilder ficheroSalida = new StringBuilder();
		List<Cobertura> listaCoberturas = crearListaCoberturas();
		transformaCoberturas(listaCoberturas, expediente);

		if (expediente.getCodigoProductoCIA() != null) {
			ficheroSalida.append(expediente.getCodigoProductoCIA());
		}
		ficheroSalida.append(Constants.SEPARADOR);
		if (expediente.getNumSolicitud() != null) {
			ficheroSalida.append(expediente.getNumSolicitud());
		}
		ficheroSalida.append(Constants.SEPARADOR);
		if (expediente.getNumPoliza() != null) {
			ficheroSalida.append(expediente.getNumPoliza());
		}
		ficheroSalida.append(Constants.SEPARADOR);
		if (expediente.getServicios() != null) {
			ficheroSalida.append(dameDescripcionServicio(expediente.getServicios()));
		}
		ficheroSalida.append(Constants.SEPARADOR);

		ficheroSalida.append(procesarDetallesCoberturas(listaCoberturas, expediente));

		return ficheroSalida.toString();
	}

	private String procesarDetallesCoberturas(List<Cobertura> entrada, Expediente expediente) {
		StringBuilder lineaDetalle = new StringBuilder();
		StringBuilder resultados = new StringBuilder();
		StringBuilder primas = new StringBuilder();
		StringBuilder capitales = new StringBuilder();
		StringBuilder exclusiones = new StringBuilder();
		StringBuilder premios = new StringBuilder();

		for (Cobertura actual : entrada) {
			resultados.append(mapearResultadoCobertura(actual.getResult())).append(Constants.SEPARADOR);
			primas.append(checkearValor(actual.getPrima())).append(Constants.SEPARADOR);
			capitales.append(checkearValor(actual.getCapital())).append(Constants.SEPARADOR);
			exclusiones.append(checkearValor(actual.getExclusiones())).append(Constants.SEPARADOR);
			// No sabemos como se agregan las columnas de Premio por tanto agregamos separador.
			premios.append(Constants.SEPARADOR);
		}

		lineaDetalle.append(resultados.toString());
		lineaDetalle.append(primas.toString());
		lineaDetalle.append(capitales.toString());
		lineaDetalle.append(exclusiones.toString());
		lineaDetalle.append(premios.toString());

		return lineaDetalle.toString();
	}

	private String procesaCabecera() {
		List<Cobertura> listaCoberturas = crearListaCoberturas();
		StringBuilder cabecera = new StringBuilder("Prodotto;Proposta;Polizza;Tipologia Ultimo Underwriting;");
		cabecera.append(procesaTitulo("Esito ", listaCoberturas));
		cabecera.append(procesaTitulo("% Maggiorazione del Premio ", listaCoberturas));
		cabecera.append(procesaTitulo("‰ Maggiorazione del Premio ", listaCoberturas));
		cabecera.append(procesaTitulo("Motivazione Esito ", listaCoberturas));
		cabecera.append(procesaTitulo("Premio ", listaCoberturas));
		return cabecera.toString();
	}

	private String procesaTitulo(String prefijo, List<Cobertura> coberturas) {
		StringBuilder salida = new StringBuilder();
		for (Cobertura actual : coberturas) {
			salida.append(prefijo);
			salida.append(actual.getName());
			salida.append(Constants.SEPARADOR);
		}
		return salida.toString();
	}

	private void transformaCoberturas(List<Cobertura> entrada, Expediente expediente) {
		List<CoberturaExpediente> coberturasExpediente = expediente.getCoberturasExpediente();

		if (expediente.getCoberturasExpediente() != null) {
			for (Cobertura actual : entrada) {
				for (CoberturaExpediente nueva : coberturasExpediente) {
					if (nueva.getCodigoCobertura().equals(actual.getCode())) {
						transformaResultado(actual, nueva);
						break;
					}
				}
			}
		}
	}

	private String checkearValor(String entrada) {
		if (entrada != null) {
			return entrada.replace(",", ".");
		} else {
			return "";
		}
	}

	private String mapearResultadoCobertura(String codResultado) {

		if (codResultado != null) {
			switch (codResultado.trim()) {
				case Constants.R_1:
					return Constants.SI;
				case "2":
					return Constants.NO_DISATTIVAZIONE;
				case "7":
					return Constants.NO;
				case "8":
					return Constants.NO;
				case "9":
					return Constants.NO;
				case "13":
					return Constants.NO_DISATTIVAZIONE;
				case "20":
					return Constants.NO;
				case Constants.R_3:
					return Constants.SI_SOVRAPPREMIO;
				case Constants.R_30:
					return Constants.SI_SOVRAPPREMIO;
				case Constants.R_31:
					return Constants.SI_SOVRAPPREMIO;
				// Si viene a Constants.SI hay que dejarlo (porque se ha puesto antes).
				case Constants.SI:
					return Constants.SI;
				default:
					return "";
			}
		} else {
			return "";
		}
	}

	private String dameDescripcionServicio(List<ExpedienteServicio> listaServicios) {
		if (listaServicios == null || listaServicios.isEmpty()) {
			return "";
		}

		boolean esProd = "1080".equals(propiedades.getProperty("codigo_cia"));

		if (listaServicios.size() == 1) {
			// Solo hay un servicio
			if (listaServicios.get(0).getCompanyaServicio() != null && listaServicios.get(0).getCompanyaServicio().getCodigoCompanya() != null && !listaServicios.get(0).getCompanyaServicio().getCodigoCompanya().isEmpty()) {

				String codigo = listaServicios.get(0).getCompanyaServicio().getCodigoCompanya();

				if (codigo != null) {
					if (esProd) {
						switch (codigo) {
							case "002503":
								return "QUESTIONNAIRE";
							case "002968":
								return "MEDICALEXAMINATION";
							default:
								return "";
						}
					} else { // PREPRO
						switch (codigo) {
							case "002502":
								return "QUESTIONNAIRE";
							case "002503":
								return "MEDICALEXAMINATION";
							default:
								return "";
						}
					}
				}
			}
				return "";
			} else {
				// Más de un servicio => TELEUNDERWRITING
				return "TELEUNDERWRITING";
		}
	}
	
	//se transforman todos los resultados para no incluir sobreprimas menos de 50 y sobremortalidades menores de 1
	private Cobertura transformaResultado(Cobertura cobertura, CoberturaExpediente coberturaExpediente){
		boolean cambiarResultadoCobertura=false;
		cobertura.setResult(coberturaExpediente.getCodResultadoCobertura());
		cobertura.setPrima(coberturaExpediente.getValoracionPrima());
		cobertura.setCapital(coberturaExpediente.getValoracionCapital());
		
		//Excluimos sobreprimas inferiores o igual a 50
		if (coberturaExpediente.getCodResultadoCobertura().equals(Constants.R_3)
				|| coberturaExpediente.getCodResultadoCobertura().equals(Constants.R_30) ){
			int resultado= Integer.parseInt(coberturaExpediente.getValoracionPrima());
			if (resultado<=50){
				// Poner a null los valores de prima y capital y resultado al valor Constants.SI.
				cobertura.setResult(Constants.SI);
				cobertura.setPrima(null);
				cobertura.setCapital(null);
				cambiarResultadoCobertura=true;
			}
		}
		
		//Excluimos sobremortalidades inferiores o igual a 1
		if (coberturaExpediente.getCodResultadoCobertura().equals(Constants.R_3)
				|| coberturaExpediente.getCodResultadoCobertura().equals(Constants.R_31) ){
			double resultado= Double.parseDouble(checkearValor(coberturaExpediente.getValoracionCapital()));
			if (resultado<=1.0){
				// Se ponen a null los valores en el fichero de salida.
				cobertura.setResult(Constants.SI);
				cobertura.setPrima(null);
				cobertura.setCapital(null);
				cambiarResultadoCobertura=true;
			}else{
				//por si acaso se pretende cambiar el resultado para prima, pero al ser la de capital alto no se debe hacer.
				cambiarResultadoCobertura=false;
			}
		}
		
		//Cambiamos el tipo en caso de que se haya producido una modificación
		if (cambiarResultadoCobertura){
			coberturaExpediente.setCodResultadoCobertura(Constants.R_1);
		}
		
		return cobertura;
	}
}

