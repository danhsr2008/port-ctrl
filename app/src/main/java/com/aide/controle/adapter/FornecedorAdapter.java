package com.aide.controle.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.aide.controle.R;
import com.aide.controle.model.Fornecedor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FornecedorAdapter extends RecyclerView.Adapter<FornecedorAdapter.MyViewHolder> {
	private List<Fornecedor> listaFornecedores;
	private Context context;
	private OnFornecedorClickListener listener;

	public interface OnFornecedorClickListener {
		void onEditarClick(Fornecedor fornecedor, int position);

		void onFornecedorAtualizado(Fornecedor fornecedor, int position);
	}

	public FornecedorAdapter(List<Fornecedor> lista, Context c, OnFornecedorClickListener listener) {
		this.listaFornecedores = lista;
		this.context = c;
		this.listener = listener;
	}

	public static class MyViewHolder extends RecyclerView.ViewHolder {
		TextView tvNome, tvMercadoria, tvMotorista, tvPlaca, tvTelefone, tvPedido, tvConferente, tvNota;
		Button btnLiberado, btnDescarregou;
		LinearLayout cardHeader;

		public MyViewHolder(@NonNull View itemView) {
			super(itemView);
			tvNome = itemView.findViewById(R.id.tvNome);
			tvMercadoria = itemView.findViewById(R.id.tvMercadoria);
			tvMotorista = itemView.findViewById(R.id.tvMotorista);
			tvPlaca = itemView.findViewById(R.id.tvPlaca);
			tvTelefone = itemView.findViewById(R.id.tvTelefone);
			tvPedido = itemView.findViewById(R.id.tvPedido);
			tvConferente = itemView.findViewById(R.id.tvConferente);
			tvNota = itemView.findViewById(R.id.tvNota);
			btnLiberado = itemView.findViewById(R.id.btnLiberado);
			btnDescarregou = itemView.findViewById(R.id.btnDescarregou);
			cardHeader = itemView.findViewById(R.id.card_header);
		}
	}

	@NonNull
	@Override
	public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fornecedor, parent, false);
		return new MyViewHolder(itemLista);
	}

	@Override
	public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
		Fornecedor fornecedor = listaFornecedores.get(position);

		holder.tvNome.setText(fornecedor.getNome());
		holder.tvMercadoria.setText(fornecedor.getMercadoria());
		holder.tvMotorista
				.setText(fornecedor.getMotorista().isEmpty() ? "" : "Motorista: " + fornecedor.getMotorista());
		holder.tvPlaca.setText(fornecedor.getPlaca().isEmpty() ? "" : "Placa: " + formatarPlaca(fornecedor.getPlaca()));
		holder.tvTelefone.setText(
				fornecedor.getTelefone().isEmpty() ? "" : "Tel: " + formatarTelefone(fornecedor.getTelefone()));
		holder.tvPedido.setText(fornecedor.getPedido().isEmpty() ? "" : "N° PEDIDO: " + fornecedor.getPedido());
		holder.tvNota.setText(fornecedor.getNumeroNota().isEmpty() ? "" : "N° NOTA: " + fornecedor.getNumeroNota());
		holder.tvConferente.setText(fornecedor.getConferente().isEmpty() ? "" : fornecedor.getConferente());

		// Configuração de visibilidade
		holder.tvMotorista.setVisibility(fornecedor.getMotorista().isEmpty() ? View.GONE : View.VISIBLE);
		holder.tvPlaca.setVisibility(fornecedor.getPlaca().isEmpty() ? View.GONE : View.VISIBLE);
		holder.tvTelefone.setVisibility(fornecedor.getTelefone().isEmpty() ? View.GONE : View.VISIBLE);
		holder.tvPedido.setVisibility(fornecedor.getPedido().isEmpty() ? View.GONE : View.VISIBLE);
		holder.tvNota.setVisibility(fornecedor.getNumeroNota().isEmpty() ? View.GONE : View.VISIBLE);
		holder.tvConferente.setVisibility(fornecedor.getConferente().isEmpty() ? View.GONE : View.VISIBLE);

		// Configuração de cores
		int corCard;
		switch (fornecedor.getStatus()) {
		case "subiu":
			corCard = ContextCompat.getColor(context, R.color.status_subiu);
			break;
		case "foi_embora":
			corCard = ContextCompat.getColor(context, R.color.status_foi_embora);
			break;
		default:
			corCard = fornecedor.getPedido().trim().isEmpty()
					? ContextCompat.getColor(context, R.color.status_nao_agendado)
					: ContextCompat.getColor(context, R.color.status_aguardando);
		}
		holder.cardHeader.setBackgroundColor(corCard);

		// Configuração dos botões
		if (fornecedor.getStatus().equals("subiu")) {
			holder.btnLiberado.setVisibility(View.GONE);
			holder.btnDescarregou.setVisibility(View.VISIBLE);
		} else if (fornecedor.getStatus().equals("foi_embora")) {
			holder.btnLiberado.setVisibility(View.GONE);
			holder.btnDescarregou.setVisibility(View.GONE);
		} else {
			holder.btnLiberado.setVisibility(View.VISIBLE);
			holder.btnDescarregou.setVisibility(View.GONE);
		}

		holder.btnLiberado.setOnClickListener(v -> mostrarDialogoConferente(fornecedor, position));
		holder.btnDescarregou.setOnClickListener(v -> registrarSaida(fornecedor, position));

		// Menu de contexto
		holder.itemView.setOnLongClickListener(v -> {
			showMenuOptions(v, fornecedor, position);
			return true;
		});
	}

	private void registrarSaida(Fornecedor fornecedor, int position) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault());
		String dataHora = sdf.format(new Date());

		fornecedor.setHoraSaida(dataHora);
		fornecedor.setStatus("foi_embora");

		if (listener != null) {
			listener.onFornecedorAtualizado(fornecedor, position);
		}
		notifyItemChanged(position);
		mostrarInformacoesCompletas(fornecedor);
	}

	private void mostrarDialogoConferente(Fornecedor fornecedor, int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Registrar Subida");

		View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_conferente, null);
		EditText etConferente = dialogView.findViewById(R.id.etConferente);
		builder.setView(dialogView);

		builder.setPositiveButton("Confirmar", (dialog, which) -> {
			String nomeConferente = etConferente.getText().toString().trim();
			if (!nomeConferente.isEmpty()) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault());
				String dataHora = sdf.format(new Date());

				fornecedor.setConferente(context.getString(R.string.liberado_por, nomeConferente));
				fornecedor.setHoraSubida(dataHora);
				fornecedor.setStatus("subiu");

				if (listener != null) {
					listener.onFornecedorAtualizado(fornecedor, position);
				}
				notifyItemChanged(position);
				mostrarInformacoesCompletas(fornecedor);
			}
		});

		builder.setNegativeButton("Cancelar", null);
		builder.show();
	}

	private String formatarTelefone(String telefone) {
		if (TextUtils.isEmpty(telefone))
			return "";

		String numeros = telefone.replaceAll("[^0-9]", "");
		if (numeros.length() == 11) {
			return String.format(Locale.getDefault(), "(%s) %s.%s-%s", numeros.substring(0, 2), numeros.substring(2, 3),
					numeros.substring(3, 7), numeros.substring(7));
		} else if (numeros.length() == 10) {
			return String.format(Locale.getDefault(), "(%s) %s-%s", numeros.substring(0, 2), numeros.substring(2, 6),
					numeros.substring(6));
		}
		return telefone;
	}

	private void mostrarInformacoesCompletas(Fornecedor fornecedor) {
		StringBuilder mensagem = new StringBuilder();
		mensagem.append("Fornecedor: ").append(fornecedor.getNome()).append("\n\n");
		mensagem.append("Motorista: ").append(fornecedor.getMotorista()).append("\n\n");
		mensagem.append("Placa: ").append(formatarPlaca(fornecedor.getPlaca())).append("\n\n");
		mensagem.append("Mercadoria: ").append(fornecedor.getMercadoria()).append("\n\n");
		mensagem.append("Telefone: ").append(formatarTelefone(fornecedor.getTelefone())).append("\n\n");
		mensagem.append("N° PEDIDO: ").append(fornecedor.getPedido()).append("\n\n");
		mensagem.append("N° NOTA: ").append(fornecedor.getNumeroNota()).append("\n\n");
		mensagem.append("Chegada: ").append(fornecedor.getDataChegada()).append("\n\n");
		mensagem.append("Subida: ")
				.append(fornecedor.getHoraSubida().isEmpty() ? "Não registrado" : fornecedor.getHoraSubida())
				.append("\n\n");
		mensagem.append("Saída: ")
				.append(fornecedor.getHoraSaida().isEmpty() ? "Não registrado" : fornecedor.getHoraSaida())
				.append("\n\n");

		if (!fornecedor.getConferente().isEmpty()) {
			mensagem.append(fornecedor.getConferente()).append("\n\n");
		}

		new AlertDialog.Builder(context).setTitle("Informações Completas").setMessage(mensagem.toString())
				.setPositiveButton("OK", null).show();
	}

	private String formatarPlaca(String placa) {
		return (placa.length() == 7) ? placa.substring(0, 3) + "-" + placa.substring(3) : placa;
	}

	private void showMenuOptions(View view, Fornecedor fornecedor, int position) {
		PopupMenu popup = new PopupMenu(context, view);
		popup.inflate(R.menu.fornecedor_menu);

		popup.setOnMenuItemClickListener(item -> {
			if (item.getItemId() == R.id.menu_info) {
				mostrarInformacoesCompletas(fornecedor);
				return true;
			} else if (item.getItemId() == R.id.menu_editar) {
				if (listener != null) {
					listener.onEditarClick(fornecedor, position);
				}
				return true;
			}
			return false;
		});
		popup.show();
	}

	@Override
	public int getItemCount() {
		return listaFornecedores.size();
	}
}
