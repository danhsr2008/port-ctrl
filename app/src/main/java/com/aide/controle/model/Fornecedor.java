package com.aide.controle.model;

public class Fornecedor {
	private int id;
	private String nome;
	private String placa;
	private String mercadoria;
	private String status;
	private String motorista;
	private String telefone;
	private String pedido;
	private String conferente;
	private String numeroNota;
	private String dataChegada;
	private String horaSubida;
	private String horaSaida;
	private String observacoesPortaria;

	public Fornecedor(String nome, String placa, String mercadoria) {
		this.nome = nome;
		this.placa = placa;
		this.mercadoria = mercadoria;
		this.status = "aguardando";
		this.motorista = "";
		this.telefone = "";
		this.pedido = "";
		this.conferente = "";
		this.numeroNota = "";
		this.dataChegada = "";
		this.horaSubida = "";
		this.horaSaida = "";
		this.observacoesPortaria = "";
	}

	// Getters e Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public String getMercadoria() {
		return mercadoria;
	}

	public void setMercadoria(String mercadoria) {
		this.mercadoria = mercadoria;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMotorista() {
		return motorista;
	}

	public void setMotorista(String motorista) {
		this.motorista = motorista;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getPedido() {
		return pedido;
	}

	public void setPedido(String pedido) {
		this.pedido = pedido;
	}

	public String getConferente() {
		return conferente;
	}

	public void setConferente(String conferente) {
		this.conferente = conferente;
	}

	public String getNumeroNota() {
		return numeroNota;
	}

	public void setNumeroNota(String numeroNota) {
		this.numeroNota = numeroNota;
	}

	public String getDataChegada() {
		return dataChegada;
	}

	public void setDataChegada(String dataChegada) {
		this.dataChegada = dataChegada;
	}

	public String getHoraSubida() {
		return horaSubida;
	}

	public void setHoraSubida(String horaSubida) {
		this.horaSubida = horaSubida;
	}

	public String getHoraSaida() {
		return horaSaida;
	}

	public void setHoraSaida(String horaSaida) {
		this.horaSaida = horaSaida;
	}

	public String getObservacoesPortaria() {
		return observacoesPortaria;
	}

	public void setObservacoesPortaria(String observacoesPortaria) {
		this.observacoesPortaria = observacoesPortaria;
	}

	@Override
	public String toString() {
		return "Fornecedor{" + "id=" + id + ", nome='" + nome + '\'' + ", placa='" + placa + '\'' + ", mercadoria='"
				+ mercadoria + '\'' + ", status='" + status + '\'' + '}';
	}
}
