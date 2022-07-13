package it.polito.tdp.bar.model;

import java.util.Comparator;

public class Tavolo implements Comparator<Tavolo> {
	
	private int posti;
	private boolean occupato; // flag che ci indica lo stato di un tavolo
	
	public Tavolo(int posti, boolean occupato) {
		super();
		this.posti = posti;
		this.occupato = occupato;
	}

	public int getPosti() {
		return posti;
	}

	public void setPosti(int posti) {
		this.posti = posti;
	}

	public boolean isOccupato() {
		return occupato;
	}

	public void setOccupato(boolean occupato) {
		this.occupato = occupato;
	}

	@Override
	public int compare(Tavolo o1, Tavolo o2) {
		// TODO Auto-generated method stub
		return o1.getPosti()-o2.getPosti();
	}

	
	
	
	

}
