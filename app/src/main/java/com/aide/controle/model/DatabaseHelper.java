package com.aide.controle.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String TAG = "DatabaseHelper";
	private static final String DATABASE_NAME = "controle_fornecedores.db";
	private static final int DATABASE_VERSION = 1; // Versão inicial simplificada

	// Tabela e colunas
	public static final String TABLE_FORNECEDORES = "fornecedores";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NOME = "nome";
	public static final String COLUMN_PLACA = "placa";
	public static final String COLUMN_MERCADORIA = "mercadoria";
	public static final String COLUMN_STATUS = "status";
	public static final String COLUMN_MOTORISTA = "motorista";
	public static final String COLUMN_TELEFONE = "telefone";
	public static final String COLUMN_PEDIDO = "pedido";
	public static final String COLUMN_CONFERENTE = "conferente";
	public static final String COLUMN_NUMERO_NOTA = "numero_nota";
	public static final String COLUMN_DATA_CHEGADA = "data_chegada";
	public static final String COLUMN_HORA_SUBIDA = "hora_subida";
	public static final String COLUMN_HORA_SAIDA = "hora_saida";
	public static final String COLUMN_OBSERVACOES = "observacoes";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TABLE = "CREATE TABLE " + TABLE_FORNECEDORES + "(" + COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NOME + " TEXT NOT NULL, " + COLUMN_PLACA + " TEXT, "
				+ COLUMN_MERCADORIA + " TEXT NOT NULL, " + COLUMN_STATUS + " TEXT DEFAULT 'aguardando', "
				+ COLUMN_DATA_CHEGADA + " TEXT DEFAULT (strftime('%d/%m/%Y', 'now')), " + COLUMN_HORA_SUBIDA + " TEXT, "
				+ COLUMN_HORA_SAIDA + " TEXT)";
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			// ATENÇÃO: Comentado para evitar crashes durante desenvolvimento
			// db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORNECEDORES);
			// onCreate(db);

			// Migração segura (exemplo para versão 2):
			// if (oldVersion < 2) {
			//     db.execSQL("ALTER TABLE " + TABLE_FORNECEDORES 
			//          + " ADD COLUMN " + COLUMN_OBSERVACOES + " TEXT DEFAULT ''");
			// }

			Log.d(TAG, "Banco de dados atualizado da versão " + oldVersion + " para " + newVersion);
		} catch (Exception e) {
			Log.e(TAG, "Erro ao atualizar banco: " + e.getMessage());
		}
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Evitar crashes em downgrade
		onUpgrade(db, oldVersion, newVersion);
	}
}
