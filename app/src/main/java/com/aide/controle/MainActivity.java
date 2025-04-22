package com.aide.controle;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aide.controle.adapter.FornecedorAdapter;
import com.aide.controle.model.DatabaseHelper;
import com.aide.controle.model.Fornecedor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements FornecedorAdapter.OnFornecedorClickListener {
	private static final String TAG = "MainActivity";
	private List<Fornecedor> fornecedores = new ArrayList<>();
	private FornecedorAdapter adapter;
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	private SimpleDateFormat sdfDatabase = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	private SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dbHelper = new DatabaseHelper(this);
		database = dbHelper.getWritableDatabase();

		RecyclerView recyclerView = findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

		adapter = new FornecedorAdapter(fornecedores, this, this);
		recyclerView.setAdapter(adapter);

		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(v -> showAddDialog());

		if (getSupportActionBar() != null) {
			getSupportActionBar().hide();
		}

		carregarFornecedores();
	}

	private void carregarFornecedores() {
		fornecedores.clear();
		Cursor cursor = database.query(DatabaseHelper.TABLE_FORNECEDORES, null, null, null, null, null,
				DatabaseHelper.COLUMN_ID + " DESC");

		while (cursor.moveToNext()) {
			try {
				Fornecedor fornecedor = new Fornecedor(
						cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOME)),
						cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PLACA)),
						cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MERCADORIA)));

				fornecedor.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
				fornecedor.setMotorista(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MOTORISTA)));
				fornecedor.setTelefone(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TELEFONE)));
				fornecedor.setPedido(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PEDIDO)));
				fornecedor.setNumeroNota(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NUMERO_NOTA)));
				fornecedor.setConferente(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONFERENTE)));
				fornecedor.setStatus(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS)));
				fornecedor.setDataChegada(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATA_CHEGADA)));
				fornecedor.setHoraSubida(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HORA_SUBIDA)));
				fornecedor.setHoraSaida(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HORA_SAIDA)));
				fornecedor.setObservacoesPortaria(
						cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_OBSERVACOES)));

				fornecedores.add(fornecedor);
			} catch (Exception e) {
				Log.e(TAG, "Erro ao carregar fornecedor: " + e.getMessage());
			}
		}
		cursor.close();
		adapter.notifyDataSetChanged();
	}

	private void showAddDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Adicionar Fornecedor");

		View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_fornecedor, null);
		EditText etNome = dialogView.findViewById(R.id.etNome);
		EditText etPlaca = dialogView.findViewById(R.id.etPlaca);
		EditText etMercadoria = dialogView.findViewById(R.id.etMercadoria);
		EditText etMotorista = dialogView.findViewById(R.id.etMotorista);
		EditText etTelefone = dialogView.findViewById(R.id.etTelefone);
		EditText etPedido = dialogView.findViewById(R.id.etPedido);
		EditText etNumeroNota = dialogView.findViewById(R.id.etNumeroNota);

		builder.setView(dialogView);
		builder.setPositiveButton("Salvar", (dialog, which) -> {
			Fornecedor novoFornecedor = new Fornecedor(etNome.getText().toString(), etPlaca.getText().toString(),
					etMercadoria.getText().toString());
			novoFornecedor.setMotorista(etMotorista.getText().toString());
			novoFornecedor.setTelefone(etTelefone.getText().toString());
			novoFornecedor.setPedido(etPedido.getText().toString());
			novoFornecedor.setNumeroNota(etNumeroNota.getText().toString());
			novoFornecedor.setDataChegada(sdfDatabase.format(new Date()));
			novoFornecedor.setStatus("aguardando");

			salvarFornecedor(novoFornecedor);
		});
		builder.setNegativeButton("Cancelar", null);
		builder.show();
	}

	private void salvarFornecedor(Fornecedor fornecedor) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_NOME, fornecedor.getNome());
		values.put(DatabaseHelper.COLUMN_PLACA, fornecedor.getPlaca());
		values.put(DatabaseHelper.COLUMN_MERCADORIA, fornecedor.getMercadoria());
		values.put(DatabaseHelper.COLUMN_MOTORISTA, fornecedor.getMotorista());
		values.put(DatabaseHelper.COLUMN_TELEFONE, fornecedor.getTelefone());
		values.put(DatabaseHelper.COLUMN_PEDIDO, fornecedor.getPedido());
		values.put(DatabaseHelper.COLUMN_NUMERO_NOTA, fornecedor.getNumeroNota());
		values.put(DatabaseHelper.COLUMN_STATUS, fornecedor.getStatus());
		values.put(DatabaseHelper.COLUMN_DATA_CHEGADA, fornecedor.getDataChegada());

		long id = database.insert(DatabaseHelper.TABLE_FORNECEDORES, null, values);
		if (id != -1) {
			fornecedor.setId((int) id);
			fornecedores.add(0, fornecedor);
			adapter.notifyItemInserted(0);
			Toast.makeText(this, "Fornecedor adicionado", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onEditarClick(Fornecedor fornecedor, int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Editar Fornecedor");

		View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_fornecedor, null);
		EditText etNome = dialogView.findViewById(R.id.etNome);
		EditText etPlaca = dialogView.findViewById(R.id.etPlaca);
		EditText etMercadoria = dialogView.findViewById(R.id.etMercadoria);
		EditText etMotorista = dialogView.findViewById(R.id.etMotorista);
		EditText etTelefone = dialogView.findViewById(R.id.etTelefone);
		EditText etPedido = dialogView.findViewById(R.id.etPedido);
		EditText etNumeroNota = dialogView.findViewById(R.id.etNumeroNota);

		etNome.setText(fornecedor.getNome());
		etPlaca.setText(fornecedor.getPlaca());
		etMercadoria.setText(fornecedor.getMercadoria());
		etMotorista.setText(fornecedor.getMotorista());
		etTelefone.setText(fornecedor.getTelefone());
		etPedido.setText(fornecedor.getPedido());
		etNumeroNota.setText(fornecedor.getNumeroNota());

		builder.setView(dialogView);
		builder.setPositiveButton("Salvar", (dialog, which) -> {
			fornecedor.setNome(etNome.getText().toString());
			fornecedor.setPlaca(etPlaca.getText().toString());
			fornecedor.setMercadoria(etMercadoria.getText().toString());
			fornecedor.setMotorista(etMotorista.getText().toString());
			fornecedor.setTelefone(etTelefone.getText().toString());
			fornecedor.setPedido(etPedido.getText().toString());
			fornecedor.setNumeroNota(etNumeroNota.getText().toString());

			atualizarFornecedor(fornecedor, position);
		});
		builder.setNegativeButton("Cancelar", null);
		builder.show();
	}

	private void atualizarFornecedor(Fornecedor fornecedor, int position) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_NOME, fornecedor.getNome());
		values.put(DatabaseHelper.COLUMN_PLACA, fornecedor.getPlaca());
		values.put(DatabaseHelper.COLUMN_MERCADORIA, fornecedor.getMercadoria());
		values.put(DatabaseHelper.COLUMN_MOTORISTA, fornecedor.getMotorista());
		values.put(DatabaseHelper.COLUMN_TELEFONE, fornecedor.getTelefone());
		values.put(DatabaseHelper.COLUMN_PEDIDO, fornecedor.getPedido());
		values.put(DatabaseHelper.COLUMN_NUMERO_NOTA, fornecedor.getNumeroNota());

		int rows = database.update(DatabaseHelper.TABLE_FORNECEDORES, values, DatabaseHelper.COLUMN_ID + " = ?",
				new String[] { String.valueOf(fornecedor.getId()) });

		if (rows > 0) {
			adapter.notifyItemChanged(position);
			Toast.makeText(this, "Fornecedor atualizado", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onApagarClick(Fornecedor fornecedor, int position) {
		new AlertDialog.Builder(this).setTitle("Confirmar exclusão")
				.setMessage("Deseja realmente apagar " + fornecedor.getNome() + "?")
				.setPositiveButton("Sim", (dialog, which) -> {
					int rows = database.delete(DatabaseHelper.TABLE_FORNECEDORES, DatabaseHelper.COLUMN_ID + " = ?",
							new String[] { String.valueOf(fornecedor.getId()) });

					if (rows > 0) {
						fornecedores.remove(position);
						adapter.notifyItemRemoved(position);
						Toast.makeText(this, "Fornecedor removido", Toast.LENGTH_SHORT).show();
					}
				}).setNegativeButton("Não", null).show();
	}

	@Override
	public void onAdicionarInfoClick(Fornecedor fornecedor, int position) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_OBSERVACOES, fornecedor.getObservacoesPortaria());

		int rows = database.update(DatabaseHelper.TABLE_FORNECEDORES, values, DatabaseHelper.COLUMN_ID + " = ?",
				new String[] { String.valueOf(fornecedor.getId()) });

		if (rows > 0) {
			adapter.notifyItemChanged(position);
			Toast.makeText(this, "Informações adicionadas", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onFornecedorAtualizado(Fornecedor fornecedor, int position) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_STATUS, fornecedor.getStatus());
		values.put(DatabaseHelper.COLUMN_CONFERENTE, fornecedor.getConferente());
		values.put(DatabaseHelper.COLUMN_HORA_SUBIDA, fornecedor.getHoraSubida());
		values.put(DatabaseHelper.COLUMN_HORA_SAIDA, fornecedor.getHoraSaida());

		database.update(DatabaseHelper.TABLE_FORNECEDORES, values, DatabaseHelper.COLUMN_ID + " = ?",
				new String[] { String.valueOf(fornecedor.getId()) });
	}

	@Override
	public void onMostrarInfoClick(Fornecedor fornecedor) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Informações Completas");

		StringBuilder info = new StringBuilder();
		info.append("Nome: ").append(fornecedor.getNome()).append("\n\n");
		info.append("Mercadoria: ").append(fornecedor.getMercadoria()).append("\n\n");
		info.append("Placa: ").append(fornecedor.getPlaca()).append("\n\n");
		info.append("Motorista: ").append(fornecedor.getMotorista()).append("\n\n");
		info.append("Telefone: ").append(fornecedor.getTelefone()).append("\n\n");
		info.append("Pedido: ").append(fornecedor.getPedido()).append("\n\n");
		info.append("Nota: ").append(fornecedor.getNumeroNota()).append("\n\n");
		info.append("Chegada: ").append(fornecedor.getDataChegada()).append("\n\n");
		info.append("Subida: ").append(fornecedor.getHoraSubida()).append("\n\n");
		info.append("Saída: ").append(fornecedor.getHoraSaida()).append("\n\n");
		info.append("Conferente: ").append(fornecedor.getConferente()).append("\n\n");
		info.append("Observações: ").append(fornecedor.getObservacoesPortaria());

		builder.setMessage(info.toString());
		builder.setPositiveButton("OK", null);
		builder.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (database != null) {
			database.close();
		}
		if (dbHelper != null) {
			dbHelper.close();
		}
	}
}