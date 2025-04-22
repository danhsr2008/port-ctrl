package com.aide.controle.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "controle_fornecedores.db";
	private static final int DATABASE_VERSION = 2;

	public static final String TABLE_FORNECEDORES = "fornecedores";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NOME = "nome";
	public static final String COLUMN_PLACA = "placa";
	public static final String COLUMN_MERCADORIA = "mercadoria";
	public static final String COLUMN_STATUS = "status";
	public static final String COLUMN_MOTORISTA = "motorista";
	public static final String COLUMN_TELEFONE = "telefone";
	public static final String COLUMN_PEDIDO = "pedido";
	public static final String COLUMN_NUMERO_NOTA = "numero_nota";
	public static final String COLUMN_CONFERENTE = "conferente";
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
				+ COLUMN_MOTORISTA + " TEXT, " + COLUMN_TELEFONE + " TEXT, " + COLUMN_PEDIDO + " TEXT, "
				+ COLUMN_NUMERO_NOTA + " TEXT, " + COLUMN_CONFERENTE + " TEXT, " + COLUMN_DATA_CHEGADA + " TEXT, "
				+ COLUMN_HORA_SUBIDA + " TEXT, " + COLUMN_HORA_SAIDA + " TEXT, " + COLUMN_OBSERVACOES + " TEXT)";

		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 2) {
			// Atualizações para a versão 2
			db.execSQL("ALTER TABLE " + TABLE_FORNECEDORES + " ADD COLUMN " + COLUMN_OBSERVACOES + " TEXT");
		}
	}
}