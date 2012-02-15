package sdz.tp.repertoire;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;

import android.content.Context;
import android.content.DialogInterface;

import android.graphics.Color;

import android.os.Bundle;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class RepertoireActivity extends ListActivity {
	private RepertoireAdapter mAdapter = null;
	private ListView liste = null;
	
	private final static int DIALOG_ADD = 0;
	private final static int DIALOG_MULTIPLE_DELETE = 1;
	private final static int DIALOG_MODIFY = 2;
	
	private Contact currentContact = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mAdapter = new RepertoireAdapter();
        
        liste = getListView();
        liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int position,
					long id) {
				Contact c = (Contact)mAdapter.getItem(position);
				//On passe de "sélectionné" à "désélectionné" ou l'inverse
				c.switchSelected();
				//On indique qu'on a changé les informations
				mAdapter.notifyDataSetChanged();
			}
		});
        //Ajouter un menu contextuel à chaque item de la liste
        registerForContextMenu(liste);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.options, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onPrepareOptionsMenu (Menu menu)
    {
    	//S'il y a des éléments sélectionnés, on active la suppression multiple
    	if(mAdapter.getListeSelected().size() > 0)
    		menu.findItem(R.menuItem.multipleDelete).setEnabled(true);
    	else
    		menu.findItem(R.menuItem.multipleDelete).setEnabled(false);
		return true;
    	
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch(item.getItemId())
    	{
    	case R.menuItem.add:
    		showDialog(DIALOG_ADD);
    		return true;
    	case R.menuItem.multipleDelete:
    		showDialog(DIALOG_MULTIPLE_DELETE);
    		return true;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    public Dialog onCreateDialog(int id)
    {
    	AlertDialog retour = null;
		AlertDialog.Builder builder = null;
		LayoutInflater inflater = getLayoutInflater();
    	switch(id)
    	{
    	case DIALOG_ADD:
    		final RelativeLayout layoutAdd = (RelativeLayout) inflater.inflate(R.layout.addmodifydialog, null);
    		builder = new AlertDialog.Builder(this)
    			.setView(layoutAdd)
    			.setPositiveButton("Ajouter", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Contact c = new Contact();
						//Récupérer les champs
						EditText nom = (EditText)layoutAdd.findViewById(R.edit.name);
						EditText phone = (EditText)layoutAdd.findViewById(R.edit.phone);
						ToggleButton gender = (ToggleButton)layoutAdd.findViewById(R.bouton.gender);
						
						//On met les informations dans le nouveau contact
						c.setNom(nom.getText().toString().trim());
						c.setNumero(phone.getText().toString().trim());
						c.setHomme(gender.isChecked());
						//Si on avait pas d'adaptateur, on le crée
						if(mAdapter == null)
							mAdapter = new RepertoireAdapter();
						//On ajoute l'item et si la liste n'avait pas encore d'adaptateur, on lui attribut
						if(mAdapter.addItem(c) && getListAdapter() == null)
								setListAdapter(mAdapter);
					}
				})
				.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//Fermer la boîte de dialogue
						dismissDialog(DIALOG_ADD);
					}
				})
				.setTitle("Ajouter un nouvel utilisateur");
    		break;
    	case DIALOG_MULTIPLE_DELETE:
    		builder = new AlertDialog.Builder(this)
    			.setTitle("Supprimer ces utilisateurs ?")
    			.setMessage("Êtes-vous certain de vouloir supprimer ces utilisateurs ?")
    			.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//Récupération des contacts sélectionnés
						ArrayList<Integer> selected = mAdapter.getListeSelected();
						int position;
						//On parcourt en sens inverse la liste...
						for(int i = selected.size() ; i > 0 ; i--)
						{
							//position du ième contact sélectionné
							position = selected.get(i - 1);
							//et on supprime le contact à cette position
							mAdapter.deleteItem(position);
						}
					}
				});
    		break;
    	case DIALOG_MODIFY:
    		final RelativeLayout layoutModifiy = (RelativeLayout) inflater.inflate(R.layout.addmodifydialog, null);
    		builder = new AlertDialog.Builder(this)
				.setView(layoutModifiy)
				.setPositiveButton("Modifier", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Contact c = new Contact();
						//Récupération des informations insérées dans les champs
						EditText nom = (EditText)layoutModifiy.findViewById(R.edit.name);
						EditText phone = (EditText)layoutModifiy.findViewById(R.edit.phone);
						ToggleButton gender = (ToggleButton)layoutModifiy.findViewById(R.bouton.gender);
						
						//On le insère dans le contact
						c.setNom(nom.getText().toString().trim());
						c.setNumero(phone.getText().toString().trim());
						c.setHomme(gender.isChecked());
						//Et remplacement du contact déjà existant
						mAdapter.modifyItem(c, currentContact.hashCode());
					}
				})
				.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//Fermer la boîte de dialogue
						dismissDialog(DIALOG_MODIFY);
					}
				})
				.setTitle("Modifier un utilisateur");
    		break;
    			
    	}
		retour = builder.create();
		return retour;
    	
    }
    
    @Override
    public void onPrepareDialog (int id, Dialog dialog)
    {
    	switch(id)
    	{
    	case DIALOG_ADD:
			EditText nom = (EditText)dialog.findViewById(R.edit.name);
			EditText phone = (EditText)dialog.findViewById(R.edit.phone);
			ToggleButton gender = (ToggleButton)dialog.findViewById(R.bouton.gender);
			
			//Remise à zéro des champs de la boîte de dialogue
			nom.setText("");
			//On accorde le focus au premier champ
			nom.requestFocus();
			phone.setText("");
			gender.setChecked(false);
			break;
    	case DIALOG_MODIFY:
    		EditText nomMod = (EditText)dialog.findViewById(R.edit.name);
			EditText phoneMod = (EditText)dialog.findViewById(R.edit.phone);
			ToggleButton genderMod = (ToggleButton)dialog.findViewById(R.bouton.gender);
			
			//Les champs de la boîte sont complétés avec les informations sur le contadt
			nomMod.setText(currentContact.getNom());
			//On accorde le focus au premier champ
			nomMod.requestFocus();
			phoneMod.setText(currentContact.getNumero());
			genderMod.setChecked(currentContact.isHomme());
			break;
    	}
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	//Inflation du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextuel, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	//On récupère des informations supplémentaires sur l'item
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //On récupère l'identifiant de l'item concerné
        long id = mAdapter.getItemId(info.position);
        switch(item.getItemId())
        {
        //Si on veut modifier un élément
        case R.menuItem.edit :
        	//on récupère le contact à modifier
        	currentContact = mAdapter.getItem(id);
        	//et on envoie la boîte de dialogue appropriée
        	showDialog(DIALOG_MODIFY);
        	//On a bien traité la sélection
        	return true;
        //
        case R.menuItem.delete :
        	//suppression de l'item
        	mAdapter.deleteItemId(id);
        	//On a bien traité la sélection
        	return true;
        }
        //C'est à la super classe de traiter l'item
        return super.onContextItemSelected(item);
    }
    
    /**
     * Adaptateur de notre répertoire.
     *
     */
    private class RepertoireAdapter extends BaseAdapter {
    	/**
    	 * La liste des contacts de notre répertoire
    	 */
    	private ArrayList<Contact> liste = new ArrayList<Contact>();
    	/**
    	 * Le LayoutInflater de notre activité pour récupèrer certaines vues
    	 */
    	private LayoutInflater mInflater = null;
    	
    	public RepertoireAdapter() {
    		//On récupère le LayoutInflater de note activité
    		mInflater = getLayoutInflater();
    	}

    	/**
    	 * Modifier un contact en fonction de son identifiant
    	 * @param c les nouvelles informations sur le contact
    	 * @param id l'identifiant du contact à modifier
    	 */
    	public void modifyItem(Contact c, long id) {
    		Contact tmp = getItem(id);
    		if(tmp != null)
    		{
        		tmp.setHomme(c.isHomme());
        		tmp.setNom(c.getNom());
        		tmp.setNumero(c.getNumero());
    			notifyDataSetChanged();
    		}
		}

		/**
    	 * Le nombre de contacts dans la liste
    	 * @return -1 si la liste est vide, le nombre de contacts sinon
    	 */
    	@Override
    	public int getCount() {
    		if(liste != null)
    			return liste.size();
    		return -1;
    	}

    	/**
    	 * Recupère un contact en fonction de son identifiant
    	 * @param id identifiant du contact recherché
    	 * @return le contact s'il est trouvé, sinon null
    	 */
    	public Contact getItem(long id) {
    		if(liste != null )
    			for(Contact c : liste)
    				if(c.hashCode() == id)
    					return c;
    		return null;
    	}
    	
    	/**
    	 * Recupère un contact en fonction de sa position
    	 * @param r la position du contact dans la liste
    	 * @return le contact s'il est trouvé, sinon null
    	 */
    	@Override
    	public Object getItem(int r) {
    		//Si la liste exist et qu'on se trouve dans la liste, on renvoit le contact
    		if(liste != null && r >= 0 && r < liste.size())
    			return liste.get(r);
    		return null;
    	}

    	/**
    	 * Récupère l'identifiant d'un élément en fonction de sa position
    	 * @param position rang de l'élément dans la liste
    	 * @return l'identifiant de l'élément ou -1 s'il nest pas trouvé
    	 */
    	@Override
    	public long getItemId(int position) {
    		//On essaie de récupère l'élément du rang précisé
    		Contact e = (Contact) getItem(position);
    		//S'il n'y a pas de contact de ce rang là, on renvoit -1
    		if(e == null)
    			return -1;
    		//Sinon on renvoit son identifiant
    		return e.hashCode();
    	}
    	
    	/**
    	 * Rajouter un contact
    	 * @param c le contact à rajouter
    	 * @return true si l'élément a été rajouté
    	 */
    	public boolean addItem(Contact c)
    	{
    		//On cherche dans tous les contacts de la liste si le contact n'existe pas déjà
    		for(Contact tmp : liste)
    			//si l'identifiant du nouveau contact est déjà dans la liste
    			if(tmp.hashCode() == c.hashCode())
    			{
    				Toast.makeText(RepertoireActivity.this, "Ce contact existe déjà !", Toast.LENGTH_SHORT).show();
    				return false;
    			}
    		//Si le contact n'est pas déjà dans la liste
    		liste.add(c);
			//On indique que l'ensemble de données a été modifié
    		notifyDataSetChanged();
    		return true;
    	}
    	
    	/**
    	 * Supprimer l'élément en précisant son identifiant
    	 * @param id identifiant de l'élément à supprimer
    	 */
    	public void deleteItemId(long id)
    	{
    		//On cherche dans tous les contacts de la liste
    		for(Contact c : liste)
    		{
    			//Si le contact en cours a l'identifiant précisé
    			if(c.hashCode() == id)
    			{
    				//On supprime l'élément
    				liste.remove(c);
    				//On indique que l'ensemble de données a été modifié
    				notifyDataSetChanged();
    				break;
    			}
    		}
    	}

    	/**
    	 * Supprimer l'élément du rang défini
    	 * @param position le rang de l'élément à supprimer
    	 */
    	public void deleteItem(int position)
    	{
    		//Si la liste n'est pas vide et qu'on se trouve bien dans la liste
    		if(liste != null && position < liste.size())
    		{
				//On supprime l'élément
    			liste.remove(position);
				//On indique que l'ensemble de données a été modifié
				notifyDataSetChanged();
    		}
    	}
    	
    	/**
    	 * Récupérer la liste des positions des éléments sélectionnés
    	 * @return liste de la position des éléments sélectionnés
    	 */
    	public ArrayList<Integer> getListeSelected()
    	{
    		//Liste de la position des éléments sélectionnés
    		ArrayList<Integer> retour = new ArrayList<Integer>();
    		int i = 0;
    		if(liste != null)
    		{
    			//Pour chaque contact de la liste
    			for(Contact c : liste)
    			{
    				//Si le contact est sélectionné
    				if(c.isSelected())
    					//On ajoute la position à la liste des positions
    					retour.add(i);
        			i++;
    			}
    		}
    		return retour;
    	}
    	
    	/**
    	 * Créer la vue demandée.
    	 * @param r rang de la vue dans l'adaptateur.
    	 * @param convertView recyclage de la vue.
    	 * @param parent le layout dans lequel se trouve le convertView.
    	 */
    	@Override
    	public View getView(int r, View convertView, ViewGroup parent) {
    		ViewHolder holder = null;
    		//Si la vue n'est pas recyclée
    		if(convertView == null)
    		{
    			//On récupère le layout
    			convertView  = mInflater.inflate(R.layout.rangee, null);
    			
    			holder = new ViewHolder();
    			//On place les widgets de notre layout dans le holder
    			holder.nom = (TextView) convertView.findViewById(R.text.nom);
    			holder.numero = (TextView) convertView.findViewById(R.text.numero);
    			holder.photo = (ImageView) convertView.findViewById(R.image.photo);
    			
    			//puis on insère le holder en tant que tag dans le layout
    			convertView.setTag(holder);
    		}else
    		{
    			//Si on recycle la vue, on récupère son holder en tag
    			holder = (ViewHolder)convertView.getTag();
    		}
    		//Dans tous les cas, on récupère l'élément concerné
    		Contact c = (Contact)getItem(r);
    		//Si cet élément existe vraiment...
    		if(c != null)
    		{
    			//On place dans le holder les informations sur le contact
    			holder.nom.setText(c.getNom());
    			holder.numero.setText(c.getNumero());
    			if(c.isHomme())
    				holder.photo.setImageResource(R.drawable.dakutenshi);
    			else
    				holder.photo.setImageResource(R.drawable.dakutenshette);
    			//S'il est sélectionné, on change sa couleur de fond
    			if(c.isSelected())
    				convertView.setBackgroundColor(Color.BLUE);
    			else
    				convertView.setBackgroundColor(Color.BLACK);
    		}
    		return convertView;
    	}

    }
	
    /**
     * Classe nécessaire pour le pattern Holder
     */
	public static class ViewHolder {
        public TextView nom;
        public TextView numero;
        public ImageView photo;
    }
}