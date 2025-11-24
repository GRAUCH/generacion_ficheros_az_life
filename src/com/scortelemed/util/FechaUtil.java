package com.scortelemed.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class FechaUtil {

  private FechaUtil() {

  }

  public static boolean fechaEnRango (String fecha, String rango1, String rango2){
	  boolean resultado =false;
	  SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd");
	  SimpleDateFormat df2 = new SimpleDateFormat("yyyy/MM/dd");
	  Calendar fechaIni = Calendar.getInstance();
	  Calendar fechaFin = Calendar.getInstance();
	  Calendar dia= Calendar.getInstance();

	  try {
		  fechaIni.setTime(df1.parse(rango1));
		  fechaFin.setTime(df1.parse(rango2));
		  dia.setTime(df2.parse(fecha));
	  } catch (ParseException e) {

		  e.printStackTrace();
	  }


	  if ((dia.after(fechaIni) || dia.equals(fechaIni)) && (dia.before(fechaFin) || dia.equals(fechaFin))){
		  resultado=true;
	  }
	  return resultado;

  }

  public static boolean fechaEnRango (Date fecha, String rango1, String rango2){
	  boolean resultado =false;
	  SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd");
	  Calendar fechaIni = Calendar.getInstance();
	  Calendar fechaFin = Calendar.getInstance();
	  Calendar dia= Calendar.getInstance();

	  try {
		  fechaIni.setTime(df1.parse(rango1));
		  fechaFin.setTime(df1.parse(rango2));
		  dia.setTime(fecha);
	  } catch (ParseException e) {

		  e.printStackTrace();
	  }


	  if ((dia.after(fechaIni) || dia.equals(fechaIni)) && (dia.before(fechaFin) || dia.equals(fechaFin))){
		  resultado=true;
	  }
	  return resultado;

  }
	public static Date formateaCampoFecha(String fechaStr) {

		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		Date fecha=null;
		try {
			fecha=sd.parse(fechaStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println("fecha "+fechaStr +"mal parseada");
			e.printStackTrace();
		}
		return fecha;

	}

}
