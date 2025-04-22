package com.aide.controle.adapter;

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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
	private SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault());

	public interface OnFornecedorClickListener {
		void onEditarClick(Fornecedor fornecedor, int position);

		void onFornecedorAtualizado(Fornecedor fornecedor, int position);

		void onApagarClick(Fornecedor fornecedor, int position);

		void onAdicionarInfoClick(Fornecedor fornecedor, int position);

		void onMostrarInfoClick(Fornecedor fornecedor);
	}

	public FornecedorAdapter(List<Fornecedor> lista, Context c, OnFornecedorClickListener listener) {
		this.listaFornecedores = lista;
		this.context = c;
		this.listener = listener;
	}

	public static class MyViewHolder extends RecyclerView.ViewHolder {
		TextView tvNome, tvMercadoria, tvMotorista, tvPlaca, tvTelefone, tvPedido, tvConferente, tvNota;
		Button btnLiberado, btnDescarregou, btnApagar;
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
			btnApagar = itemView.findViewById(R.id.btnApagar);
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

		// Configuração dos textos com proteção contra null
		holder.tvNome.setText(fornecedor.getNome() != null ? fornecedor.getNome() : "");
		holder.tvMercadoria.setText(fornecedor.getMercadoria() != null ? fornecedor.getMercadoria() : "");

		// Proteção contra null para todos os campos
		String motorista = fornecedor.getMotorista() != null ? fornecedor.getMotorista() : "";
		String placa = fornecedor.getPlaca() != null ? fornecedor.getPlaca() : "";
		String telefone = fornecedor.getTelefone() != null ? fornecedor.getTelefone() : "";
		String pedido = fornecedor.getPedido() != null ? fornecedor.getPedido() : "";
		String numeroNota = fornecedor.getNumeroNota() != null ? fornecedor.getNumeroNota() : "";
		String conferente = fornecedor.getConferente() != null ? fornecedor.getConferente() : "";

		holder.tvMotorista.setText(motorista.isEmpty() ? "" : "Motorista: " + motorista);
		holder.tvPlaca.setText(placa.isEmpty() ? "" : "Placa: " + formatarPlaca(placa));
		holder.tvTelefone.setText(telefone.isEmpty() ? "" : "Tel: " + formatarTelefone(telefone));
		holder.tvPedido.setText(pedido.isEmpty() ? "" : "N° PEDIDO: " + pedido);
		holder.tvNota.setText(numeroNota.isEmpty() ? "" : "N° NOTA: " + numeroNota);
		holder.tvConferente.setText(conferente.isEmpty() ? "" : conferente);

		// Configura visibilidade dos campos
		holder.tvMotorista.setVisibility(motorista.isEmpty() ? View.GONE : View.VISIBLE);
		holder.tvPlaca.setVisibility(placa.isEmpty() ? View.GONE : View.VISIBLE);
		holder.tvTelefone.setVisibility(telefone.isEmpty() ? View.GONE : View.VISIBLE);
		holder.tvPedido.setVisibility(pedido.isEmpty() ? View.GONE : View.VISIBLE);
		holder.tvNota.setVisibility(numeroNota.isEmpty() ? View.GONE : View.VISIBLE);
		holder.tvConferente.setVisibility(conferente.isEmpty() ? View.GONE : View.VISIBLE);

		// Configura cores por status
		int corCard;
		switch (fornecedor.getStatus() != null ? fornecedor.getStatus() : "aguardando") {
		case "subiu":
			corCard = ContextCompat.getColor(context, R.color.status_subiu);
			break;
		case "foi_embora":
			corCard = ContextCompat.getColor(context, R.color.status_foi_embora);
			break;
		default:
			corCard = pedido.trim().isEmpty() ? ContextCompat.getColor(context, R.color.status_nao_agendado)
					: ContextCompat.getColor(context, R.color.status_aguardando);
		}
		holder.cardHeader.setBackgroundColor(corCard);

		// Configuração dinâmica dos botões - MODIFICADO PARA INTERAÇÃO INDEPENDENTE
		holder.btnLiberado.setVisibility(View.VISIBLE);
		holder.btnDescarregou.setVisibility(View.VISIBLE);
		holder.btnApagar.setVisibility(View.VISIBLE);

		// Apenas um botão visível por vez, mas agora qualquer card pode ser manipulado
		if (fornecedor.getStatus() != null) {
			switch (fornecedor.getStatus()) {
			case "subiu":
				holder.btnLiberado.setVisibility(View.GONE);
				holder.btnApagar.setVisibility(View.GONE);
				break;
			case "foi_embora":
				holder.btnLiberado.setVisibility(View.GONE);
				holder.btnDescarregou.setVisibility(View.GONE);
				break;
			default: // "aguardando"
				holder.btnDescarregou.setVisibility(View.GONE);
				holder.btnApagar.setVisibility(View.GONE);
			}
		}

		// Listeners dos botões
		holder.btnLiberado.setOnClickListener(v -> mostrarDialogoConferente(fornecedor, position));
		holder.btnDescarregou.setOnClickListener(v -> registrarSaida(fornecedor, position));
		holder.btnApagar.setOnClickListener(v -> listener.onApagarClick(fornecedor, position));

		holder.itemView.setOnLongClickListener(v -> {
			showMenuOptions(v, fornecedor, position);
			return true;
		});
	}

	private void registrarSaida(Fornecedor fornecedor, int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Confirmar Saída");
		builder.setMessage("Deseja registrar a saída deste fornecedor?");

		builder.setPositiveButton("Sim", (dialog, which) -> {
			String dataHora = sdfDisplay.format(new Date());
			fornecedor.setHoraSaida(dataHora);
			fornecedor.setStatus("foi_embora");

			if (listener != null) {
				listener.onFornecedorAtualizado(fornecedor, position);
			}
			notifyItemChanged(position);
		});

		builder.setNegativeButton("Não", null);
		builder.show();
	}

	private void mostrarDialogoConferente(Fornecedor fornecedor, int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Autorizar Fornecedor");

		View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_conferente, null);
		EditText etConferente = dialogView.findViewById(R.id.etConferente);
		builder.setView(dialogView);

		builder.setPositiveButton("Confirmar", (dialog, which) -> {
			String nomeConferente = etConferente.getText().toString().trim();
			if (!nomeConferente.isEmpty()) {
				String dataHora = sdfDisplay.format(new Date());

				fornecedor.setConferente("Liberado por: " + nomeConferente);
				fornecedor.setHoraSubida(dataHora);
				fornecedor.setStatus("subiu");

				if (listener != null) {
					listener.onFornecedorAtualizado(fornecedor, position);
				}
				notifyItemChanged(position);
			} else {
				Toast.makeText(context, "Digite o nome do conferente", Toast.LENGTH_SHORT).show();
			}
		});

		builder.setNegativeButton("Cancelar", null);
		builder.show();
	}

	private void showMenuOptions(View view, Fornecedor fornecedor, int position) {
		PopupMenu popup = new PopupMenu(context, view);
		popup.inflate(R.menu.fornecedor_menu);

		popup.setOnMenuItemClickListener(item -> {
			if (item.getItemId() == R.id.menu_info) {
				listener.onMostrarInfoClick(fornecedor);
				return true;
			} else if (item.getItemId() == R.id.menu_editar) {
				listener.onEditarClick(fornecedor, position);
				return true;
			} else if (item.getItemId() == R.id.menu_adicionar_info) {
				mostrarDialogoAdicionarInfo(fornecedor, position);
				return true;
			}
			return false;
		});
		popup.show();
	}

	private void mostrarDialogoAdicionarInfo(Fornecedor fornecedor, int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Adicionar Informações");

		View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_adicionar_info, null);
		EditText etInfo = dialogView.findViewById(R.id.etInfo);
		etInfo.setText(fornecedor.getObservacoesPortaria() != null ? fornecedor.getObservacoesPortaria() : "");

		builder.setView(dialogView);
		builder.setPositiveButton("Salvar", (dialog, which) -> {
			String info = etInfo.getText().toString().trim();
			fornecedor.setObservacoesPortaria(info);
			listener.onAdicionarInfoClick(fornecedor, position);
		});

		builder.setNegativeButton("Cancelar", null);
		builder.show();
	}

	private String formatarTelefone(String telefone) {
		if (TextUtils.isEmpty(telefone))
			return "";

		String numeros = telefone.replaceAll("[^0-9]", "");
		if (numeros.length() == 11) {
			return String.format("(%s) %s.%s-%s", numeros.substring(0, 2), numeros.substring(2, 3),
					numeros.substring(3, 7), numeros.substring(7));
		} else if (numeros.length() == 10) {
			return String.format("(%s) %s-%s", numeros.substring(0, 2), numeros.substring(2, 6), numeros.substring(6));
		}
		return telefone;
	}

	private String formatarPlaca(String placa) {
		return (placa != null && placa.length() == 7) ? placa.substring(0, 3) + "-" + placa.substring(3) : placa;
	}

	@Override
	public int getItemCount() {
		return listaFornecedores != null ? listaFornecedores.size() : 0;
	}
}