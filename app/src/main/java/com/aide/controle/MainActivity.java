package com.aide.controle;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {
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

		} catch (Exception e) {
			Log.e(TAG, "Erro na inicialização: " + e.getMessage());
			Toast.makeText(this, "Erro ao abrir o banco de dados", Toast.LENGTH_LONG).show();
			finish();
		}
	}

	private void showAddDialog() {
		try {
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
				try {
					Fornecedor novoFornecedor = new Fornecedor(etNome.getText().toString(),
							etPlaca.getText().toString(), etMercadoria.getText().toString());
					novoFornecedor.setMotorista(etMotorista.getText().toString());
					novoFornecedor.setTelefone(etTelefone.getText().toString());
					novoFornecedor.setPedido(etPedido.getText().toString());
					novoFornecedor.setNumeroNota(etNumeroNota.getText().toString());

					salvarFornecedor(novoFornecedor);
				} catch (Exception e) {
					Log.e(TAG, "Erro ao criar fornecedor: " + e.getMessage());
					Toast.makeText(MainActivity.this, "Erro ao salvar fornecedor", Toast.LENGTH_SHORT).show();
				}
			});
			builder.setNegativeButton("Cancelar", null);
			builder.show();
		} catch (Exception e) {
			Log.e(TAG, "Erro no diálogo: " + e.getMessage());
		}
	}

	@Override
	public void onEditarClick(Fornecedor fornecedor, int position) {
		try {
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

			// Preenche os campos com os valores atuais
			etNome.setText(fornecedor.getNome());
			etPlaca.setText(fornecedor.getPlaca());
			etMercadoria.setText(fornecedor.getMercadoria());
			etMotorista.setText(fornecedor.getMotorista());
			etTelefone.setText(fornecedor.getTelefone());
			etPedido.setText(fornecedor.getPedido());
			etNumeroNota.setText(fornecedor.getNumeroNota());

			builder.setView(dialogView);
			builder.setPositiveButton("Salvar", (dialog, which) -> {
				try {
					// Atualiza o fornecedor com os novos valores
					fornecedor.setNome(etNome.getText().toString());
					fornecedor.setPlaca(etPlaca.getText().toString());
					fornecedor.setMercadoria(etMercadoria.getText().toString());
					fornecedor.setMotorista(etMotorista.getText().toString());
					fornecedor.setTelefone(etTelefone.getText().toString());
					fornecedor.setPedido(etPedido.getText().toString());
					fornecedor.setNumeroNota(etNumeroNota.getText().toString());

					atualizarFornecedor(fornecedor, position);
				} catch (Exception e) {
					Log.e(TAG, "Erro ao editar fornecedor: " + e.getMessage());
					Toast.makeText(MainActivity.this, "Erro ao editar fornecedor", Toast.LENGTH_SHORT).show();
				}
			});
			builder.setNegativeButton("Cancelar", null);
			builder.show();
		} catch (Exception e) {
			Log.e(TAG, "Erro na edição: " + e.getMessage());
		}
	}

	private void atualizarFornecedor(Fornecedor fornecedor, int position) {
		try {
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.COLUMN_NOME, fornecedor.getNome());
			values.put(DatabaseHelper.COLUMN_PLACA, fornecedor.getPlaca());
			values.put(DatabaseHelper.COLUMN_MERCADORIA, fornecedor.getMercadoria());
			values.put(DatabaseHelper.COLUMN_MOTORISTA, fornecedor.getMotorista());
			values.put(DatabaseHelper.COLUMN_TELEFONE, fornecedor.getTelefone());
			values.put(DatabaseHelper.COLUMN_PEDIDO, fornecedor.getPedido());
			values.put(DatabaseHelper.COLUMN_NUMERO_NOTA, fornecedor.getNumeroNota());

			int rowsAffected = database.update(DatabaseHelper.TABLE_FORNECEDORES, values,
					DatabaseHelper.COLUMN_ID + " = ?", new String[] { String.valueOf(fornecedor.getId()) });

			if (rowsAffected > 0) {
				adapter.notifyItemChanged(position);
				Toast.makeText(this, "Fornecedor atualizado com sucesso", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "Falha ao atualizar fornecedor", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Log.e(TAG, "Erro ao atualizar fornecedor: " + e.getMessage());
			Toast.makeText(this, "Erro ao atualizar fornecedor", Toast.LENGTH_SHORT).show();
		}
	}

	private void salvarFornecedor(Fornecedor fornecedor) {
		try {
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.COLUMN_NOME, fornecedor.getNome());
			values.put(DatabaseHelper.COLUMN_PLACA, fornecedor.getPlaca());
			values.put(DatabaseHelper.COLUMN_MERCADORIA, fornecedor.getMercadoria());
			values.put(DatabaseHelper.COLUMN_MOTORISTA, fornecedor.getMotorista());
			values.put(DatabaseHelper.COLUMN_TELEFONE, fornecedor.getTelefone());
			values.put(DatabaseHelper.COLUMN_PEDIDO, fornecedor.getPedido());
			values.put(DatabaseHelper.COLUMN_NUMERO_NOTA, fornecedor.getNumeroNota());
			values.put(DatabaseHelper.COLUMN_STATUS, "aguardando");

			// Garante a data atual se não estiver definida
			if (fornecedor.getDataChegada() == null || fornecedor.getDataChegada().isEmpty()) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
				fornecedor.setDataChegada(sdf.format(new Date()));
			}
			values.put(DatabaseHelper.COLUMN_DATA_CHEGADA, fornecedor.getDataChegada());

			long id = database.insert(DatabaseHelper.TABLE_FORNECEDORES, null, values);

			if (id != -1) {
				fornecedor.setId((int) id);
				fornecedores.add(0, fornecedor);
				adapter.notifyItemInserted(0);
			} else {
				Toast.makeText(this, "Falha ao salvar fornecedor", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Log.e(TAG, "Erro no banco: " + e.getMessage());
			Toast.makeText(this, "Erro ao salvar fornecedor", Toast.LENGTH_SHORT).show();
		}
	}

	private void carregarFornecedores() {
		try {
			fornecedores.clear();
			Cursor cursor = database.query(DatabaseHelper.TABLE_FORNECEDORES, null, null, null, null, null,
					DatabaseHelper.COLUMN_ID + " DESC");

			if (cursor != null) {
				while (cursor.moveToNext()) {
					Fornecedor f = new Fornecedor(
							cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOME)),
							cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PLACA)),
							cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MERCADORIA)));

					f.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
					f.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS)));
					f.setMotorista(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MOTORISTA)));
					f.setTelefone(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TELEFONE)));
					f.setPedido(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PEDIDO)));
					f.setConferente(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONFERENTE)));
					f.setNumeroNota(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NUMERO_NOTA)));
					f.setDataChegada(
							cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATA_CHEGADA)));
					f.setHoraSubida(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HORA_SUBIDA)));
					f.setHoraSaida(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HORA_SAIDA)));
					f.setObservacoesPortaria(
							cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OBSERVACOES)));

					fornecedores.add(f);
				}
				cursor.close();
			}
			adapter.notifyDataSetChanged();
		} catch (Exception e) {
			Log.e(TAG, "Erro ao carregar dados: " + e.getMessage());
			Toast.makeText(this, "Erro ao carregar fornecedores", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onFornecedorAtualizado(Fornecedor fornecedor, int position) {
		try {
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.COLUMN_STATUS, fornecedor.getStatus());
			values.put(DatabaseHelper.COLUMN_CONFERENTE, fornecedor.getConferente());
			values.put(DatabaseHelper.COLUMN_HORA_SUBIDA, fornecedor.getHoraSubida());
			values.put(DatabaseHelper.COLUMN_HORA_SAIDA, fornecedor.getHoraSaida());

			database.update(DatabaseHelper.TABLE_FORNECEDORES, values, DatabaseHelper.COLUMN_ID + " = ?",
					new String[] { String.valueOf(fornecedor.getId()) });
			adapter.notifyItemChanged(position);
		} catch (Exception e) {
			Log.e(TAG, "Erro ao atualizar: " + e.getMessage());
			Toast.makeText(this, "Erro ao atualizar status", Toast.LENGTH_SHORT).show();
		}
	}
}
