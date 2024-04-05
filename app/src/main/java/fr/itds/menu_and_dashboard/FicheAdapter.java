package fr.itds.menu_and_dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FicheAdapter extends RecyclerView.Adapter<FicheAdapter.ViewHolder> {
    private List<Fiche> fichesList;

    // Constructeur
    public FicheAdapter(List<Fiche> fichesList) {
        this.fichesList = fichesList;
    }

    // Crée de nouvelles vues
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fiche, parent, false);
        return new ViewHolder(view);
    }

    // Remplace le contenu d'une vue
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Fiche fiche = fichesList.get(position);
        holder.ficheTitle.setText(fiche.getTitle());
        holder.ficheDescription.setText(fiche.getDescription());
        // Gérez les clics sur les boutons Modifier et Supprimer ici
    }

    // Retourne la taille de la liste de données
    @Override
    public int getItemCount() {
        return fichesList.size();
    }

    // Fournit une référence aux vues pour chaque élément de données
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView ficheTitle, ficheDescription;
        // Déclarez les boutons Modifier et Supprimer ici

        public ViewHolder(View view) {
            super(view);
            ficheTitle = view.findViewById(R.id.ficheTitle);
            ficheDescription = view.findViewById(R.id.ficheDescription);
            // Initialisez les boutons Modifier et Supprimer ici
        }
    }
}

