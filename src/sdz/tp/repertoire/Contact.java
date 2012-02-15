package sdz.tp.repertoire;

public class Contact {
	private String nom = null;
	private String numero = null;
	private boolean homme = true;
	private boolean selected = false;
	
	public String getNom() {
		return nom;
	}
	
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public String getNumero() {
		return numero;
	}
	
	public void setNumero(String numéro) {
		this.numero = numéro;
	}
	
	public boolean isHomme() {
		return homme;
	}
	
	public void setHomme(boolean homme) {
		this.homme = homme;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public void switchSelected() {
		this.selected = !this.selected;
	}
	
	/**
	 * Est-ce que le contact répond aux critères d'intégrité ?
	 * @return true si le contact est valide
	 */
	public boolean isCorrect()
	{
		return (this.nom != null && this.nom.compareTo("") != 0 && this.numero != null && this.numero.compareTo("") != 0);
	}
	
	/**
	 * Fonction de hashage, génère un code unique pour chaque contact
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (homme ? 1231 : 1237);
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		return result;
	}

	/**
	 * Permet de savoir si deux contacts sont égaux
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Contact))
			return false;
		Contact other = (Contact) obj;
		if (homme != other.homme)
			return false;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		if (numero == null) {
			if (other.numero != null)
				return false;
		} else if (!numero.equals(other.numero))
			return false;
		return true;
	}
}
