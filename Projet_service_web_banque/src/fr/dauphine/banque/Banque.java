package fr.dauphine.banque;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

import NET.webserviceX.www.Currency;
import NET.webserviceX.www.CurrencyConvertorLocator;
import NET.webserviceX.www.CurrencyConvertorSoap;

public class Banque implements Serializable{
	
	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	private final HashMap<Long, Compte> banque = new HashMap<Long, Compte>();
	private long compteurCompte;

	public Banque(){
		super();
		initBanque();
	}

	private void initBanque() {
		banque.put(1L, new Compte("Tiganu", "Eugen", "eugen.tiganu@gmail.com", "eugen", 1, "EUR"));
		banque.put(2L, new Compte("Lestic", "Florian", "florian.lestic@gmail.com", "florian", 2, "EUR"));
	}

	public Compte getCompte(String email,String mdp){
		for (Entry<Long, Compte> compte : banque.entrySet()){
			if (compte.getValue().getEmail().equals(email)&&compte.getValue().getMdp().equals(mdp))
				return compte.getValue();
		}
		return null;
	}
	
	private boolean exist(String email){
		for (Compte compte : banque.values()){
			if (compte.getEmail().equals(email))
				return true;
		}
		return false;
	}

	/**
	 * Ajoute un compte a la base
	 */
	public boolean addCompte(String nom, String prenom, String email, String mdp, String devise) {
		if(!exist(email)){
			Compte compte = new Compte(nom, prenom, email, mdp, compteurCompte, devise);
			banque.put(compteurCompte, compte);
			compteurCompte++;
			System.out.println(compte.toString() +" vient d'etre ajoute a la base");
			return true;
		}
		System.out.println("Une personne avec le mail: " + email 
				+ " existe deja dans la base");
		return false;
	}
	
	/**
	 * Supprime le compte de la base
	 */

	public boolean delCompte(String email,String mdp){
		Compte compte = getCompte(email,mdp);
		if(compte!=null){
			banque.remove(compte.getNoCompte());
			System.out.println(compte.toString() +" vient d'etre supprime de la base");
			return true;
		} else {
			return false;
		}
	}
	/**
	 * Permet d'alimenter le compte
	 * @param email
	 * @param mdp
	 * @param montant
	 * @return true si le email, le mdp et le montant sont bien 
	 * indiques et le montant a ete rajoute au solde
	 */
	public boolean depot(String email, String mdp, double montant){
		Compte compte = getCompte(email,mdp);
		if(compte==null){
			System.out.println("Le compte pour l'email "+ email + " n'existe pas");
			return false;
		} else if(!compte.getMdp().equals(mdp)){
			System.out.println(compte.toString() + " le mot de passe renseigne n'est pas correct");
			return false;
		} else if(montant<=0){
			System.out.println(compte.toString() + " le montant indique n'est pas correct");
			return false;
		} else {
			compte.setSolde(compte.getSolde()+montant);
			System.out.println(compte.toString() + " a ete credite avec succes");
			return true;
		}
	}
	/**
	 * Permet de debiter le compte
	 * @return 0 si le compte avec l'email indiqué n'existe pas; 
	 * 2 si le mot de passe indique n'est pas correcte
	 * 3 si le montant n'a pas ete correctement reseigne
	 * 4 si la solde du compte est inferieur au montant
	 * 5 si le service de convertisseur a echoue
	 * 1 si le compte a ete debite avec succes
	 */
	public int retraitEur(String email, String mdp, double montant){
		Compte compte = getCompte(email,mdp);
		if(compte==null){
			System.out.println("Le compte pour l'email "+ email + " n'existe pas");
			return 0;
		} else if(!compte.getMdp().equals(mdp)){
			System.out.println(compte.toString() + " le mot de passe renseigne n'est pas correct");
			return 2;
		} else if(montant<=0){
			System.out.println(compte.toString() + " le montant indique n'est pas correct");
			return 3;
		} else if(compte.getDevise()!="EUR"){
			double montantDev = 0;
			try {
				CurrencyConvertorSoap dev = new CurrencyConvertorLocator().getCurrencyConvertorSoap();
				montantDev= montant*dev.conversionRate(Currency.fromString("EUR"), 
						Currency.fromString(compte.getDevise()));
				if(compte.getSolde()<montantDev){
					System.out.println(compte.toString() + " le solde du compte est inferieur au montant demande");
					return 4;
				} else {
					compte.setSolde(compte.getSolde()-montantDev);
					System.out.println(compte.toString() + " a ete debite avec succes");
					return 1;
				}		
			} catch (Exception e) {
				e.printStackTrace();
				return 5;
			}					
		} else {
			compte.setSolde(compte.getSolde()-montant);
			System.out.println(compte.toString() + " a ete debite avec succes");
			return 1;
		}
	}
	/**
	 * Renvoie le solde d'un compte
	 * @param email
	 * @param mdp
	 * @param montant
	 * @return
	 */
	public double consultSolde(String email, String mdp){
		Compte compte = getCompte(email,mdp);
		if(compte==null){
			System.out.println("Le compte pour l'email "+ email + " n'existe pas");
			return 0;
		} else if(!compte.getMdp().equals(mdp)){
			System.out.println(compte.toString() + " le mot de passe renseigne n'est pas correct");
			return 0;
		} else {			
			System.out.println(compte.toString() + " interogation du compte");
			return compte.getSolde();
		}
	}
	/**
	 * Renvoie la devise d'un compte
	 * @param email
	 * @param mdp
	 * @param montant
	 * @return
	 */
	public String consultDevise(String email, String mdp){
		Compte compte = getCompte(email,mdp);
		if(compte==null){
			System.out.println("Le compte pour l'email "+ email + " n'existe pas");
			return "";
		} else if(!compte.getMdp().equals(mdp)){
			System.out.println(compte.toString() + " le mot de passe renseigne n'est pas correct");
			return "";
		} else {			
			System.out.println(compte.toString() + " interogation du compte");
			return compte.getDevise();
		}
	}
	
	public boolean modifierCompte(String email, String mdp,String nom, String prenom,String devise){
		Compte compte = getCompte(email,mdp);
		if(compte==null){
			System.out.println("Le compte pour l'email "+ email + " n'existe pas");
			return false;
		}
		compte.setNom(nom);
		compte.setPrenom(prenom);
		compte.setDevise(devise);
		return true;
	}
}
