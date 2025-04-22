package com.aide.controle.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.File;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "controle_fornecedores_v3.db";
    private static final int DATABASE_VERSION = 4;
    private Context context;

    // Nomes das tabelas e colunas
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
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    private void createTables(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            // Verifica se a tabela já existe
            cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", 
                new String[]{TABLE_FORNECEDORES});
            
            if (cursor != null && !cursor.moveToFirst()) {
                // Só cria se não existir
                String CREATE_TABLE = "CREATE TABLE " + TABLE_FORNECEDORES + "(" 
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_NOME + " TEXT NOT NULL, "
                    + COLUMN_PLACA + " TEXT, "
                    + COLUMN_MERCADORIA + " TEXT NOT NULL, "
                    + COLUMN_STATUS + " TEXT DEFAULT 'aguardando', "
                    + COLUMN_MOTORISTA + " TEXT, "
                    + COLUMN_TELEFONE + " TEXT, "
                    + COLUMN_PEDIDO + " TEXT, "
                    + COLUMN_CONFERENTE + " TEXT, "
                    + COLUMN_NUMERO_NOTA + " TEXT, "
                    + COLUMN_DATA_CHEGADA + " TEXT, "
                    + COLUMN_HORA_SUBIDA + " TEXT, "
                    + COLUMN_HORA_SAIDA + " TEXT, "
                    + COLUMN_OBSERVACOES + " TEXT)";

                db.execSQL(CREATE_TABLE);
                Log.d(TAG, "Tabela " + TABLE_FORNECEDORES + " criada com sucesso");
            } else {
                Log.d(TAG, "Tabela " + TABLE_FORNECEDORES + " já existe");
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao verificar/criar tabela: " + e.getMessage());
            repairDatabase();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            // Migração incremental segura
            if (oldVersion < 2) {
                // Atualizações para versão 2
                db.execSQL("ALTER TABLE " + TABLE_FORNECEDORES + 
                    " ADD COLUMN " + COLUMN_CONFERENTE + " TEXT DEFAULT ''");
            }
            
            if (oldVersion < 3) {
                // Atualizações para versão 3
                db.execSQL("ALTER TABLE " + TABLE_FORNECEDORES + 
                    " ADD COLUMN " + COLUMN_OBSERVACOES + " TEXT DEFAULT ''");
            }
            
            if (oldVersion < 4) {
                // Atualizações para versão 4
                db.execSQL("ALTER TABLE " + TABLE_FORNECEDORES + 
                    " ADD COLUMN " + COLUMN_HORA_SUBIDA + " TEXT DEFAULT ''");
                db.execSQL("ALTER TABLE " + TABLE_FORNECEDORES + 
                    " ADD COLUMN " + COLUMN_HORA_SAIDA + " TEXT DEFAULT ''");
            }
            
            Log.d(TAG, "Banco atualizado da versão " + oldVersion + " para " + newVersion);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao atualizar banco: " + e.getMessage());
            repairDatabase();
        }
    }

    public synchronized SQLiteDatabase getSafeWritableDatabase() {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            
            // Teste de conexão seguro
            Cursor cursor = db.rawQuery("SELECT 1", null);
            if (cursor != null) {
                cursor.close();
                return db;
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao acessar banco: " + e.getMessage());
            if (db != null && db.isOpen()) {
                db.close();
            }
            repairDatabase();
        }
        
        // Segunda tentativa após reparo
        try {
            db = this.getWritableDatabase();
            return db;
        } catch (Exception e) {
            Log.e(TAG, "Erro persistente ao acessar banco: " + e.getMessage());
            return null;
        }
    }

    public void repairDatabase() {
        SQLiteDatabase db = null;
        try {
            close();
            
            File dbFile = context.getDatabasePath(DATABASE_NAME);
            if (dbFile.exists()) {
                boolean deleted = dbFile.delete();
                Log.d(TAG, "Tentativa de deletar banco: " + (deleted ? "Sucesso" : "Falha"));
            }
            
            // Recria o banco
            db = this.getWritableDatabase();
            createTables(db);
            Log.d(TAG, "Banco de dados reparado com sucesso");
        } catch (Exception e) {
            Log.e(TAG, "Erro ao reparar banco: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public boolean checkDatabaseIntegrity() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("PRAGMA integrity_check;", null);
            
            if (cursor != null && cursor.moveToFirst()) {
                String result = cursor.getString(0);
                return "ok".equalsIgnoreCase(result);
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao verificar integridade: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }
}