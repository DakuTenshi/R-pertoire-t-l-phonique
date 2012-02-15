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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		return result;
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
	
	public boolean isCorrect()
	{
		return (this.nom != null && this.nom.compareTo("") != 0 && this.numero != null && this.numero.compareTo("") != 0);
	}
}
