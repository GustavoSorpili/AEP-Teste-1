package org.aep.observacao.model;

public class Categoria {
    private String nome;
    private int slaDias; // SLA in days based on priority, but perhaps per category

    public Categoria(String nome, int slaDias) {
        this.nome = nome;
        this.slaDias = slaDias;
    }

    public String getNome() {
        return nome;
    }

    public int getSlaDias() {
        return slaDias;
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        if (slaDias != categoria.slaDias) return false;
        return nome != null ? nome.equals(categoria.nome) : categoria.nome == null;
    }

    @Override
    public int hashCode() {
        int result = nome != null ? nome.hashCode() : 0;
        result = 31 * result + slaDias;
        return result;
    }
}