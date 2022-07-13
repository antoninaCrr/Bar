package it.polito.tdp.bar.model;

import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import it.polito.tdp.bar.model.Event.EventType;

public class Simulator {
	// modello
	private List<Tavolo> tavoli; // ci modella il nostro bar
	
	// parametri della simulazione ( TRATTI DAL TESTO)
	private int NUM_EVENTI = 2000; // dobbiamo generare 2000 eventi
	private int T_ARRIVO_MAX = 10; 
	private int NUM_PERSONE_MAX = 10; // al max arrivano 10 persone alla volta
	private int DURATA_MIN = 60;
	private int DURATA_MAX = 120;
    private double TOLLERANZA_MAX = 0.9;
    private double OCCUPAZIONE_MAX = 0.5;
    
    // coda degli eventi
    private PriorityQueue<Event> queue;
    
    // statistiche
    private Statistiche statistiche;
    
    public void init() {
    	this.queue = new PriorityQueue<Event>(); // creo la coda
    	this.statistiche = new Statistiche();
    	
    	creaTavoli();
    	creaEventi();
    }
    
    private void creaEventi() {
		// TODO Auto-generated method stub
    	Duration arrivo = Duration.ofMinutes(0); // il primo gruppo di clienti arriva al min 0
    	for(int i = 0; i<this.NUM_EVENTI; i++) {
    		int nPersone = (int)(Math.random() * this.NUM_PERSONE_MAX + 1); // Math.random tira fuori un num tra 0.0 e 0.999, lo moltiplichiamo per 10 e ci aggiungiamo 1 per traslare il val tra 1 e 10
    		Duration durata = Duration.ofMinutes(this.DURATA_MIN +
    				(int)(Math.random()*(this.DURATA_MAX-this.DURATA_MIN+1)));
    		double tolleranza = Math.random() + this.TOLLERANZA_MAX;
    		
    		Event e = new Event(EventType.ARRIVO_GRUPPO_CLIENTI,arrivo,nPersone,durata,tolleranza,null); // al momento il tavolo è nullo
    	    this.queue.add(e);
    	    arrivo = arrivo.plusMinutes((int)(Math.random() * this.T_ARRIVO_MAX+1)); // aggiungo a caso tra 1 e 10 min per fissare l'arrivo del prossimo gruppo
    	}
		
	}

	private void creaTavolo(int quantita, int dimensione) { // quantita pari al numero di tavolo con dimensione passata come param
    	for (int i = 0; i < quantita; i++) {
    		this.tavoli.add(new Tavolo(dimensione,false));
    	}
    	
    }

	private void creaTavoli() {
		// TODO Auto-generated method stub
		creaTavolo(2,10);
		creaTavolo(4,8);
		creaTavolo(4,6);
		creaTavolo(5,4);
		
		// Collections.sort(this.tavoli); --- rivedi questo punto
	}
	
	public void run() {
		while(!queue.isEmpty()) {
			Event e = queue.poll();
			processaEvento(e);
		}
	}

	private void processaEvento(Event e) {
		// TODO Auto-generated method stub
		switch (e.getType()) {
			case ARRIVO_GRUPPO_CLIENTI:
				// conto i clienti totali
				this.statistiche.incrementaClienti(e.getnPersone());
				
				// cerco un tavolo
				Tavolo tavolo = null;
				for(Tavolo t : this.tavoli) {
					if(!t.isOccupato() && t.getPosti()>= e.getnPersone() && t.getPosti() * this.OCCUPAZIONE_MAX <= e.getnPersone()) { // l'ultima condizione mi verifica che il tavolo sia occupato per almeno il 50% dei suoi posti
						tavolo = t; // so che se c'è prendo il tavolo più piccolo possibile in quanto li ho ordinati alla creazione
						break;
					}
				}
				if(tavolo != null) {
					System.out.format("Trovato un tavolo da %d per %d persone", tavolo.getPosti(), e.getnPersone());
					statistiche.incrementaSoddisfatti(e.getnPersone());
					tavolo.setOccupato(true);
					e.setTavolo(tavolo);
					// dopo un pò i clienti si alzeranno
					queue.add(new Event(EventType.TAVOLO_LIBERATO,e.getTime().plus(e.getDurata()), e.getnPersone(),e.getDurata(), e.getTolleranza(), e.getTavolo()));
				}else {
					// c'è solo il bancone
					double bancone = Math.random();
					if(bancone <= e.getTolleranza()) {
						// si, ci fermiamo
						System.out.format("%d persone si fermano al bancone ", e.getnPersone());
						statistiche.incrementaSoddisfatti(e.getnPersone());
					}else {
						// no, andiamo a casa
						System.out.format("%d persone vanno a casa", e.getnPersone());
						statistiche.incrementaInsoddisfatti(e.getnPersone());
					}
					
				}
				break;
			case TAVOLO_LIBERATO:
				e.getTavolo().setOccupato(false); // prendo il tavolo dei clienti appena andati e lo setto a libero
				break;
		}
	}

}
