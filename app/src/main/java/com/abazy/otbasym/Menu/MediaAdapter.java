package com.abazy.otbasym.Menu;

import static com.abazy.otbasym.Global.gc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.folg.gedcom.model.Media;
import org.folg.gedcom.model.Person;

import java.util.List;

import com.abazy.otbasym.DetailActivity;
import com.abazy.otbasym.F;
import com.abazy.otbasym.Global;
import com.abazy.otbasym.Memory;
import com.abazy.otbasym.Principal;
import com.abazy.otbasym.ProfileActivity;
import com.abazy.otbasym.R;
import com.abazy.otbasym.U;
import com.abazy.otbasym.Constants.Choice;
import com.abazy.otbasym.Details.MediaActivity;
import com.abazy.otbasym.Visitors.FindStack;
import com.abazy.otbasym.Visitors.MediaContainerList;
import com.abazy.otbasym.Visitors.MediaReferences;

/**
 * Adapter for the RecyclerView of media gallery {@link MediaFragment}.
 */
public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.managerViewMedia> {

    private final List<MediaContainerList.MedCont> medConts;
    private final boolean details;

    public MediaAdapter(List<MediaContainerList.MedCont> medConts, boolean details) {
        this.medConts = medConts;
        this.details = details;
    }

    @NonNull
    @Override
    public managerViewMedia onCreateViewHolder(ViewGroup parent, int type) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.media, parent, false);
        return new managerViewMedia(vista, details);
    }

    @Override
    public void onBindViewHolder(final managerViewMedia gestore, int posizione) {
        gestore.set(posizione);
    }

    @Override
    public int getItemCount() {
        return medConts.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class managerViewMedia extends RecyclerView.ViewHolder implements View.OnClickListener {
        View view;
        boolean details;
        Media media;
        Object container;
        ImageView imageView;
        TextView textView;
        TextView viewNumber;

        managerViewMedia(View vista, boolean details) {
            super(vista);
            this.view = vista;
            this.details = details;
            imageView = vista.findViewById(R.id.media_img);
            textView = vista.findViewById(R.id.media_testo);
            viewNumber = vista.findViewById(R.id.media_num);
        }

        void set(int position) {
            media = medConts.get(position).media;
            container = medConts.get(position).container;
            if (details) {
                arrangeMedia(media, textView, viewNumber);
                view.setOnClickListener(this);
                ((Activity) view.getContext()).registerForContextMenu(view);
                view.setTag(R.id.tag_object, media);
                view.setTag(R.id.tag_contenitore, container);
                final AppCompatActivity active = (AppCompatActivity) view.getContext();
                if (view.getContext() instanceof ProfileActivity) { // ProfileMediaFragment
                    active.getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.profile_pager + ":0") // non garantito in futuro
                            .registerForContextMenu(view);
                } else if (view.getContext() instanceof Principal) // MediaFragment
                    active.getSupportFragmentManager().findFragmentById(R.id.contenitore_fragment).registerForContextMenu(view);
                else // in AppCompatActivity
                    active.registerForContextMenu(view);
            } else {

                RecyclerView.LayoutParams parami = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, U.dpToPx(110));
                int margin = U.dpToPx(5);
                parami.setMargins(margin, margin, margin, margin);
                view.setLayoutParams(parami);
                textView.setVisibility(View.GONE);
                viewNumber.setVisibility(View.GONE);
            }
            F.showImage(media, imageView, view.findViewById(R.id.media_circolo));
        }

        @Override
        public void onClick(View v) {
            AppCompatActivity activity = (AppCompatActivity)v.getContext();


            if (activity.getIntent().getBooleanExtra(Choice.MEDIA, false)) {
                Intent intent = new Intent();
                intent.putExtra("mediaId", media.getId());
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();

            } else {
                Intent intent = new Intent(v.getContext(), MediaActivity.class);
                if (media.getId() != null) {
                    Memory.setFirst(media);
                } else if ((activity instanceof ProfileActivity && container instanceof Person) // media di primo livello nell'Indi
                        || activity instanceof DetailActivity) {
                    Memory.add(media);
                } else { new FindStack(Global.gc, media);
                    if (activity instanceof Principal)
                        intent.putExtra("daSolo", true);    }
                v.getContext().startActivity(intent);
            }
        }
    }

    public static void arrangeMedia(Media media, TextView textView, TextView textView1) {
        String text = "";
        if (media.getTitle() != null)
            text = media.getTitle() + "\n";
        if (Global.settings.expert && media.getFile() != null) {
            String file = media.getFile();
            file = file.replace('\\', '/');
            if (file.lastIndexOf('/') > -1) {
                if (file.length() > 1 && file.endsWith("/"))
                    file = file.substring(0, file.length() - 1);
                file = file.substring(file.lastIndexOf('/') + 1);
            }
            text += file;
        }
        if (text.isEmpty())
            textView.setVisibility(View.GONE);
        else {
            if (text.endsWith("\n"))
                text = text.substring(0, text.length() - 1);
            textView.setText(text);
        }
        if (media.getId() != null) {
            MediaReferences mediaReferences = new MediaReferences(gc, media, false);
            textView1.setText(String.valueOf(mediaReferences.num));
            textView1.setVisibility(View.VISIBLE);
        } else
            textView1.setVisibility(View.GONE);
    }

    public static class RecycleView extends RecyclerView {
        boolean details;

        public RecycleView(Context context, boolean details) {
            super(context);
            this.details = details;
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            super.onTouchEvent(e);
            return details; 
        }
    }
}
