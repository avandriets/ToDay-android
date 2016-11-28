package com.gcgamecore.today.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.gcgamecore.today.Data.DB_ThemeQuiz;
import com.gcgamecore.today.Data.DatabaseHelper;
import com.gcgamecore.today.R;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class ArchiveRecyclerViewAdapter extends OrmliteCursorRecyclerViewAdapter<DB_ThemeQuiz, ArchiveRecyclerViewAdapter.ArchiveViewHolder>{

    private final SimpleDateFormat dateFormatter;
    DatabaseHelper mDatabaseHelper = null;

    final private ArchiveAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;

    public static interface ArchiveAdapterOnClickHandler {
        void onClick(Long pId, ArchiveViewHolder vh);
    }

    public class ArchiveViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView tvHeader;
        public TextView tvDate;

        public ArchiveViewHolder(View itemView) {
            super(itemView);

            tvHeader    = (TextView) itemView.findViewById(R.id.themeHeader);
            tvDate      = (TextView) itemView.findViewById(R.id.themeDate);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

            getCursor().moveToPosition(adapterPosition);
            int dateColumnIndex = getCursor().getColumnIndex(DB_ThemeQuiz.ID);
            mClickHandler.onClick(getCursor().getLong(dateColumnIndex), this);
        }
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        Cursor cur = super.swapCursor(newCursor);
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        return cur;
    }

    public ArchiveRecyclerViewAdapter(Context context, ArchiveAdapterOnClickHandler dh, View emptyView, DatabaseHelper mDatabaseHelper) {
        super(context);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

        this.mDatabaseHelper = mDatabaseHelper;

        mClickHandler   = dh;
        mEmptyView      = emptyView;
    }

    @Override
    public ArchiveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ArchiveViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View vElementItem = inflater.inflate(R.layout.archive_list_item, parent, false);
        viewHolder = new ArchiveViewHolder(vElementItem);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ArchiveViewHolder holder, DB_ThemeQuiz theme_quiz) {
        holder.tvDate.setText( dateFormatter.format(theme_quiz.getTarget_date()) );
        holder.tvHeader.setText(theme_quiz.getName());
    }
}
