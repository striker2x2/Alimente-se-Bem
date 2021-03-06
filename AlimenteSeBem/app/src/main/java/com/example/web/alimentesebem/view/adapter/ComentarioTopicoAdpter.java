package com.example.web.alimentesebem.view.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.web.alimentesebem.R;
import com.example.web.alimentesebem.model.ComentarioForumBean;
import com.example.web.alimentesebem.model.UsuarioBean;
import com.example.web.alimentesebem.rest.config.RetrofitConfig;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by WEB on 16/03/2018.
 */

public class ComentarioTopicoAdpter extends RecyclerView.Adapter {
    private Context context;
    private final List<ComentarioForumBean> comentarios;
    private ArrayList<ComentarioForumBean> lista;

    public ComentarioTopicoAdpter(List<ComentarioForumBean> comentarios, Context context) {
        this.context = context;
        this.comentarios = comentarios;
        this.lista = new ArrayList<>();
        this.lista.addAll(comentarios);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.detalhe_comentario, parent, false);

        return new ComentarioTopicoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ComentarioForumBean comentarioForumBean = comentarios.get(position);

        try {
            ((ComentarioTopicoViewHolder) holder).preencher(comentarioForumBean);
        } catch (Exception e) {
            Toast.makeText(context, context.getResources().getString(R.string.falha_de_acesso), Toast.LENGTH_LONG).show();
        }

    }

    public void ordena(){


        Collections.sort(comentarios, new Comparator<ComentarioForumBean> () {
            @Override
            public int compare(ComentarioForumBean obj1, ComentarioForumBean obj2) {
                return obj1.getData_criacao().compareTo(obj2.getData_criacao());
            }
        });
        Collections.reverse(comentarios);

    }


    @Override
    public int getItemCount() {
        return comentarios.size();
    }

    public class ComentarioTopicoViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvAutor, tvData, tvComentario;
        private long comentarioId;
        private DateFormat dtFmt = DateFormat.getDateInstance(DateFormat.LONG);
        private UsuarioBean usuario;
        private SharedPreferences preferencesGet;

        public ComentarioTopicoViewHolder(View itemView) {

            super(itemView);

            tvAutor = itemView.findViewById(R.id.tv_autor_comentario);
            tvComentario = itemView.findViewById(R.id.tv_comentario);
            tvData = itemView.findViewById(R.id.tv_data_comentario);

            Typeface typeFont = Typeface.createFromAsset(context.getAssets(), "fonts/Gotham_Condensed_Bold.otf");
            tvAutor.setTypeface(typeFont);
            typeFont = Typeface.createFromAsset(context.getAssets(), "fonts/Gotham_Light.otf");
            tvData.setTypeface(typeFont);
            tvComentario.setTypeface(typeFont);
        }


        public void preencher(ComentarioForumBean comentario) throws Exception {
            comentarioId = comentario.getId_Comentario();

            getUsuario(comentario.getId_user());
            tvComentario.setText(comentario.getDescricao());
            tvData.setText(dtFmt.format(comentario.getData_criacao()));

        }


        private void getUsuario(final long idUsuario) {
            Call<UsuarioBean> call = new RetrofitConfig().getRestInterface().getUsuario(idUsuario);
            call.enqueue(new Callback<UsuarioBean>() {
                @Override
                public void onResponse(Call<UsuarioBean> call, Response<UsuarioBean> response) {

                    if (response.isSuccessful()) {

                        usuario = response.body();
                        if (usuario != null){
                                tvAutor.setText(usuario.getNome());
                        }

                    }
                }

                @Override
                public void onFailure(Call<UsuarioBean> call, Throwable t) {
                    Log.d("ComentarioAdpter", t.getMessage());
                }

            });
        }
    }
}
