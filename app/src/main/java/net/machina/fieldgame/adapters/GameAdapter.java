package net.machina.fieldgame.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.machina.fieldgame.R;
import net.machina.fieldgame.data.Game;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    class GameViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtGameTitle, txtGameDescription, txtGameRiddleCount;

        GameViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardGame);
            txtGameTitle = itemView.findViewById(R.id.txtGameTitle);
            txtGameDescription = itemView.findViewById(R.id.txtGameDescription);
            txtGameRiddleCount = itemView.findViewById(R.id.txtGameRiddleCount);
        }
    }

    private List<Game> gameList;
    private GameSelectedListener listener;
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setListener(GameSelectedListener listener) {
        this.listener = listener;
    }

    public GameAdapter(List<Game> gameList) {
        this.gameList = gameList;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_game, parent, false);
        return new GameViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        holder.txtGameTitle.setText(gameList.get(position).getGameTitle());
        holder.txtGameDescription.setText(gameList.get(position).getGameDescription());
        holder.txtGameRiddleCount.setText(
                context.getText(R.string.label_riddle_count) +
                String.valueOf(gameList.get(position).getGameRiddleCount()));
        holder.cardView.setOnClickListener((view) -> {
            listener.onGameSelected(gameList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
